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

	/**
	 * Constructor for HDBOfficer
	 * 
	 * @param name
	 * @param nric
	 * @param age
	 * @param maritalStatus
	 * @param password
	 * @param officerRegistrations
	 */

	public ArrayList<OfficerRegistration> getOfficerRegistrations(){
		return this.officerRegistrations;
	}
	
	/**
	 * Set the officer registrations for this HDBOfficer.
	 * 
	 * @param officerRegistrations The list of officer registrations to set.
	 */
	public void setOfficerRegistrations(ArrayList<OfficerRegistration> officerRegistrations){
		this.officerRegistrations = officerRegistrations;
	}
}