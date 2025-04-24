package controllers;

import java.util.ArrayList;

import models.Applicant;
import models.Application;
import models.FlatType;
import models.Project;

/**
 * This interface defines the methods for handling the application process for applicants.
 * It provides methods to manage applications, including submitting and withdrawing applications.
 */

public interface IApplicationController {

	void projectAction(Applicant applicant);
	void applicationAction(Applicant applicant);
	boolean submitApplication(Project project, Applicant applicant, FlatType flatType);
	ArrayList<Project> getApplicableProjects(Applicant applicant);
	boolean withdrawApplication(Application application);

}