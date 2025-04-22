package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import databases.OfficerRegistrationDB;
import enums.OfficerRegisterationStatus;
import models.Application;

import models.HDBOfficer;
import models.OfficerRegistration;
import models.Project;
import views.OfficerJoinProjectView;

public class OfficerJoinProjectController implements IOfficerJoinProjectController {

	/**
	 * 
	 * @param project
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

	@Override
	public String getRegistrationStatus(int officerRegistrationID) {
		try {
			// Retrieve all officer registrations from the database
			ArrayList<OfficerRegistration> allRegistrations = OfficerRegistrationDB.getAllOfficerRegistrations();

			// Find the registration with the matching ID
			for (OfficerRegistration registration : allRegistrations) {
				if (registration.getOfficerRegistrationID() == officerRegistrationID) {
					return registration.getRegistrationStatus();
				}
			}

			// If no matching registration is found, return a default message
			return "Registration not found.";
		} catch (IOException e) {
			System.out.println("[ERROR] Failed to retrieve registration status: " + e.getMessage());
			return "Error retrieving registration status.";
		}
	}

	/**
	 * 
	 * @param project
	 */
	@Override
	public boolean checkProjectEligibility(Project project) {
		try {
			// Check if the project is visible
			if (!project.getProjectVisibility()) {
				return false;
			}

			// Check if the project is still open for applications
			if (!project.getApplicationClosingDate().after(new Date())) {
				return false;
			}

			// Additional eligibility checks can be added here
			return true;
		} catch (Exception e) {
			System.out.println("[ERROR] Failed to check project eligibility: " + e.getMessage());
			return false;
		}
	}
    public void joinProjectAction(HDBOfficer officer) {
		OfficerJoinProjectView officerJoinProjectView = new OfficerJoinProjectView();
		OfficerJoinProjectController officerJoinProjectController = new OfficerJoinProjectController();
		officerJoinProjectView.showRegistrableProjectMenu(officerJoinProjectController.getRegistrableProjects(officer), officer);
    }

	public void showRegistrations(HDBOfficer officer){
		OfficerJoinProjectView officerJoinProjectView = new OfficerJoinProjectView();
		try {
			// Get all registrations the user has made
			ArrayList<OfficerRegistration> registrations = OfficerRegistration.getOfficerRegistrationsByOfficer(officer);
			officerJoinProjectView.showRegistrationStatus(registrations);
		} catch (IOException | NumberFormatException e) {
			System.out.println("Cannot show project registrations due to error, contact admin if error persist.");
		}
	}

	public ArrayList<Project> getRegistrableProjects(HDBOfficer officer){
		try {
			List<Project> allProjects = Project.getAllProjectsDB();
			ArrayList<Project> registrableProjects = new ArrayList<Project>();
			// Get all registrations the user has made
			ArrayList<OfficerRegistration> registrations = OfficerRegistration.getOfficerRegistrationsByOfficer(officer);
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
				boolean toSkip = false;
				for (OfficerRegistration registration : registrations){
					// Check that projects are not the same ID
					if (project.getProjectID() == registration.getProjectID()){
						toSkip = true;
						break;
					}
					// Check that application is SUCCESSFUL/PENDING and date does not clash 
					Project registerationProject = Project.getProjectByIdDB(registration.getProjectID());

					if (registration.getRegistrationStatus().equals(OfficerRegisterationStatus.PENDING.getStatus()) || registration.getRegistrationStatus().equals(OfficerRegisterationStatus.SUCESSFUL.getStatus())){
						if (!registerationProject.getApplicationClosingDate().before(project.getApplicationOpeningDate()) || registerationProject.getApplicationOpeningDate().after(project.getApplicationClosingDate())) {
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