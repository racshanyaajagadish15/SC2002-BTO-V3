package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import controllers.ApplicantMainController;
import controllers.ManagerMainController;
import controllers.OfficerMainController;
import databases.ApplicantDB;
import databases.HDBManagerDB;
import databases.HDBOfficerDB;

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

	public static User findUserByNricDB(String nric) throws IOException, NumberFormatException {
		User user;
		user = ApplicantDB.getApplicantByNRIC(nric);
		if (user == null) user = HDBOfficerDB.getOfficerByNRIC(nric);
		if (user == null) user = HDBManagerDB.getManagerByNRIC(nric);
		return user;
	}

	public String getName() {
		return this.name;
	}

	public String getNric() {
		return this.nric;
	}

	public int getAge() {
		return this.age;
	}

	public String getMaritalStatus() {
		return this.maritalStatus;
	}

	public String getPassword() {
		return this.password;
	}

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


	public void setName(String name) {
		this.name = name;
	}
	public void setNric(String nric) {
		this.nric = nric;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
    
	public boolean validatePassword(String password) {
		return this.getPassword().equals(password);
	}
	
	public boolean changePassword(String currentPassword, String newPassword) throws IOException {
		if (!validatePassword(currentPassword)) {
			return false;
		}
		this.setPassword(newPassword);
		this.saveUserDB();
		return true;
	}

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
