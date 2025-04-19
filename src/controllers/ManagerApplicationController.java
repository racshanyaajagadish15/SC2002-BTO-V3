package controllers;
import java.io.IOException;

import databases.ApplicationDB;
import models.Application;

public class ManagerApplicationController implements IManagerApplicationController {

    /**
     * Update the status of a BTO application.
     * 
     * @param application The application to update.
     * @param status The new status to set.
     */
    @Override
    public void updateBTOApplicationStatus(Application application, String status) {
        try {
            // Update the application's status
            application.setApplicationStatus(status);
            // Persist the updated application to the database
            Application.updateApplicationDB(application);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update application status: " + e.getMessage(), e);
        }
    }

    /**
     * Update the withdrawal status of a BTO application.
     * 
     * @param application The application to update.
     * @param status The new withdrawal status to set.
     */
    @Override
    public void updateBTOApplicationWithdrawalStatus(Application application, String status) {
        try {
            // Update the application's status
            application.setApplicationStatus(status);

            // Persist the updated application to the database
            Application.updateApplicationDB(application);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update application withdrawal status: " + e.getMessage(), e);
        }
    }
}