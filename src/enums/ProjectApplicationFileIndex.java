package enums;

public enum ProjectApplicationFileIndex {
    ID(0),
    PROJECT_ID(1),
    NRIC(2),
    STATUS(3),
    DATE(4);

    private final int index;

    ProjectApplicationFileIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}