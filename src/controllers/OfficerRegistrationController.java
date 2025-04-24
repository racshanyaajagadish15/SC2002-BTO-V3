package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import enums.OfficerRegisterationStatus;
import models.Application;
import models.HDBOfficer;
import models.OfficerRegistration;
import models.Project;
import views.OfficerRegistrationView;

/**
 * OfficerRegistrationController handles the officer's registration actions.
 * It allows officers to join projects and view their registration status.
 */

public class OfficerRegistrationController implements IOfficerRegistrationController {

    private final OfficerRegistrationView officerRegistrationView;

    public OfficerRegistrationController() {
        this.officerRegistrationView = new OfficerRegistrationView();
    }

    /**
     * Handles the officer's request to join a project.
     */
    public void joinProjectAction(HDBOfficer officer) {
        ArrayList<Project> projects = getRegistrableProjects(officer);
        if (projects == null) {
            officerRegistrationView.displayError("Cannot show project registrations due to error, contact admin if error persist.");
            return;
        }
        if (projects.isEmpty()) {
            officerRegistrationView.showRegistrableProjects(projects);
            return;
        }
        while (true) {
            officerRegistrationView.showRegistrableProjects(projects);
            int projectIndex = officerRegistrationView.getProjectSelection(projects.size());
            if (projectIndex == -1) {
                // User chose to go back
                return;
            }
            Project selectedProject = projects.get(projectIndex);
            boolean confirmed = officerRegistrationView.getRegistrationConfirmation(selectedProject.getProjectName());
            if (confirmed) {
                boolean success = registerForProject(officer, selectedProject);
                if (success) {
                    officerRegistrationView.displaySuccess("Application Successful");
                    return;
                } else {
                    officerRegistrationView.displayError("Application unsuccessful, contact admin if error persist.");
                }
            }
            // If not confirmed, loop again
        }
    }

    /**
     * Registers the officer for a project.
     * @param officer The officer to register.
     * @param project The project to register for.
     * @return true if registration is successful, false otherwise.
     * @throws IOException If there is an error writing to the database.
     */
    public boolean registerForProject(HDBOfficer officer, Project project) {
        try {
            OfficerRegistration.createOfficerRegistrationDB(officer, project, OfficerRegisterationStatus.PENDING.getStatus());
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    /**
     * Shows the officer's registration status.
     * @param officer The officer requesting the registration status.
     * @return true if the status was successfully displayed, false otherwise.
     * @throws IOException If there is an error reading from the database.
     */
    public void showRegistrations(HDBOfficer officer){
        try {
            ArrayList<OfficerRegistration> registrations = OfficerRegistration.getOfficerRegistrationsByOfficerDB(officer);
            officerRegistrationView.showRegistrationStatus(registrations);
            officerRegistrationView.waitForBack();
        } catch (IOException | NumberFormatException e) {
            officerRegistrationView.displayError("Cannot show project registrations due to error, contact admin if error persist.");
        }
    }

    /**
     * Gets the list of projects the officer can register for.
     * @param officer The officer requesting the projects.
     * @return A list of registrable projects.
     * @exception IOException If there is an error reading from the database.
     */
    public ArrayList<Project> getRegistrableProjects(HDBOfficer officer){
        try {
            List<Project> allProjects = Project.getAllProjectsDB();
            ArrayList<Project> registrableProjects = new ArrayList<Project>();
            ArrayList<OfficerRegistration> registrations = OfficerRegistration.getOfficerRegistrationsByOfficerDB(officer);
            Application application = Application.getApplicationByNricDB(officer.getNric());
            Project applicationProject = null;
            if (application != null){
                applicationProject = application.getProject();
            }

            for (Project project : allProjects){
                // Check project visibility
                if (!project.getProjectVisibility()){
                    continue; 
                }
                // Check if project is still open
                if (!project.getApplicationClosingDate().after(new Date())){
                    continue; 
                }
                // Check if officer is an applicant of the project
                if (applicationProject != null && project.getProjectID() == applicationProject.getProjectID()){
                    continue;
                }
                if (project.getOfficerSlots() <= 0){
                    continue;
                }
                boolean toSkip = false;
                for (OfficerRegistration registration : registrations){
                    // Check that projects are not the same ID
                    if (project.getProjectID() == registration.getProjectID()){
                        toSkip = true;
                        break;
                    }
                    // Check that application is SUCCESSFUL/PENDING and date does not clash 
                    Project registrationProject = registration.getProject();

                    if (registration.getRegistrationStatus().equals(OfficerRegisterationStatus.PENDING.getStatus()) || registration.getRegistrationStatus().equals(OfficerRegisterationStatus.SUCESSFUL.getStatus())){
                        if (!(registrationProject.getApplicationClosingDate().before(project.getApplicationOpeningDate()) || registrationProject.getApplicationOpeningDate().after(project.getApplicationClosingDate()))){
                            toSkip = true;
                            break;
                        }
                    }
                }
                if (toSkip){
                    continue;
                }
                // Passes all criterias
                registrableProjects.add(project);
            }
            Project.sortProjectByName(registrableProjects);
            return registrableProjects;
        }
        catch (IOException e){
            return null;
        }
    }
}