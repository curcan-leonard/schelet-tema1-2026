package model;

import com.fasterxml.jackson.databind.node.ObjectNode;

public final class ImbalancePrice extends EnergyPoint {
    private static final double MAX_PRICE = 15000;

    private final Double positive;
    private final Double negative;

    public ImbalancePrice(final String date, final String interval, final int intervalIndex,
                          final Double positive, final Double negative) {
        super(DataType.IMBALANCE_PRICE, date, interval, intervalIndex);
        this.positive = positive;
        this.negative = negative;
    }

    @Override
    public String validate() {
        String message = validatePrice("PositiveImbalancePrice", positive);
        if (message != null) {
            return message;
        }
        return validatePrice("NegativeImbalancePrice", negative);
    }

    private static String validatePrice(final String fieldName, final Double price) {
        if (price == null) {
            return fieldName + " is null; interval skipped.";
        }
        if (Math.abs(price) > MAX_PRICE) {
            return fieldName + " " + price
                    + " exceeds the maximum allowed value 15000 EUR/MWh; interval skipped.";
        }
        return null;
    }

    @Override
    public void writeValuesToJson(final ObjectNode pointJson) {
        pointJson.put("positiveImbalancePrice", positive);
        pointJson.put("negativeImbalancePrice", negative);
    }
}
