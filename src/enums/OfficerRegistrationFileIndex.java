package enums;

public enum OfficerRegistrationFileIndex {
    ID(0),
    NRIC(1),
    PROJECT(2),
    STATUS(3),
    DATE(4);

    private final int index;

    OfficerRegistrationFileIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}