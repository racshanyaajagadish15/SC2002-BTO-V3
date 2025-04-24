package enums;

/**
 * This enum represents the various indices of the filter list.
 */

public enum FilterIndex {
    PROJECT_NAME(0),
    NEIGHBOURHOOD(1),
    PRICE_START(2),
    PRICE_END(3),
    FLAT_TYPE(4);

    private final int index;

    FilterIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
