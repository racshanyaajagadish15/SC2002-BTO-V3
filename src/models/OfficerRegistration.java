package models;
import java.io.IOException;
import java.util.ArrayList;

import databases.OfficerRegistrationDB;
/**
 * OfficerRegistration class representing a registrations in the system.
 */
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
    public OfficerRegistration(int officerRegistrationID, HDBOfficer officer, Project project, String registrationStatus) {
        this.officerRegistrationID = officerRegistrationID;
        this.officer = officer;
        this.project = project;
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
        return this.project.getProjectID();
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
    /**
     * Retrieve all officer registrations from the database.
     * 
     * @return A list of all officer registrations.
     * @throws IOException If there is an error retrieving the data from the database.
     */
    
    public static ArrayList<OfficerRegistration> getPendingRegistrationsDB() throws IOException {
        ArrayList<OfficerRegistration> allRegistrations = OfficerRegistrationDB.getAllOfficerRegistrations();
        ArrayList<OfficerRegistration> pendingRegistrations = new ArrayList<>();
    
        for (OfficerRegistration registration : allRegistrations) {
            if ("Pending".equalsIgnoreCase(registration.getRegistrationStatus())) {
                pendingRegistrations.add(registration);
            }
        }
    
        return pendingRegistrations;
    }

    /**
     * Retrieve all officer registrations from the database.
     * 
     * @return A list of all officer registrations.
     * @throws IOException If there is an error retrieving the data from the database.
     */

    public static ArrayList<OfficerRegistration> getOfficerRegistrationsByOfficerDB(HDBOfficer officer) throws IOException {
        ArrayList<OfficerRegistration> allRegistrations = OfficerRegistrationDB.getAllOfficerRegistrations();
        ArrayList<OfficerRegistration> officerRegistrations = new ArrayList<>();

        for (OfficerRegistration registration : allRegistrations) {
            if (registration.getOfficer().getNric().equalsIgnoreCase(officer.getNric())) {
                officerRegistrations.add(registration);
            }
        }

        return officerRegistrations;
    }
    /**
     * Retrieve all officer registrations from the database.
     * 
     * @return A list of all officer registrations.
     * @throws IOException If there is an error retrieving the data from the database.
     */

    public static void updateOfficerApplicationStatusDB(int registrationID, String status) throws IOException {
        OfficerRegistrationDB.updateOfficerRegistration(registrationID, status);
        }
}