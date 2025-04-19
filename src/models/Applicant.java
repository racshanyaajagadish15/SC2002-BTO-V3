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
		this.projectApplication = null;
		this.enquiries = null;
	}

	public Application getApplication() {
		return projectApplication;
	}

	public void setApplication(Application projectApplication) {
		this.projectApplication = projectApplication;
	}

	public List<Enquiry> getEnquiries(){
		return this.enquiries;
	}



}