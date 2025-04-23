package controllers;

import models.Application;
import models.FlatType;
import models.HDBOfficer;
import models.OfficerRegistration;
import models.Project;
import utilities.LoggerUtility;
import views.OfficerUpdateView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import enums.ApplicationStatus;
import enums.OfficerRegisterationStatus;

public class OfficerUpdateController implements IOfficerUpdateController {

    private OfficerUpdateView view;
    public OfficerUpdateController() {
        this.view = new OfficerUpdateView();
    }

    /**
     * Update the details of an application in the database.
     * 
     * @param application The application to update.
     */
    @Override
    public boolean updateApplications(Application application, String status) {
        try {
            // Update the application in the database
            application.setApplicationStatus(status);
            Application.updateApplicationDB(application);
            return true;
        } catch (Exception e) {
            LoggerUtility.logError("Failed to update application when booking", e);
            return false;
        }
    }

    /**
     * Update the project associated with an application in the database.
     * 
     * @param application The application whose project is to be updated.
     */
    @Override
    public boolean updateProject(Application application) {
        try {
            // Update the project details in the database
            for (FlatType flatType : application.getProject().getFlatTypes()){
                if (application.getFlatType() == flatType.getFlatType()){
                    flatType.setNumFlats(flatType.getNumFlats()-1);
                    Project.updateProjectDB(application.getProject());
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            LoggerUtility.logError("Failed to update project when booking", e);
            return false;
        }
    }

    /**
     * Generate a booking receipt for the specified application.
     * 
     * @param applicationId The ID of the application to generate a receipt for.
     * @return boolean indicating if receipt generation was successful
     */
    private boolean updateBooking(Application application) {
        if (updateApplications(application, ApplicationStatus.BOOKED.getStatus())){
            if (updateProject(application)){
                // Revert
                updateApplications(application, ApplicationStatus.BOOKED.getStatus());
                return false;
            }
        }
        
        return true;
    }

    public void selectApplicationToBook(HDBOfficer officer){
        List<Application> allApplications;
        ArrayList<OfficerRegistration> registrations;
        try {
            allApplications = Application.getAllApplicationDB();
            registrations = OfficerRegistration.getOfficerRegistrationsByOfficerDB(officer);
            ArrayList<Application> officerProjectApplications = new ArrayList<Application>();
            for (Application application : allApplications){
                for (OfficerRegistration registration : registrations){
                    if ((registration.getProjectID() == application.getProject().getProjectID()) && 
                    application.getApplicationStatus() == ApplicationStatus.SUCESSFUL.getStatus() && 
                    (registration.getRegistrationStatus() == OfficerRegisterationStatus.SUCESSFUL.getStatus())){
                        officerProjectApplications.add(application);
                        break;
                    }
                }
            }
            
            int option = view.showApplicationsToUpdate(officerProjectApplications);
            if (option == -1){
                return;
            }
            Application selectedApplication = officerProjectApplications.get(option);
            if (updateBooking(selectedApplication)){
                view.displaySuccess("Successfully booked flat!");
            }
            else{
                view.displayError("Failed to book flat. Contact admin if error persist.");
            }
        }
        catch (IOException e){
            LoggerUtility.logError("Failed to get all projects when booking", e);
            view.displayError("Cannot display applications. Contact admin if error persist.");
        }

    }
}