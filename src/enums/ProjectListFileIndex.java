package enums;

/**
 * This enum represents the various indices of the Project file.
 */

public enum ProjectListFileIndex {
    PROJECT_ID(0), // Add this line
    NAME(1),
    NEIGHBORHOOD(2),
    TYPE_1(3),
    TYPE_1_UNITS(4),
    TYPE_1_PRICE(5),
    TYPE_2(6),
    TYPE_2_UNITS(7),
    TYPE_2_PRICE(8),
    OPENING_DATE(9),
    CLOSING_DATE(10),
    MANAGER(11),
    OFFICER_SLOT(12),
    OFFICERS(13),
    VISIBILITY(14);

    private final int index;

    ProjectListFileIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}