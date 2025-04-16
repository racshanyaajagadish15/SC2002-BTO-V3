package models;
import java.util.ArrayList;

public class OfficerRegistration {

	private int officerRegistrationID;
	private HDBOfficer officer;
	private Project project;
	private String registrationStatus;

	/**
	 * 
	 * @param officer
	 * @param project
	 * @param officerRegistrationID
	 * @param registrationStatus
	 */
	public OfficerRegistration(HDBOfficer officer, Project project, int officerRegistrationID, String registrationStatus) {
		// TODO - implement OfficerRegistration.OfficerRegistration
		
	}

	/**
	 * 
	 * @param officer
	 * @param project
	 * @param registrationStatus
	 */
	public static OfficerRegistration createOfficerRegistrationDB(HDBOfficer officer, Project project, String registrationStatus) {
		// TODO - implement OfficerRegistration.createOfficerRegistrationDB
		
	}

	/**
	 * 
	 * @param officer
	 */
	public static ArrayList<OfficerRegistration> getOfficerRegistrationDB(HDBOfficer officer) {
		// TODO - implement OfficerRegistration.getOfficerRegistrationDB
		
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

	public Project getProject() {
		return this.project;
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
	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * 
	 * @param registrationStatus
	 */
	public void setRegistrationStatus(String registrationStatus) {
		this.registrationStatus = registrationStatus;
	}

}