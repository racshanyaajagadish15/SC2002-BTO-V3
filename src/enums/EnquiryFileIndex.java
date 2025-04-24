package enums;

/*
 * EnquiryFileIndex.java
 * This enum represents the various indices of the enquiry file.
 */
public enum EnquiryFileIndex {
    ID(0),
    NRIC(1),
    PROJECT_ID(2),
    ENQUIRY(3),
    REPLY(4),
    ENQUIRY_DATE(5),
    REPLY_DATE(6);

    private final int index;

    EnquiryFileIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
