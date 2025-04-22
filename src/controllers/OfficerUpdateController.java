package controllers;

import models.Application;
import databases.ApplicationDB;

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
}