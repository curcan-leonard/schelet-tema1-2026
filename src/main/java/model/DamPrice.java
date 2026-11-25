package model;

import com.fasterxml.jackson.databind.node.ObjectNode;

public final class DamPrice extends EnergyPoint {
    private final Double price;

    public DamPrice(final String date, final String interval,
                    final int intervalIndex, final Double price) {
        super(DataType.DAM_PRICE, date, interval, intervalIndex);
        this.price = price;
    }

    public Double getPrice() {
        return price;
    }

    @Override
    public String validate() {
        return validateMarketPrice("PriceAmount", price);
    }

    @Override
    public void writeValuesToJson(final ObjectNode pointJson) {
        pointJson.put("priceAmount", price);
    }
}
