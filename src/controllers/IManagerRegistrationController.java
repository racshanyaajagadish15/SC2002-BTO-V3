package controllers;
import java.util.ArrayList;

import models.HDBManager;
import models.OfficerRegistration;


public interface IManagerRegistrationController {

	/**
	 * 
	 * @param officerApplication
	 * @param status
	 */
	public void updateOfficerApplicationStatus(OfficerRegistration officerApplication, String status);
	public ArrayList<OfficerRegistration> getPendingRegistrations(HDBManager manager);

}