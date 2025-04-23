package controllers;

import java.util.ArrayList;

import models.HDBOfficer;
import models.Project;

public interface IOfficerRegistrationController {

	/**
	 * 
	 * @param project
	 */
	boolean registerForProject(HDBOfficer officer, Project project);

	ArrayList<Project> getRegistrableProjects(HDBOfficer officer);

	/**
	 * Handles the officer's request to join a project.
	 * @param officer
	 */
	void joinProjectAction(models.HDBOfficer officer);

	/**
	 * Shows the officer's registration status.
	 * @param officer
	 */
	void showRegistrations(models.HDBOfficer officer);
}