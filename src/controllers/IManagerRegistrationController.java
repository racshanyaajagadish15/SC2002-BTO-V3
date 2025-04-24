package controllers;
import java.util.ArrayList;

import models.HDBManager;
import models.OfficerRegistration;


/**
 * Interface for the ManagerRegistrationController class.
 * This interface defines the methods that a manager can use to manage officer registrations.
 */

public interface IManagerRegistrationController {
	
	public void updateOfficerApplicationStatus(OfficerRegistration officerApplication, String status);
	public ArrayList<OfficerRegistration> getPendingRegistrations(HDBManager manager);

}