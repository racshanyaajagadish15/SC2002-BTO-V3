package controllers;

import java.io.IOException;
import java.util.ArrayList;

import models.Enquiry;
import models.Project;

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
            enquiry.setReplyDate(new java.util.Date()); // Set the current date as the reply date
            boolean success = Enquiry.updateEnquiryDB(enquiry);
            if (!success) {
                System.out.println("Failed to update the enquiry in the database.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while replying to the enquiry: " + e.getMessage());
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
			System.out.println("An error occurred while retrieving project enquiries: " + e.getMessage());
		}
		return projectEnquiries;		
	}
}