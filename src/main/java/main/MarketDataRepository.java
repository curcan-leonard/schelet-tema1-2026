package main;

import model.DataType;
import model.EnergyPoint;
import model.GridLoad;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class MarketDataRepository {
    private final Map<DataType, Map<LocalDate, DailyDocument>> documents =
            new EnumMap<>(DataType.class);

    void ensureDay(final LocalDate date) {
        for (DataType type : DataType.values()) {
            ensureDocument(type, date);
        }
    }

    private DailyDocument ensureDocument(final DataType type, final LocalDate date) {
        Map<LocalDate, DailyDocument> days = documents.computeIfAbsent(type,
                key -> new HashMap<>());
        return days.computeIfAbsent(date, key -> new DailyDocument(type, key));
    }

    String validateAndStorePoint(final EnergyPoint point, final boolean patch) {
        String message = point.validate();
        if (message == null && point instanceof GridLoad gridLoad) {
            EnergyPoint previous = getPreviousPoint(point);
            if (previous instanceof GridLoad previousGridLoad) {
                message = gridLoad.validateChange(previousGridLoad);
            }
        }
        if (message == null || !patch) {
            DailyDocument document = ensureDocument(point.getType(),
                    LocalDate.parse(point.getDate()));
            document.setPoint(point.getIntervalIndex(), message == null ? point : null);
        }
        return message;
    }

    private EnergyPoint getPreviousPoint(final EnergyPoint point) {
        LocalDate date = LocalDate.parse(point.getDate());
        if (point.getIntervalIndex() == 1) {
            return getPoint(point.getType(), date.minusDays(1), class_create.INTERVALS_PER_DAY);
        }
        return getPoint(point.getType(), date, point.getIntervalIndex() - 1);
    }

    EnergyPoint getPoint(final DataType type, final LocalDate date, final int intervalIndex) {
        Map<LocalDate, DailyDocument> days = documents.get(type);
        if (days == null) {
            return null;
        }
        DailyDocument document = days.get(date);
        return document == null ? null : document.getPoint(intervalIndex);
    }

    EnergyPoint getLatest(final DataType type) {
        Map<LocalDate, DailyDocument> days = documents.get(type);
        if (days == null) {
            return null;
        }
        LocalDate latestDate = null;
        EnergyPoint latestPoint = null;
        for (LocalDate date : days.keySet()) {
            if (latestDate != null && !date.isAfter(latestDate)) {
                continue;
            }
            EnergyPoint point = getLatestForDay(days.get(date));
            if (point != null) {
                latestDate = date;
                latestPoint = point;
            }
        }
        return latestPoint;
    }

    private static EnergyPoint getLatestForDay(final DailyDocument document) {
        for (int index = class_create.INTERVALS_PER_DAY; index >= 1; index--) {
            EnergyPoint point = document.getPoint(index);
            if (point != null) {
                return point;
            }
        }
        return null;
    }

    List<EnergyPoint> getRange(final DataType type, final LocalDateTime start,
                              final LocalDateTime end) {
        List<EnergyPoint> points = new ArrayList<>();
        LocalDateTime time = start;
        if (!class_create.isIntervalStart(time.toLocalTime())) {
            int minutesUntilNext = class_create.INTERVAL_MINUTES
                    - time.getMinute() % class_create.INTERVAL_MINUTES;
            time = time.truncatedTo(ChronoUnit.MINUTES).plusMinutes(minutesUntilNext);
        }
        while (time.isBefore(end)) {
            int index = class_create.calculateIntervalIndex(time.toLocalTime());
            EnergyPoint point = getPoint(type, time.toLocalDate(), index);
            if (point != null) {
                points.add(point);
            }
            time = time.plusMinutes(class_create.INTERVAL_MINUTES);
        }
        return points;
    }
}
