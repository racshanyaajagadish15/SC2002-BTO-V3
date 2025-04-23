package models;

import java.io.IOException;
import java.util.List;

import databases.ApplicationDB;

public class Application {

	private Applicant applicant;
	private Project project;
	private String applicationStatus;
	private int applicationID;
	private String flatType;

	/**
	 * 
	 * @param applicant
	 * @param project
	 * @param applicationStatus
	 * @param applicationID
	 */
	public Application(Applicant applicant, Project project, String applicationStatus, int applicationID, String flatType) {
		this.applicant = applicant;
		this.project = project;
		this.applicationStatus = applicationStatus;
		this.applicationID = applicationID;
		this.flatType = flatType;
	}

	/**
	 * 
	 * @param applicant
	 * @param project
	 * @param applicationStatus
	 * @param applicationID
	 */
	public static void updateApplicationDB(Application applicantion) throws IOException{
		ApplicationDB.updateApplication(applicantion);
	}

	public static void createApplicationDB(Applicant applicant, Project project, String applicationStatus, FlatType flatType) throws IOException {
		ApplicationDB.createApplication(applicant, project, applicationStatus, flatType);
	}

	public static Application getApplicationByNricDB(String nric) throws IOException {
		return ApplicationDB.getApplicationByNric(nric);
	}

	public static List<Application> getAllApplicationDB() throws IOException {
		return ApplicationDB.getAllApplications();
	}
	public Applicant getApplicant() {
		return this.applicant;
	}

	public Project getProject() {
		return this.project;
	}

	public String getApplicationStatus() {
		return this.applicationStatus;
	}

	public int getApplicationID() {
		return this.applicationID;
	}
	
	public String getFlatType() {
		return this.flatType;
	}


	/**
	 * 
	 * @param applicant
	 */
	public void setApplicant(Applicant applicant) {
		this.applicant = applicant;
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
	 * @param applicationStatus
	 */
	public void setApplicationStatus(String applicationStatus) {
		this.applicationStatus = applicationStatus;
	}

	/**
	 * 
	 * @param applicationID
	 */
	public void setApplicationID(int applicationID) {
		this.applicationID = applicationID;
	}

	/**
	 * 
	 * @param flatType
	 */
	public void setFlatType(String flatType) {
		this.flatType = flatType;
	}
}