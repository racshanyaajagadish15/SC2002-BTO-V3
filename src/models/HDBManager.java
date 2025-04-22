package models;

import java.util.ArrayList;
import java.util.List;

import databases.ProjectDB;

public class HDBManager extends User {

    private List<Project> ManagedProjects;

    /**
     * Constructor for HDBManager
     * 
     * @param name
     * @param nric
     * @param age
     * @param maritalStatus
     * @param password
     */
    public HDBManager(String name, String nric, int age, String maritalStatus, String password) {
        super(name, nric, age, maritalStatus, password);
    }

    /**
     * Retrieve the list of projects managed by the HDBManager based on their NRIC.
     * 
     * @param nric The NRIC of the HDBManager.
     * @return A list of projects managed by the HDBManager.
     */
    public List<Project> getManagedProjects(String nric) {
        try {
            // Retrieve all projects from the database
            List<Project> allProjects = ProjectDB.getAllProjects();
            List<Project> managedProjects = new ArrayList<>();

            // Filter projects managed by this HDBManager
            for (Project project : allProjects) {
                if (project.getProjectManager().getNric().equalsIgnoreCase(nric)) {
                    managedProjects.add(project);
                }
            }

            return managedProjects;
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to retrieve managed projects: " + e.getMessage());
            return new ArrayList<>();
        }
    }
	/**
	 * Update the details of the HDBManager.
	 * 
	 * @param name          The new name of the HDBManager.
	 * @param nric          The new NRIC of the HDBManager.
	 * @param age           The new age of the HDBManager.
	 * @param maritalStatus The new marital status of the HDBManager.
	 * @param password      The new password of the HDBManager.
	 */
	public void updateHDBManager(String name, String nric, int age, String maritalStatus, String password) {
		// Use setters from the superclass (User)
        super.setName(name); // From User class
		this.setNric(nric); // From User class
		this.setAge(age); // From User class
		this.setMaritalStatus(maritalStatus); // From User class
		this.setPassword(password); // From User class

		System.out.println("[SUCCESS] HDBManager details updated successfully.");
	}
    /**
     * Retrieve the list of projects managed by the HDBManager.
     * 
     * @return A list of projects managed by the HDBManager.
     */
    public List<Project> getManagedProjects() {
        return this.ManagedProjects;
    }

    /**
     * Set the list of projects managed by the HDBManager.
     * 
     * @param ManagedProjects The list of projects to set.
     */
    public void setManagedProjects(List<Project> ManagedProjects) {
        this.ManagedProjects = ManagedProjects;
    }

    /**
     * Retrieve the NRIC of the HDBManager.
     * 
     * @return The NRIC of the HDBManager.
     */
    public String getNric() {
        return this.nric;
    }
}