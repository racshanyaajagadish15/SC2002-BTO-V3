package models;

import java.util.List;

public class HDBManager extends User {

	private List<Project> ManagedProjects;

	/**
	 * 
	 * @param name
	 * @param nric
	 * @param age
	 * @param maritalstatus
	 * @param password
	 */
	public HDBManager(String name, String nric, int age, String maritalStatus, String password) {
		super(name, nric, age, maritalStatus, password);
	}
	

	/**
	 * 
	 * @param nric
	 */
	public List<Project> getManagedProjects(String nric) {
		// TODO - implement HDBManager.getManagedProjects
		return null;
	}

	/**
	 * 
	 * @param name
	 * @param nric
	 * @param age
	 * @param maritalstatus
	 * @param password
	 */
	public void updateHDBManager(String name, String nric, int age, String maritalstatus, String password) {
		// TODO - implement HDBManager.updateHDBManager
		
	}

	public List<Project> getManagedProjects() {
		// TODO - implement HDBManager.getManagedProjects
		return null;
	}

	/**
	 * 
	 * @param ManagedProjects
	 */
	public void setManagedProjects(List<Project> ManagedProjects) {
		// TODO - implement HDBManager.setManagedProjects
		
	}

	public String getNric() {
        return this.nric;
    }

}