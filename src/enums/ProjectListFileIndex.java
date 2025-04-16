package enums;

public enum ProjectListFileIndex {
    NAME(0),
    NEIGHBORHOOD(1),
    TYPE_1(2),
    TYPE_1_UNITS(3),
    TYPE_1_PRICE(4),
    TYPE_2(5),
    TYPE_2_UNITS(6),
    TYPE_2_PRICE(7),
    OPENING_DATE(8),
    CLOSING_DATE(9),
    MANAGER(10),
    OFFICER_SLOT(11),
    OFFICER(12),
    VISIBILITY(13); // Add visibility field here

    private final int index;

    ProjectListFileIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}