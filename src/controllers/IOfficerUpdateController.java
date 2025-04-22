package controllers;

import models.Application;

public interface IOfficerUpdateController {

	/**
	 * 
	 * @param application
	 */
	void updateApplications(Application application);

	/**
	 * 
	 * @param application
	 */
	void updateProject(Application application);

}