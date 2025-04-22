package models;

import java.util.Date;

public class BookingReceipt {
    private final Application application;
    private final Date generationDate;

    public BookingReceipt(Application application) {
        this.application = application;
        this.generationDate = new Date();
    }

    public Application getApplication() {
        return application;
    }

    public Date getGenerationDate() {
        return generationDate;
    }
}
