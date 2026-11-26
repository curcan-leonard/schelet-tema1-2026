package main;

import model.DataType;
import model.EnergyPoint;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

record DailyDocument(DataType type, String biddingZoneDomain, Instant periodStart,
                     Instant periodEnd, String resolution, String currencyUnit,
                     String priceMeasureUnit, String quantityMeasureUnit,
                     List<EnergyPoint> points) {
    private static final ZoneId MARKET_ZONE = ZoneId.of("Europe/Bucharest");
    private static final String BIDDING_ZONE = "10YRO-TEL------P";
    private static final String RESOLUTION = "PT15M";
    private static final String CURRENCY_UNIT = "EUR";
    private static final String MEASURE_UNIT = "MWH";

    DailyDocument(final DataType type, final LocalDate date) {
        this(type, BIDDING_ZONE, date.atStartOfDay(MARKET_ZONE).toInstant(),
                date.plusDays(1).atStartOfDay(MARKET_ZONE).toInstant().minusSeconds(1), RESOLUTION,
                isPriceType(type) ? CURRENCY_UNIT : null,
                isPriceType(type) ? MEASURE_UNIT : null,
                isPriceType(type) ? null : MEASURE_UNIT,
                new ArrayList<>(Collections.nCopies(class_create.INTERVALS_PER_DAY, null)));
    }

    private static boolean isPriceType(final DataType type) {
        return type == DataType.DAM_PRICE || type == DataType.DAM_PRICE_PREDICTION
                || type == DataType.IMBALANCE_PRICE;
    }

    EnergyPoint getPoint(final int intervalIndex) {
        return points.get(intervalIndex - 1);
    }

    void setPoint(final int intervalIndex, final EnergyPoint point) {
        points.set(intervalIndex - 1, point);
    }
}
