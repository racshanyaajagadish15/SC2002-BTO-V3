package controllers;

import models.Project;
import java.util.ArrayList;
import java.util.List;

public interface IManagerProjectController {

	void createProject();

	/**
	 * 
	 * @param project
	 */
	void editProject(Project project);

	/**
	 * 
	 * @param project
	 */
	void toggleProjectVisibility(Project project);

	ArrayList<Project> getAllProjects();

	ArrayList<Project> getOwnedProjects();

	/**
	 * 
	 * @param filter
	 */
	ArrayList<Project> getFilteredProjects(List<String> filter);

	/**
	 * 
	 * @param projectName
	 */
	ArrayList<Project> getSpecificProject(String projectName);

	/**
	 * 
	 * @param project
	 */
	void deleteProject(Project project);

}