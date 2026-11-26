package model;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.math.BigDecimal;

public final class GridLoad extends EnergyPoint {
    private static final BigDecimal MAX_QUANTITY_CHANGE = BigDecimal.valueOf(10000);

    private final BigDecimal quantity;
    private final Double reservesRequiredMW;

    public GridLoad(final String date, final String interval,
                    final int intervalIndex, final BigDecimal quantity) {
        this(date, interval, intervalIndex, quantity, null);
    }

    public GridLoad(final String date, final String interval, final int intervalIndex,
                    final BigDecimal quantity, final Double reservesRequiredMW) {
        super(DataType.GRID_LOAD, date, interval, intervalIndex);
        this.quantity = quantity;
        this.reservesRequiredMW = reservesRequiredMW;
    }

    public Double getReservesRequiredMW() {
        return reservesRequiredMW;
    }

    @Override
    public String validate() {
        return validateQuantity(quantity);
    }

    public String validateChange(final GridLoad previousPoint) {
        BigDecimal change = quantity.subtract(previousPoint.quantity).abs();
        if (change.compareTo(MAX_QUANTITY_CHANGE) > 0) {
            return "Quantity change exceeds 10000 MW compared with the previous interval;"
                    + " interval skipped.";
        }
        return null;
    }

    @Override
    public void writeValuesToJson(final ObjectNode pointJson) {
        pointJson.put("quantity", quantity);
    }
}
