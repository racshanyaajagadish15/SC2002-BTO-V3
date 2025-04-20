package controllers;

import java.io.IOException;
import java.util.regex.Pattern;

import models.Applicant;
import models.HDBManager;
import models.HDBOfficer;
import models.User;
import views.AuthenticatorView;

public class AuthenticatorController {

	public void authenticate() {
		String inNric, inPassword;
		AuthenticatorView authenticatorView = new AuthenticatorView();
		User user;
		// Prepare regex, starting with T/S and 7 numeric numbers and ending with a alpha character (NON CASE SENSITIVE)
		Pattern pattern = Pattern.compile("^[TSts]\\d{7}[a-zA-Z]$");

		authenticatorView.showLoginBanner();
		// Get user nric
		inNric = authenticatorView.getNric();
		while (!pattern.matcher(inNric).matches()) {
			authenticatorView.showFailedLogin("Invalid NRIC. Please enter a valid NRIC.");
			inNric = authenticatorView.getNric();
		}
		// Get user password
		try {
			// Try to find a user by the NRIC given
			user = User.findUserByNricDB(inNric);

			if (user == null) {
				authenticatorView.showFailedLogin("NRIC does not match any existing user. Please try again!");
				return;
			}
			inPassword = authenticatorView.getPassword();
			if (!user.getPassword().equals(inPassword)) {
				authenticatorView.showFailedLogin("Incorrect password. Please try again!");
			} 
			else {
				if (user.getClass() == Applicant.class) {
					new ApplicantMainController().applicantSelectMenu((Applicant) user);
				} 
				else if (user.getClass() == HDBOfficer.class) {
					new OfficerMainController().officerSelectMenu((HDBOfficer) user);
				} 
				else if (user.getClass() == HDBManager.class) {
					new ManagerMainController().managerSelectMenu((HDBManager) user);
				}
			}
		}
		// Catches possible error and return the program to prevent login. Prevent further corruption of data from user actions
		catch (IOException e) {
			authenticatorView.showFailedLogin("Login System is currently unavailable. Please contact admin if the issue persist.");
			return;
		}
		catch (NumberFormatException e) {
			authenticatorView.showFailedLogin("User data is corrupted. Contact admin.");
			return;
		}
	}
}