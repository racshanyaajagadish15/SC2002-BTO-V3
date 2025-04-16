package models;

import java.util.ArrayList;

public class HDBOfficer extends Applicant {

	private ArrayList<OfficerRegistration> officerRegistrations;

	/**
	 * 
	 * @param name
	 * @param nric
	 * @param age
	 * @param maritalStatus
	 * @param password
	 */
	public HDBOfficer(String name, String nric, int age, String maritalStatus, String password) {
		super(name, nric, age, maritalStatus, password);
	}

	public ArrayList<OfficerRegistration> getOfficerRegistrations(){
		return this.officerRegistrations;
	}
	
	public void setOfficerRegistrations(ArrayList<OfficerRegistration> officerRegistrations){
		this.officerRegistrations = officerRegistrations;
	}
}