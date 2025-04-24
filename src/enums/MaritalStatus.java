package enums;

/*
 * EnquiryFileIndex.java
 * This enum represents the various indices of the enquiry file.
 */

public enum MaritalStatus {
    SINGLE("Single"),
    MARRIED("Married");

    private final String status;

    MaritalStatus(String status){
        this.status = status;
    }
    public String getStatus() {
        return status;
    }
}
