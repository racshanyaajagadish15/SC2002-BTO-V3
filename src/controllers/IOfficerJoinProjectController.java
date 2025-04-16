package controllers;

public interface IOfficerJoinProjectController {

	/**
	 * 
	 * @param project
	 */
	void registerForProject(int project);

	/**
	 * 
	 * @param officerRegistrationID
	 */
	String getRegistrationStatus(int officerRegistrationID);

	/**
	 * 
	 * @param project
	 */
	bool checkProjectEligibility(Project project);

	List<Project> getAppliableProjectList();

}