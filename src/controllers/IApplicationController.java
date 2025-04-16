package controllers;

import java.util.ArrayList;

import models.Applicant;
import models.Application;
import models.Project;

public interface IApplicationController {

	
	/**	
	 * 
	 * @param applicant
	 */
	Application projectAction(Applicant applicant);

	/**	
	 * 
	 * @param applicant
	 */
	Application applicationAction(Applicant applicant);
	
	/**
	 * 
	 * @param project
	 */
	void submitApplication(Project project);

	ArrayList<Project> getViewableProjects();

	/**
	 * 
	 * @param filters
	 */
	void setProjectFilter(String[] filters);

	/**
	 * 
	 * @param application
	 */
	void bookFlat(Application application);

}