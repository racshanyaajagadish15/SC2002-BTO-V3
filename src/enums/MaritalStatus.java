package enums;

/**
 * This enum represents the various statuses for user marital status
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
