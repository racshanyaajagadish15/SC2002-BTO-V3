package controllers;

import java.io.IOException;
import java.util.regex.Pattern;

import models.Applicant;
import models.HDBManager;
import models.HDBOfficer;
import models.User;
import views.AuthenticatorView;
import utilities.LoggerUtility;

public class AuthenticatorController {

	private final AuthenticatorView authenticatorView;
	public AuthenticatorController(){
		this.authenticatorView = new AuthenticatorView();
	}
    public boolean handlePasswordChange(User user) {
        authenticatorView.showPasswordChangePrompt();
        String currentPassword = authenticatorView.getCurrentPassword();
        if (!currentPassword.equals(user.getPassword())){
            authenticatorView.displayError("Current password is incorrect! Please try again.");
            return false;
        }
        String newPassword = authenticatorView.getNewPassword();
        String confirmPassword = authenticatorView.getConfirmPassword();

        if (!newPassword.equals(confirmPassword)) {
            authenticatorView.displayError("New passwords do not match!");
            return false;
        }

        try {
            if (user.changePassword(currentPassword, newPassword)) {
                authenticatorView.displaySuccess("Password changed successfully!");
                return true;
            } else {
                authenticatorView.displayError("Current password is incorrect!");
                return false;
            }
        } catch (IOException e) {
            LoggerUtility.logError("Password change failed", e);
            authenticatorView.displayError("Failed to change password. Please try again later.");
            return false;
        }
    }

    public void authenticate() {
        String inNric, inPassword;
        
        User user;
        // Prepare regex, starting with T/S and 7 numeric numbers and ending with a alpha character (NON CASE SENSITIVE)
        Pattern pattern = Pattern.compile("^[TSts]\\d{7}[a-zA-Z]$");

        authenticatorView.showLoginBanner();
        // Get user nric
        inNric = authenticatorView.getNric();
        while (!pattern.matcher(inNric).matches()) {
            authenticatorView.displayError("Invalid NRIC. Please enter a valid NRIC.");
            inNric = authenticatorView.getNric();
        }
        // Get user password
        try {
            // Try to find a user by the NRIC given
            user = User.findUserByNricDB(inNric);

            if (user == null) {
                authenticatorView.displayError("NRIC does not match any existing user. Please try again!");
                return;
            }
            inPassword = authenticatorView.getPassword();
            if (!user.getPassword().equals(inPassword)) {
                authenticatorView.displayError("Incorrect password. Please try again!");
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
            LoggerUtility.logError("Authentication failed", e);
            authenticatorView.displayError("Login System is currently unavailable. Please contact admin if the issue persist.");
            return;
        }
        catch (NumberFormatException e) {
            LoggerUtility.logError("Data corruption detected during authentication", e);
            authenticatorView.displayError("User data is corrupted. Contact admin.");
            return;
        }
    }
}