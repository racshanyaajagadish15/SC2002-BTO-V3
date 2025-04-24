package enums;

/*
 * EnquiryFileIndex.java
 * This enum represents the various indices of the enquiry file.
 */

public enum ProjectApplicationFileIndex {
    ID(0),
    PROJECT_ID(1),
    NRIC(2),
    STATUS(3),
    FLAT_TYPE(4),
    DATE(5);

    private final int index;

    ProjectApplicationFileIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}