package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import enums.OfficerRegisterationStatus;
import models.Enquiry;
import models.HDBOfficer;
import models.OfficerRegistration;
import models.Project;
import views.OfficerEnquiryView;

public class OfficerEnquiryController implements IOfficerEnquiryController {

    /**
     * Reply to an enquiry by updating its reply and reply date.
     * 
     * @param enquiry The enquiry to reply to.
     * @param message The reply message.
     */
	public void replyEnquiry(Enquiry enquiry, String message) {
        try {
            enquiry.setReply(message);
            enquiry.setReplyDate(new Date()); // Set the current date as the reply date
            boolean success = Enquiry.updateEnquiryDB(enquiry);
            if (!success) {
                System.out.println("Failed to update the enquiry in the database.");
            }
			else{
				System.out.println("Enquiry updated successfully.");
			}
        } catch (IOException e) {
			System.out.println("An error occurred while replying to enquiries, contact admin if error persist");
        }		
	}

    /**
     * Retrieve enquiries for a specific project.
     * 
     * @param project The project for which to retrieve enquiries.
     * @return A list of enquiries for the specified project.
     */

	public ArrayList<Enquiry> getProjectEnquiries(Project project) {
		ArrayList<Enquiry> projectEnquiries = new ArrayList<>();
		try {
			ArrayList<Enquiry> allEnquiries = Enquiry.getAllEnquiriesDB();
			for (Enquiry enquiry : allEnquiries) {
				if (enquiry.getProjectID() == project.getProjectID()) {
					projectEnquiries.add(enquiry);
				}
			}
		} catch (IOException e) {
			System.out.println("An error occurred while retrieving project enquiries, contact admin if error persist");
		}
		return projectEnquiries;		
	}

	public void enquiryActionMenu(HDBOfficer officer) {
		try {
			OfficerEnquiryView officerEnquiryView = new OfficerEnquiryView();
			ArrayList<OfficerRegistration> officerRegistrations= OfficerRegistration.getOfficerRegistrationsByOfficer(officer);
			ArrayList<Project> projectsAssigned = new ArrayList<Project>();
			// Mapping of Project -> Enquiries ArrayList
			Map<Project, ArrayList<Enquiry>> projectEnquiriesMap = new HashMap<>();

			for (OfficerRegistration officerRegistration : officerRegistrations){
				if (officerRegistration.getRegistrationStatus().equals(OfficerRegisterationStatus.SUCESSFUL.getStatus())){
					projectsAssigned.add(Project.getProjectByIdDB(officerRegistration.getProjectID()));
				}
			}
			for (Project project : projectsAssigned){
				ArrayList<Enquiry> projectEnquiries = Enquiry.getProjectEnquiries(project);
				projectEnquiriesMap.put(project, projectEnquiries);
			}

			officerEnquiryView.showProjectEnquiries(projectEnquiriesMap);
		} catch (IOException e) {
			System.out.println("An error occurred while retrieving project enquiries, contact admin if error persist");
		}
	}

}