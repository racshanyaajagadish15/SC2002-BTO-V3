package controllers;

import models.Application;
/**
 * Interface for OfficerBookingController.
 */
public interface IOfficerBookingController {

	boolean updateApplications(Application application, String status);
	boolean updateProject(Application application);
	void selectApplicationToBook(models.HDBOfficer officer);
	void viewGenerateReceipt(models.HDBOfficer officer);

}