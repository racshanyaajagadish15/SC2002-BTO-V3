public interface IManagerApplicationController {

	/**
	 * 
	 * @param application
	 * @param status
	 */
	void updateBTOApplicationStatus(Application application, String status);

	/**
	 * 
	 * @param application
	 * @param status
	 */
	void updateBTOApplicationWithdrawalStatus(Application application, String status);

}