package controllers;

import java.util.ArrayList;
import java.util.Date;

import databases.ProjectDB;
import models.Enquiry;
import models.Project;
import utilities.LoggerUtility;
import views.ManagerEnquiryView;

public class ManagerEnquiryController {

    private ManagerEnquiryView view;

    public ManagerEnquiryController(ManagerEnquiryView view) {
        this.view = view;
    }

    public void handleEnquiryMenu() {
        view.showEnquiryMenu();
    }

    public void replyToEnquiry() {
        try {
            ArrayList<Enquiry> allEnquiries = getAllProjectEnquiries();
            view.displayEnquiries(allEnquiries);

            int enquiryID = view.getEnquiryIDInput();
            if (enquiryID == 0) {
                view.displayInfo("Returning to the previous menu.");
                return;
            }

            Enquiry selectedEnquiry = findEnquiryById(allEnquiries, enquiryID);
            if (selectedEnquiry == null) {
                view.displayError("Invalid Enquiry ID. Please try again.");
                return;
            }

            String reply = view.getReplyInput();
            selectedEnquiry.setReply(reply);
            view.showReplyResult(true);
            LoggerUtility.logInfo("Reply sent successfully for Enquiry ID: " + enquiryID);
        } catch (Exception e) {
            view.displayError("An error occurred: " + e.getMessage());
            LoggerUtility.logError("Error while replying to enquiry", e);
        }
    }

    public void viewAllEnquiries() {
        try {
            ArrayList<Enquiry> enquiries = getAllProjectEnquiries();
            view.displayEnquiries(enquiries);
            LoggerUtility.logInfo("Viewed all enquiries.");
        } catch (Exception e) {
            view.displayError("An error occurred: " + e.getMessage());
            LoggerUtility.logError("Error while viewing all enquiries", e);
        }
    }

    public void viewProjectEnquiries() {
        try {
            ArrayList<Project> allProjects = ProjectDB.getAllProjects();
            view.displayProjects(allProjects);

            int projectID = view.getProjectIDInput();
            Project project = new Project(
                projectID, "Default Project Name", null, "Default Neighborhood",
                new ArrayList<>(), new Date(), new Date(), 0, true
            );

            ArrayList<Enquiry> enquiries = Enquiry.getProjectEnquiries(project);
            view.displayEnquiries(enquiries);
            LoggerUtility.logInfo("Viewed enquiries for Project ID: " + projectID);
        } catch (Exception e) {
            view.displayError("An error occurred: " + e.getMessage());
            LoggerUtility.logError("Error while viewing project enquiries", e);
        }
    }

    private ArrayList<Enquiry> getAllProjectEnquiries() {
        // Logic to fetch all enquiries
        return new ArrayList<>();
    }

    private Enquiry findEnquiryById(ArrayList<Enquiry> enquiries, int enquiryID) {
        for (Enquiry enquiry : enquiries) {
            if (enquiry.getEnquiryID() == enquiryID) {
                return enquiry;
            }
        }
        return null;
    }
}