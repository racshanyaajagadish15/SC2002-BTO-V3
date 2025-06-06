package enums;

/**
 * This enum represents the various statuses for officer registratons
 */
public enum OfficerRegisterationStatus {
    PENDING("Pending"),
    SUCESSFUL("Successful"),
    UNSUCCESSFUL("Unsuccessful");
    private final String status;

    OfficerRegisterationStatus(String status){
        this.status = status;
    }
    public String getStatus() {
        return status;
    }
}
