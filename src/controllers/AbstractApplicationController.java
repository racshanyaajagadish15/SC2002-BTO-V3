package controllers;

import java.util.ArrayList;

import models.Applicant;
import models.Application;
import models.Project;
import views.ApplicationView;

public abstract class AbstractApplicationController implements IApplicationController {

	public abstract ArrayList<Project> getViewableProjects();

	/**
	 * 
	 * @param applicant
	 */
	public Application projectAction(Applicant applicant) {
		ApplicationView applicationView = new ApplicationView();
		ArrayList<Project> viewableProjects = getViewableProjects();
		if (applicant.getFilter().isEmpty()){
			// TODO Filters
		}

		applicationView.showEligibleProjects();


		return null;
	}

	/**
	 * 
	 * @param applicant
	 */
	public Application applicationAction(Applicant applicant) {
		// TODO - implement AbstractApplicationController.getProjectApplication
		return null;
	}

	/**
	 * 
	 * @param project
	 */
	public void submitApplication(Project project){

	}


	/**
	 * 
	 * @param filters
	 */
	public void setProjectFilter(String[] filters) {
		// TODO - implement AbstractApplicationController.setProjectFilter

	}

	/**
	 * 
	 * @param application
	 */
	public void bookFlat(Application application) {
		// TODO - implement AbstractApplicationController.bookFlat

	}
}