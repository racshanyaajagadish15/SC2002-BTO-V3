package controllers;

import models.Project;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface for the ManagerProjectController class.
 * This interface defines the methods that a manager can use to manage projects.
 */

public interface IManagerProjectController {

	void createProject();
	void editProject(Project project);
	void toggleProjectVisibility(Project project);
	ArrayList<Project> getAllProjects();
	ArrayList<Project> getOwnedProjects();
	ArrayList<Project> getFilteredProjects(List<String> filter);
	ArrayList<Project> getSpecificProject(String projectName);
	void deleteProject(Project project);

}