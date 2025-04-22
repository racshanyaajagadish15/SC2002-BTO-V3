package controllers;

import models.Application;
import databases.ApplicationDB;
import databases.BookingReceiptDB;

public class OfficerUpdateController implements IOfficerUpdateController {

    /**
     * Update the details of an application in the database.
     * 
     * @param application The application to update.
     */
    @Override
    public void updateApplications(Application application) {
        try {
            // Update the application in the database
            ApplicationDB.updateApplication(application);
            System.out.println("[SUCCESS] Application ID " + application.getApplicationID() + " updated successfully.");
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to update application: " + e.getMessage());
        }
    }

    /**
     * Update the project associated with an application in the database.
     * 
     * @param application The application whose project is to be updated.
     */
    @Override
    public void updateProject(Application application) {
        try {
            // Update the project details in the database
            ApplicationDB.updateApplication(application);
            System.out.println("[SUCCESS] Project for Application ID " + application.getApplicationID() + " updated successfully.");
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to update project for application: " + e.getMessage());
        }
    }

    /**
     * Generate a booking receipt for the specified application.
     * 
     * @param applicationId The ID of the application to generate a receipt for.
     * @return boolean indicating if receipt generation was successful
     */
    public boolean updateBooking(int applicationId) {
        try {
            Application application = ApplicationDB.getApplicationById(applicationId);
            if (application == null) {
                return false;
            }

            BookingReceiptDB.generateReceipt(application);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}