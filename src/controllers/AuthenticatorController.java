package controllers;

import java.io.IOException;
import java.util.regex.Pattern;

import models.Applicant;
import models.HDBManager;
import models.HDBOfficer;
import models.User;
import views.AuthenticatorView;
import utilities.LoggerUtility;

/**
 * This class is responsible for handling the authentication process for users.
 * It provides methods for user login and password change functionality.
 */

public class AuthenticatorController {

    /**
     * The view used for displaying information to the user.
     */
	private final AuthenticatorView authenticatorView;

    /**
     * Constructor for the AuthenticatorController class.
     * Initializes the view used for displaying information to the user.
     */
	public AuthenticatorController(){
		this.authenticatorView = new AuthenticatorView();
	}

    /**
     * This method handles the password change process for the given user.
     * It prompts the user for their current password, new password, and confirmation of the new password.
     * If the passwords match and the current password is correct, it updates the user's password.
     * 
     * @param user The user whose password is to be changed.
     * @return true if the password change was successful, false otherwise.
     */
    public boolean handlePasswordChange(User user) {
        authenticatorView.showPasswordChangePrompt();
        String currentPassword = authenticatorView.getCurrentPassword();
        if (currentPassword.isBlank()){
            return false;
        }
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

    /**
     * This method handles the authentication process for the user.
     * It prompts the user for their NRIC and password, validates the input, and checks if the user exists in the database.
     * If the authentication is successful, it redirects the user to their respective main menu based on their role.
     */
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
                authenticatorView.displaySuccess("Login Successful!");
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