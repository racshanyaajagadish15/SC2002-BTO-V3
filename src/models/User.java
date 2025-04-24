package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import databases.ApplicantDB;
import databases.HDBManagerDB;
import databases.HDBOfficerDB;

/**
 * User class representing a user in the system.
 * This class serves as a base class for different types of users (e.g., Applicant, HDBOfficer, HDBManager).
 * It contains common attributes and methods that are shared among all user types.
 */

public class User {

	private String name;
	protected String nric;
	private int age;
	private String maritalStatus;
	private String password;
	private List<String> filter;

	/**
	 * 
	 * @param name
	 * @param nric
	 * @param age
	 * @param maritalStatus
	 * @param password
	 */
	public User(String name, String nric, int age, String maritalStatus, String password) {
		this.name = name;
		this.nric = nric;
		this.age = age;
		this.maritalStatus = maritalStatus;
		this.password = password;
		this.filter = new ArrayList<>(List.of("", "", "", "", ""));
	}

	/**
	 * 
	 * @param nric
	 * @return User object based on the NRIC provided.
	 * @throws IOException
	 * @throws NumberFormatException
	 */

	public static User findUserByNricDB(String nric) throws IOException, NumberFormatException {
		User user;
		user = ApplicantDB.getApplicantByNRIC(nric);
		if (user == null) user = HDBOfficerDB.getOfficerByNRIC(nric);
		if (user == null) user = HDBManagerDB.getManagerByNRIC(nric);
		return user;
	}
	
	/**
	 * @return the name of the user
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * @return the NRIC of the user
	 */

	public String getNric() {
		return this.nric;
	}
	/**
	 * @return the age of the user
	 */
	public int getAge() {
		return this.age;
	}
	/**
	 * @return the marital status of the user
	 */
	public String getMaritalStatus() {
		return this.maritalStatus;
	}
	/**
	 * @return the password of the user
	 */
	public String getPassword() {
		return this.password;
	}
	/**
	 * @return the filter of the user
	 */

	public List<String> getFilter() {
		return this.filter;
	}

	/**
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 
	 * @param filter
	 */
	public void setFilter(List<String> filter) {
		this.filter = filter;
	}
	/**
	 * Set the name of the user.
	 * 
	 * @param name The new name of the user.
	 */

	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Set the NRIC of the user.
	 * 
	 * @param nric The new NRIC of the user.
	 */
	public void setNric(String nric) {
		this.nric = nric;
	}

	/**
	 * Set the age of the user.
	 * 
	 * @param age The new age of the user.
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * Set the marital status of the user.
	 * 
	 * @param maritalStatus The new marital status of the user.
	 */
	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	/**
	 * Validate the password of the user.
	 * 
	 * @param password The password to validate.
	 * @return true if the password is valid, false otherwise.
	 */
    
	public boolean validatePassword(String password) {
		return this.getPassword().equals(password);
	}
	
	/**
	 * Change the password of the user.
	 * 
	 * @param currentPassword The current password of the user.
	 * @param newPassword     The new password to set.
	 * @return true if the password was changed successfully, false otherwise.
	 * @throws IOException
	 */
	public boolean changePassword(String currentPassword, String newPassword) throws IOException {
		if (!validatePassword(currentPassword)) {
			return false;
		}
		this.setPassword(newPassword);
		this.saveUserDB();
		return true;
	}

	/**
	 * Save the user to the database.
	 * 
	 * @return true if the user was saved successfully, false otherwise.
	 * @throws IOException
	 */
	public boolean saveUserDB() throws IOException {
		if (this.getClass() == Applicant.class) {
			return ApplicantDB.saveUser((Applicant) this);
		} 
		else if (this.getClass() == HDBOfficer.class) {
			return HDBOfficerDB.saveUser((HDBOfficer) this);
		} 
		else if (this.getClass() == HDBManager.class) {
			return HDBManagerDB.saveUser((HDBManager) this);
		}
		return false;

	}
}
