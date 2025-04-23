package controllers;

import models.Application;

public interface IOfficerUpdateController {

	/**
	 * 
	 * @param application
	 */
	boolean updateApplications(Application application, String status);

	/**
	 * 
	 * @param application
	 */
	boolean updateProject(Application application);

}