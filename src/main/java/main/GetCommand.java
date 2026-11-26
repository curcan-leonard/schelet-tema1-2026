package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CommandInput;
import fileio.CommandParametersInput;
import model.DataType;
import model.EnergyPoint;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class GetCommand {
    private GetCommand() {
    }

    static ObjectNode processGet(final ObjectMapper mapper,
                                 final MarketDataRepository repository,
                                 final CommandInput command) {
        CommandParametersInput parameters = command.getParameters();
        DataType type = DataType.valueOf(parameters.getDataType());
        boolean denied = "FREE".equals(parameters.getClientTier())
                && type == DataType.DAM_PRICE_PREDICTION;
        String timestamp = command.getTimestamp();
        String target = parameters.getTarget();

        if (target == null) {
            String from = parameters.getStart();
            String to = parameters.getEnd();
            List<EnergyPoint> points = denied ? List.of() : repository.getRange(type,
                    LocalDateTime.parse(from), LocalDateTime.parse(to));
            return buildResponse(mapper, timestamp, from, to, denied, points);
        }
        if ("LATEST".equals(target)) {
            return processLatest(mapper, timestamp, denied, repository.getLatest(type));
        }

        LocalDateTime from = LocalDateTime.parse(target);
        String to = from.plusMinutes(class_create.INTERVAL_MINUTES).toString();
        EnergyPoint point = null;
        if (!denied && class_create.isIntervalStart(from.toLocalTime())) {
            int index = class_create.calculateIntervalIndex(from.toLocalTime());
            point = repository.getPoint(type, from.toLocalDate(), index);
        }
        List<EnergyPoint> points = point == null ? List.of() : List.of(point);
        return buildResponse(mapper, timestamp, target, to, denied, points);
    }

    private static ObjectNode processLatest(final ObjectMapper mapper, final String timestamp,
                                            final boolean denied, final EnergyPoint point) {
        if (point == null) {
            return buildResponse(mapper, timestamp, null, null, denied, List.of());
        }
        long minutes = (long) (point.getIntervalIndex() - 1) * class_create.INTERVAL_MINUTES;
        LocalDateTime from = LocalDate.parse(point.getDate()).atStartOfDay().plusMinutes(minutes);
        String to = from.plusMinutes(class_create.INTERVAL_MINUTES).toString();
        return buildResponse(mapper, timestamp, from.toString(), to, denied, List.of(point));
    }

    private static ObjectNode buildResponse(final ObjectMapper mapper, final String timestamp,
                                            final String from, final String to,
                                            final boolean denied, final List<EnergyPoint> points) {
        ObjectNode response = mapper.createObjectNode();
        response.put("command", "get");
        response.put("timestamp", timestamp);
        response.put("from", from);
        response.put("to", to);

        if (denied) {
            response.put("status", "ERROR");
            response.put("message",
                    "Access denied: DAM_PRICE_PREDICTION requires PREMIUM subscription.");
        } else if (points.isEmpty()) {
            response.put("status", "WARNING");
            response.putNull("data");
            response.put("message", "Data is not yet available. Try again later.");
        } else {
            response.put("status", "SUCCESS");
            response.set("data", buildData(mapper, points));
        }
        return response;
    }

    private static ArrayNode buildData(final ObjectMapper mapper, final List<EnergyPoint> points) {
        Map<String, List<EnergyPoint>> pointsByDay = new LinkedHashMap<>();
        for (EnergyPoint point : points) {
            pointsByDay.computeIfAbsent(point.getDate(), date -> new ArrayList<>()).add(point);
        }

        ArrayNode result = mapper.createArrayNode();
        for (Map.Entry<String, List<EnergyPoint>> day : pointsByDay.entrySet()) {
            List<EnergyPoint> dayPoints = day.getValue();
            ObjectNode dayJson = result.addObject();
            dayJson.put("id", dayPoints.get(0).getType().name());
            dayJson.put("date", day.getKey());

            ArrayNode dayData = dayJson.putArray("data");
            for (EnergyPoint point : dayPoints) {
                ObjectNode pointJson = dayData.addObject();
                pointJson.put("interval", point.getInterval());
                pointJson.put("intervalIndex", point.getIntervalIndex());
                point.writeValuesToJson(pointJson);
            }
        }
        return result;
    }
}
