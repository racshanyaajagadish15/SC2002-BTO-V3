public interface IManagerRegistrationController {

	/**
	 * 
	 * @param officerApplication
	 * @param status
	 */
	void updateOfficerApplicationStatus(OfficerApplication officerApplication, String status);

	ArrayList<OfficerRegistrations> getPendingRegistrations();

}