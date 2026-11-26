package model;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.math.BigDecimal;

public final class GridLoadPrediction extends EnergyPoint {
    private final BigDecimal quantity;
    private final Double reservesRequiredMW;

    public GridLoadPrediction(final String date, final String interval,
                              final int intervalIndex, final BigDecimal quantity,
                              final Double reservesRequiredMW) {
        super(DataType.GRID_LOAD_PREDICTION, date, interval, intervalIndex);
        this.quantity = quantity;
        this.reservesRequiredMW = reservesRequiredMW;
    }

    @Override
    public String validate() {
        String message = validateQuantity(quantity);
        if (message != null) {
            return message;
        }
        if (reservesRequiredMW == null) {
            return "ReservesRequiredMW is null; interval skipped.";
        }
        if (reservesRequiredMW <= 0) {
            return "ReservesRequiredMW " + reservesRequiredMW
                    + " is not positive; interval skipped.";
        }
        return null;
    }

    @Override
    public void writeValuesToJson(final ObjectNode pointJson) {
        pointJson.put("quantity", quantity);
    }
}
