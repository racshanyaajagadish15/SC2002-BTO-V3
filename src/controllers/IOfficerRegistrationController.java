package controllers;

import java.util.ArrayList;

import models.HDBOfficer;
import models.Project;


/**
 * Interface for Officer Registration Controller.
 * This interface defines the methods that an officer can use to manage project registrations.
 */
public interface IOfficerRegistrationController {

	
	boolean registerForProject(HDBOfficer officer, Project project);

	ArrayList<Project> getRegistrableProjects(HDBOfficer officer);

	void joinProjectAction(models.HDBOfficer officer);
	void showRegistrations(models.HDBOfficer officer);
}