package enums;


/*
 * ApplicationStatus.java
 * This enum represents the various statuses of an application.
 */
public enum ApplicationStatus {
    PENDING("Pending"),
    SUCESSFUL("Successful"),
    UNSUCCESSFUL("Unsuccessful"),
    BOOKED("Booked"),
    WITHDRAWAL_PENDING("Withdrawal Pending"),
    WITHDRAWAL_SUCCESSFUL("Withdrawal Successful"),
    WITHDRAWAL_UNSUCCESSFUL("Withdrawal Unuccessful");
    private final String status;

    ApplicationStatus(String status){
        this.status = status;
    }
    public String getStatus() {
        return status;
    }
}
