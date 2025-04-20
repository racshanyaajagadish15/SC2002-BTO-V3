package controllers;

import java.io.IOException;
import java.util.ArrayList;

import enums.ApplicationStatus;
import models.Applicant;
import models.Application;
import models.FlatType;
import models.Project;
import views.ApplicationView;

public abstract class AbstractApplicationController implements IApplicationController {

	public abstract ArrayList<Project> getApplicableProjects(Applicant applicant);

	/**
	 * 
	 * @param applicant
	 */
	public void projectAction(Applicant applicant) {
		ApplicationView applicationView = new ApplicationView();
		ArrayList<Project> applicableProjects = getApplicableProjects(applicant);
		applicationView.showApplicationMenu(applicableProjects, applicant);
	}

	/**
	 * 
	 * @param applicant
	 */
	public void applicationAction(Applicant applicant) {
		ApplicationView applicationView = new ApplicationView();
		applicationView.showApplicationDetails(applicant);	
	}

	/**
	 * 
	 * @param project
	 */
	public boolean submitApplication(Project project, Applicant applicant, FlatType flatType){
		try {
			Application.createApplicationDB(applicant, project, ApplicationStatus.PENDING.getStatus(), flatType);
			return true;
		}
		catch (IOException e){
			return false;
		}
	}

	/**
	 * 
	 * @param application
	 */
	public boolean withdrawApplication(Application application) {
		application.setApplicationStatus(ApplicationStatus.WITHDRAWAL_PENDING.getStatus());
		try {
			Application.updateApplicationDB(application);
			return true;
		}
		catch (IOException e){
			return false;
		}
		catch (Exception e){
			return false;
		}
	}
}