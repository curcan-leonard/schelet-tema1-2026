package model;

import com.fasterxml.jackson.databind.node.ObjectNode;

public final class DamPricePrediction extends EnergyPoint {
    private final Double prediction;
    private final Double spikeProbability;

    public DamPricePrediction(final String date, final String interval,
                              final int intervalIndex, final Double prediction,
                              final Double spikeProbability) {
        super(DataType.DAM_PRICE_PREDICTION, date, interval, intervalIndex);
        this.prediction = prediction;
        this.spikeProbability = spikeProbability;
    }

    @Override
    public String validate() {
        String message = validateMarketPrice("PredictedPriceEurMwh", prediction);
        if (message != null) {
            return message;
        }
        if (spikeProbability == null) {
            return "SpikeProbability is null; interval skipped.";
        }
        if (spikeProbability < 0.0 || spikeProbability > 1.0) {
            return "SpikeProbability " + spikeProbability
                    + " is outside the valid range [0.0, 1.0]; interval skipped.";
        }
        return null;
    }

    @Override
    public void writeValuesToJson(final ObjectNode pointJson) {
        pointJson.put("predictedPriceEurMwh", prediction);
        pointJson.put("spikeProbability", spikeProbability);
    }
}
