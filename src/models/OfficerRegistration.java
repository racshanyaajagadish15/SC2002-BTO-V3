package models;
import java.io.IOException;
import java.util.ArrayList;

import databases.OfficerRegistrationDB;

public class OfficerRegistration {

	private int officerRegistrationID;
	private HDBOfficer officer;
	private int projectID;
	private String registrationStatus;

	/**
	 * 
	 * @param officer
	 * @param project
	 * @param officerRegistrationID
	 * @param registrationStatus
	 */
	public OfficerRegistration(int officerRegistrationID, HDBOfficer officer, int projectID, String registrationStatus) {
		this.officerRegistrationID = officerRegistrationID;
		this.officer = officer;
		this.projectID = projectID;
		this.registrationStatus = registrationStatus;
	}

	/**
	 * 
	 * @param officer
	 * @param project
	 * @param registrationStatus
	 */
	public static OfficerRegistration createOfficerRegistrationDB(HDBOfficer officer, Project project, String registrationStatus) {
		try {
			return OfficerRegistrationDB.createOfficerRegistration(officer, project, registrationStatus);
		}catch (IOException e){
			return null;
		}
	}

	/**
	 * 
	 * @param officer
	 * @throws IOException 
	 */
	public static ArrayList<OfficerRegistration> getOfficerRegistrationsByOfficerDB(HDBOfficer officer) throws IOException {
		return OfficerRegistrationDB.getOfficerRegistrationsByOfficer(officer);
	}

	/**
	 * 
	 * @param officer
	 * @param project
	 * @param registrationStatus
	 */
	public static OfficerRegistration updateOfficerRegistrationDB(HDBOfficer officer, Project project, String registrationStatus) {
		// TODO - implement OfficerRegistration.updateOfficerRegistrationDB
		
	}

	public int getOfficerRegistrationID() {
		return this.officerRegistrationID;
	}

	public HDBOfficer getOfficer() {
		return this.officer;
	}

	public int getProjectID() {
		return this.projectID;
	}

	public String getRegistrationStatus() {
		return this.registrationStatus;
	}

	/**
	 * 
	 * @param officerRegistrationID
	 */
	public void setOfficerRegistrationID(int officerRegistrationID) {
		this.officerRegistrationID = officerRegistrationID;
	}

	/**
	 * 
	 * @param officer
	 */
	public void setOfficer(HDBOfficer officer) {
		this.officer = officer;
	}

	/**
	 * 
	 * @param project
	 */
	public void setProjectID(int projectID) {
		this.projectID = projectID;
	}

	/**
	 * 
	 * @param registrationStatus
	 */
	public void setRegistrationStatus(String registrationStatus) {
		this.registrationStatus = registrationStatus;
	}

}