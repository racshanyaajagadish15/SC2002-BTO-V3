package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import models.Applicant;
import models.Enquiry;
import models.Project;
import views.ApplicationEnquiryView;

public class ApplicantEnquiryController implements IApplicantEnquiryController {


	/**
	 *
	 * @param applicant
	 */
	public void enquiryActionMenu(Applicant applicant) {
		ApplicationEnquiryView applicationEnquiryView = new ApplicationEnquiryView();
		try {
			// Get all enquiries the user has made
			ArrayList<Enquiry> enquiries = getEnquiriesByNric(applicant.getNric());
	
			// Mapping of Project -> Enquiries ArrayList
			Map<Project, ArrayList<Enquiry>> projectEnquiriesMap = new HashMap<>();
			Map<Integer, Project> projectIdMap = new HashMap<>();
	
			for (Enquiry enquiry : enquiries) {
				int projID = enquiry.getProjectID();
				Project project;
	
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
			System.out.println("uhoh");
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
	 * @param nric
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public ArrayList<Enquiry> getEnquiriesByNric(String nric) throws NumberFormatException, IOException {
		return Enquiry.getEnquiriesByNricDB(nric);
	}

	/**
	 * 
	 * @param enquiry
	 */
	public boolean editEnquiry(Enquiry enquiry) {
		try {
			Enquiry.updateEnquiryDB(enquiry);
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