package model;

public abstract class EnergyPoint {
    private final int intervalIndex;

    protected EnergyPoint(final int intervalIndex) {
        this.intervalIndex = intervalIndex;
    }

    public int getIntervalIndex() {
        return intervalIndex;
    }
}
