package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import models.Applicant;
import models.Enquiry;
import models.Project;
import views.ApplicantEnquiryView;

public class ApplicantEnquiryController implements IApplicantEnquiryController {


	/**
	 *
	 * @param applicant
	 */
	public void enquiryActionMenu(Applicant applicant) {
		ApplicantEnquiryView applicationEnquiryView = new ApplicantEnquiryView();
		try {
			// Get all enquiries the user has made
			ArrayList<Enquiry> enquiries = Enquiry.getEnquiriesByNricDB(applicant.getNric());
	
			// Mapping of Project -> Enquiries ArrayList
			Map<Project, ArrayList<Enquiry>> projectEnquiriesMap = new HashMap<>();
			Map<Integer, Project> projectIdMap = new HashMap<>();
			Project project;
			int projID;
			for (Enquiry enquiry : enquiries) {
				projID = enquiry.getProjectID();
	
				if (projectIdMap.containsKey(projID)) {
					project = projectIdMap.get(projID);
				} else {
					project = Project.getProjectsByIdDB(projID);
					projectIdMap.put(projID, project);
					projectEnquiriesMap.put(project, new ArrayList<>());
				}
				
				projectEnquiriesMap.get(project).add(enquiry);
			}
			// Show menu for enquiries
			applicationEnquiryView.showEnquiriesMenu(projectEnquiriesMap);
		} catch (IOException | NumberFormatException e) {
			System.out.println("Cannot show enquiries due to error, contact admin if error persist.");
		}
	}
	

	/**
	 * 
	 * @param enquiry
	 */
	public boolean submitEnquiry(Enquiry enquiry) {
		try {
			Enquiry.createEnquiryDB(enquiry);
			return true;
		}
		catch (NumberFormatException e){
			return false;
		}
		catch (IOException e){
			return false;
		}
	}

	/**
	 * 
	 * @param enquiry
	 */
	public void editEnquiry(Enquiry enquiry, String newText) {
		try {
			enquiry.setEnquiry(newText);
			enquiry.setEnquiryDate(new Date());
			boolean success = Enquiry.updateEnquiryDB(enquiry);
			if (!success) {
                System.out.println("Failed to update the enquiry in the database.");
            }else{
				System.out.println("Enquiry updated successfully.");
			}
        } catch (IOException e) {
			System.out.println("An error occurred while replying to enquiries, contact admin if error persist");
		}		
	}

	/**
	 * 
	 * @param id
	 */
	public boolean deleteEnquiry(int id) {
		try {
			Enquiry.deleteEnquiryDB(id);
			return true;
		}
		catch (NumberFormatException e){
			return false;
		}
		catch (IOException e){
			return false;
		}
	}

}