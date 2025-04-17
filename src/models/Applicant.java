package models;

import java.util.List;

public class Applicant extends User {

	private Application projectApplication;
	private List<Enquiry> enquiries;
	private int applicationID;
	
		
	/**
	 * 
	 * @param name
	 * @param nric
	 * @param age
	 * @param maritalStatus
	 * @param password
	 */
	public Applicant(String name, String nric, int age, String maritalStatus, String password) {
		super(name, nric, age, maritalStatus, password);		
	}

	public String getProjectApplicationStatus() {
		// TODO - implement Applicant.getProjectApplicationStatus
		return null;
	}

	public boolean isEligibleForHDB() {
        return true; // Placeholder logic
    }

	public int getApplicationID() {
		return applicationID;
	}

	public void setApplicationID(int applicationID) {
		this.applicationID = applicationID;
	}



}