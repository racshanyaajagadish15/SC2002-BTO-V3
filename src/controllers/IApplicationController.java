package controllers;

import java.util.ArrayList;

import models.Applicant;
import models.Application;
import models.FlatType;
import models.Project;

public interface IApplicationController {

	
	/**	
	 * 
	 * @param applicant
	 */
	void projectAction(Applicant applicant);

	/**	
	 * 
	 * @param applicant
	 */
	void applicationAction(Applicant applicant);
	
	/**
	 * 
	 * @param project
	 */
	boolean submitApplication(Project project, Applicant applicant, FlatType flatType);

	ArrayList<Project> getApplicableProjects(Applicant applicant);


	/**
	 * 
	 * @param application
	 * @return 
	 */
	boolean withdrawApplication(Application application);

}