package enums;

public enum ApplicationFileIndex {
    ID(0),
    NRIC(1),
    PROJECT_ID(2),
    STATUS(3),
    DATE(4);

    private final int index;

    ApplicationFileIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}