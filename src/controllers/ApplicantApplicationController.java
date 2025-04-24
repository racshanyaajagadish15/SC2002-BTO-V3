package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import enums.FlatTypeName;
import enums.MaritalStatus;
import models.Applicant;
import models.FlatType;
import models.Project;

/**
 * This class is responsible for handling the application process for applicants.
 * It extends the AbstractApplicationController class and implements the getApplicableProjects method.
 */

public class ApplicantApplicationController extends AbstractApplicationController {
	
	/**
	 * This method retrieves a list of applicable projects for the given applicant.
	 * It checks the applicant's marital status and age to determine the applicable flat types and projects.
	 * 
	 * @param applicant The applicant for whom to retrieve applicable projects.
	 * @return A list of applicable projects for the applicant, or null if an error occurs.
	 */
	@Override
	public ArrayList<Project> getApplicableProjects(Applicant applicant){
		try {
			List<Project> allProjects = Project.getAllProjectsDB();
			ArrayList<Project> applicableProjects = new ArrayList<Project>();

			for (Project project : allProjects){
				// Check project visibility
				if (!project.getProjectVisibility()){
					continue; 
				}
				// Check if application is still open
				if (!project.getApplicationClosingDate().after(new Date())){
					continue; 
				}

				List<FlatType> projectFlatTypes = project.getFlatTypes();
				
				// Single applicant
				if (applicant.getMaritalStatus().equals(MaritalStatus.SINGLE.getStatus())){
					if (applicant.getAge() < 35){
						return null;
					}
					List<FlatType> applicableFlatTypes = new ArrayList<FlatType>();
					for (FlatType flatType : projectFlatTypes){
						if (flatType.getFlatType().equals(FlatTypeName.TWO_ROOM.getflatTypeName())){
							applicableProjects.add(project);
							applicableFlatTypes.add(flatType);
						}
					}
					project.setFlatTypes(applicableFlatTypes);
				}
				// Married applicant
				else if (applicant.getMaritalStatus().equals(MaritalStatus.MARRIED.getStatus())){
					if (applicant.getAge() < 21){
						return null; 
					}
					List<FlatType> applicableFlatTypes = new ArrayList<FlatType>();
					for (FlatType flatType : projectFlatTypes){
						if (flatType.getFlatType().equals(FlatTypeName.TWO_ROOM.getflatTypeName()) || 
						flatType.getFlatType().equals(FlatTypeName.THREE_ROOM.getflatTypeName())){
							applicableFlatTypes.add(flatType);
						}
					}
					if (applicableFlatTypes.size() != 0){
						project.setFlatTypes(applicableFlatTypes);
						applicableProjects.add(project);
					}
				}
			}
			Project.sortProjectByName(applicableProjects);
			return applicableProjects;
		}
		catch (IOException e){
			return null;
		}
	}
}