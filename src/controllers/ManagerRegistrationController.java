package controllers;

import java.io.IOException;
import java.util.ArrayList;

import models.HDBManager;
import models.OfficerRegistration;
import models.Project;
import databases.OfficerRegistrationDB;
import databases.ProjectDB;
import enums.OfficerRegisterationStatus;

public class ManagerRegistrationController implements IManagerRegistrationController {

    /**
     * Update the status of an officer's application.
     * 
     * @param officerApplication The officer's application to update.
     * @param status The new status to set (e.g., "Approved", "Rejected").
     */
   @Override
   public void updateOfficerApplicationStatus(OfficerRegistration officerApplication, String status) {
    try {
        // Update the registration status in the object
        officerApplication.setRegistrationStatus(status);

        // Update in DB
        OfficerRegistration.updateOfficerApplicationStatusDB(
            officerApplication.getOfficerRegistrationID(),
            status
        );

        // If status is "Successful", update ManagerList and Excel file
        if (OfficerRegisterationStatus.SUCESSFUL.getStatus().equalsIgnoreCase(status)) {
            Project project = Project.getProjectByIdDB(officerApplication.getProjectID());
            if (project != null) {
                project.addOfficerToManagerList(officerApplication.getOfficer().getName());
                ProjectDB.addOfficerNRICToExcel(officerApplication.getProjectID(), officerApplication.getOfficer().getNric());
            } else {
                System.out.println("[ERROR] Project not found for Project ID: " + officerApplication.getProjectID());
                return;
            }
        }

        System.out.println("[SUCCESS] Officer application status updated to: " + status);
    } catch (IOException e) {
        System.out.println("[ERROR] Failed to update officer application status: " + e.getMessage());
    }
}

    /**
     * Retrieve all pending officer registrations.
     * 
     * @return A list of pending officer registrations.
     */
    public ArrayList<OfficerRegistration> getPendingRegistrations(HDBManager manager) {
        ArrayList<OfficerRegistration> pendingRegistrations = new ArrayList<>();
        try {
            // Retrieve all officer registrations from the database
            ArrayList<OfficerRegistration> allRegistrations = OfficerRegistrationDB.getAllOfficerRegistrations();

            // Filter for pending registrations
            for (OfficerRegistration registration : allRegistrations) {
                if (registration.getRegistrationStatus().equalsIgnoreCase("Pending")) {
                    pendingRegistrations.add(registration);
                }
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to retrieve pending registrations: " + e.getMessage());
        }
        return pendingRegistrations;
    }

        /**
     * Retrieve all pending officer registrations.
     * 
     * @return A list of pending officer registrations.
     */
    public ArrayList<OfficerRegistration> getAllRegistrations(HDBManager manager) {
        ArrayList<OfficerRegistration> pendingRegistrations = new ArrayList<>();
        try {
            // Retrieve all officer registrations from the database
            ArrayList<OfficerRegistration> allRegistrations = OfficerRegistrationDB.getAllOfficerRegistrations();

            // Filter for pending registrations
            for (OfficerRegistration registration : allRegistrations) {
                if (registration.getRegistrationStatus().equalsIgnoreCase("Pending")) {
                    pendingRegistrations.add(registration);
                }
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to retrieve pending registrations: " + e.getMessage());
        }
        return pendingRegistrations;
    }
}