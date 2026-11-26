package main;

import fileio.MarketDataInput;
import model.DamPrice;
import model.DamPricePrediction;
import model.DataType;
import model.EnergyPoint;
import model.GridLoad;
import model.GridLoadPrediction;
import model.ImbalancePrice;
import model.ImbalanceVolume;

import java.time.LocalTime;

final class class_create {
    static final int INTERVAL_MINUTES = 15;
    static final int INTERVALS_PER_DAY = 96;
    private static final int SECONDS_PER_MINUTE = 60;

    private class_create() {
    }

    static EnergyPoint createPoint(final DataType type, final MarketDataInput input) {
        String[] bounds = input.interval().split("\\s*-\\s*");
        String start = bounds[0].trim();
        String interval = start + " - " + bounds[1].trim();
        String date = input.date();
        int index = calculateIntervalIndex(LocalTime.parse(start));

        return switch (type) {
            case DAM_PRICE -> new DamPrice(date, interval, index, input.priceAmount());
            case DAM_PRICE_PREDICTION -> new DamPricePrediction(date, interval, index,
                    input.predictedPriceEurMwh(), input.spikeProbability());
            case GRID_LOAD -> new GridLoad(date, interval, index,
                    input.quantity(), input.reservesRequiredMW());
            case GRID_LOAD_PREDICTION -> new GridLoadPrediction(date, interval, index,
                    input.quantity(), input.reservesRequiredMW());
            case IMBALANCE_PRICE -> new ImbalancePrice(date, interval, index,
                    input.positiveImbalancePrice(), input.negativeImbalancePrice());
            case IMBALANCE_VOLUME -> new ImbalanceVolume(date, interval, index,
                    input.positiveImbalanceVol(), input.negativeImbalanceVol());
        };
    }

    static int calculateIntervalIndex(final LocalTime time) {
        return time.toSecondOfDay() / (INTERVAL_MINUTES * SECONDS_PER_MINUTE) + 1;
    }

    static boolean isIntervalStart(final LocalTime time) {
        return time.getMinute() % INTERVAL_MINUTES == 0
                && time.getSecond() == 0 && time.getNano() == 0;
    }
}
