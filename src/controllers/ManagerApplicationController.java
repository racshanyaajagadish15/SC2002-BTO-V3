package controllers;

import java.io.IOException;
import models.Application;

/**
 * Interface for the ManagerApplicationController class.
 * This interface defines the methods that a manager can use to manage BTO applications.
 */
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
            application.setApplicationStatus(status);
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
            application.setApplicationStatus(status);
            Application.updateApplicationDB(application);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update withdrawal status: " + e.getMessage(), e);
        }
    }
}

