package controllers;

import models.Application;

public interface IOfficerBookingController {

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

	/**
	 * Select application to book for officer.
	 * @param officer
	 */
	void selectApplicationToBook(models.HDBOfficer officer);

	/**
	 * Generate receipt for officer.
	 * @param officer
	 */
	void viewGenerateReceipt(models.HDBOfficer officer);

}