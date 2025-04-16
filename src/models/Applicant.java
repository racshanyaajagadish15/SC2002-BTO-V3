package models;

import java.util.List;

public class Applicant extends User {

	private Application projectApplication;
	private List<Enquiry> enquiries;

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


}