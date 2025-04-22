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
	public static OfficerRegistration createOfficerRegistrationDB(HDBOfficer officer, Project project, String registrationStatus) throws IOException {
		return OfficerRegistrationDB.createOfficerRegistration(officer, project, registrationStatus);
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
	
	public static ArrayList<OfficerRegistration> getPendingRegistrations() throws IOException {
		ArrayList<OfficerRegistration> allRegistrations = OfficerRegistrationDB.getAllOfficerRegistrations();
		ArrayList<OfficerRegistration> pendingRegistrations = new ArrayList<>();
	
		for (OfficerRegistration registration : allRegistrations) {
			if ("Pending".equalsIgnoreCase(registration.getRegistrationStatus())) {
				pendingRegistrations.add(registration);
			}
		}
	
		return pendingRegistrations;
	}

	public static ArrayList<OfficerRegistration> getOfficerRegistrationsByOfficer(HDBOfficer officer) throws IOException {
        ArrayList<OfficerRegistration> allRegistrations = OfficerRegistrationDB.getAllOfficerRegistrations();
        ArrayList<OfficerRegistration> officerRegistrations = new ArrayList<>();

        for (OfficerRegistration registration : allRegistrations) {
            if (registration.getOfficer().getNric().equalsIgnoreCase(officer.getNric())) {
                officerRegistrations.add(registration);
            }
        }

        return officerRegistrations;
    }

	public static void updateOfficerApplicationStatus(OfficerRegistration registration, String status) throws IOException {
		// Update the registration status in the object
		registration.setRegistrationStatus(status);
	
		// Persist the updated status in the database
		OfficerRegistrationDB.updateOfficerRegistration(registration.getOfficerRegistrationID(), status);
	
		System.out.println("[SUCCESS] Registration ID " + registration.getOfficerRegistrationID() + " updated to status: " + status);
	}
}