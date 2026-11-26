package model;

import com.fasterxml.jackson.databind.node.ObjectNode;

public final class ImbalanceVolume extends EnergyPoint {
    private final Double positive;
    private final Double negative;

    public ImbalanceVolume(final String date, final String interval, final int intervalIndex,
                           final Double positive, final Double negative) {
        super(DataType.IMBALANCE_VOLUME, date, interval, intervalIndex);
        this.positive = positive;
        this.negative = negative;
    }

    @Override
    public String validate() {
        String message = validateVolume("PositiveImbalanceVol", positive);
        if (message != null) {
            return message;
        }
        return validateVolume("NegativeImbalanceVol", negative);
    }

    private static String validateVolume(final String fieldName, final Double volume) {
        if (volume == null) {
            return fieldName + " is null; interval skipped.";
        }
        if (volume < 0) {
            return fieldName + " " + volume
                    + " is below the minimum allowed value 0; interval skipped.";
        }
        return null;
    }

    @Override
    public void writeValuesToJson(final ObjectNode pointJson) {
        pointJson.put("positiveImbalanceVol", positive);
        pointJson.put("negativeImbalanceVol", negative);
    }
}
