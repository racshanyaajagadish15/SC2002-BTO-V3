package enums;

public enum OfficerRegistrationFileIndex {
    NAME(0),
    NRIC(1),
    AGE(2),
    MARITAL_STATUS(3),
    PROJECTS(4);

    private final int index;

    OfficerRegistrationFileIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}