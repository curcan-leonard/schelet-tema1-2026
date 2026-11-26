package main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CommandInput;
import fileio.CommandParametersInput;
import fileio.MarketDataInput;
import model.DataType;
import model.EnergyPoint;

final class DataCommand {
    private DataCommand() {
    }

    static void processData(final CommandInput command, final MarketDataRepository repository,
                            final ArrayNode output, final boolean patch) {
        CommandParametersInput parameters = command.getParameters();
        for (MarketDataInput input : parameters.getData()) {
            DataType type = DataType.valueOf(patch ? parameters.getDataType() : input.id());
            EnergyPoint point = class_create.createPoint(type, input);
            String message = repository.validateAndStorePoint(point, patch);
            if (message != null) {
                ObjectNode warning = output.addObject();
                warning.put("command", command.getCommand());
                warning.put("timestamp", command.getTimestamp());
                warning.put("status", "WARNING");
                warning.put("id", point.getType().name());
                warning.put("date", point.getDate());
                warning.put("interval", point.getInterval());
                warning.put("intervalIndex", point.getIntervalIndex());
                warning.put("message", message);
            }
        }
    }
}
