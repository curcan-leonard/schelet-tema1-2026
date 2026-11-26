package main;

import java.time.LocalDate;
import java.time.LocalDateTime;

final class SimulationClock {
    private LocalDateTime currentTime;
    private LocalDateTime firstImportTime;

    void advance(final String timestamp) {
        currentTime = LocalDateTime.parse(timestamp);
    }

    void startImport() {
        firstImportTime = currentTime;
    }

    void stopImport() {
        firstImportTime = null;
    }

    boolean valid() {
        return firstImportTime != null && !currentTime.isBefore(firstImportTime)
                && class_create.isIntervalStart(currentTime.toLocalTime());
    }

    LocalDate getDate() {
        return currentTime.toLocalDate();
    }
}
