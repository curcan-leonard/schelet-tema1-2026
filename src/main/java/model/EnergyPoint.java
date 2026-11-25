package model;

import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class EnergyPoint {
    private static final double MIN_MARKET_PRICE = -500;
    private static final double MAX_MARKET_PRICE = 4000;

    private final DataType type;
    private final String date;
    private final String interval;
    private final int intervalIndex;

    protected EnergyPoint(final DataType type, final String date,
                          final String interval, final int intervalIndex) {
        this.type = type;
        this.date = date;
        this.interval = interval;
        this.intervalIndex = intervalIndex;
    }

    public DataType getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getInterval() {
        return interval;
    }

    public int getIntervalIndex() {
        return intervalIndex;
    }

    public abstract String validate();

    public abstract void writeValuesToJson(ObjectNode pointJson);

    protected static String validateMarketPrice(final String fieldName, final Double price) {
        if (price == null) {
            return fieldName + " is null; interval skipped.";
        }
        if (price < MIN_MARKET_PRICE || price > MAX_MARKET_PRICE) {
            return fieldName + " " + price
                    + " is outside the market range [-500, 4000] EUR/MWh; interval skipped.";
        }
        return null;
    }
}
