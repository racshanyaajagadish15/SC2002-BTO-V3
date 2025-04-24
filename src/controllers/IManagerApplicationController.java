package controllers;
import models.Application;

/**
 * This interface defines the methods for handling the application process for managers.
 * It provides methods to manage applications, including updating application status and withdrawal status.
 */

public interface IManagerApplicationController {

	void updateBTOApplicationStatus(Application application, String status);
	void updateBTOApplicationWithdrawalStatus(Application application, String status);

}