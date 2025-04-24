package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import enums.OfficerRegisterationStatus;
import models.Enquiry;
import models.HDBOfficer;
import models.OfficerRegistration;
import models.Project;
import views.OfficerEnquiryView;

/**
 * OfficerEnquiryController handles the logic for managing enquiries related to projects.
 * It allows officers to reply to enquiries and retrieve project-specific enquiries.
 */

public class OfficerEnquiryController implements IOfficerEnquiryController {
    private final OfficerEnquiryView view;

    public OfficerEnquiryController() {
        this.view = new OfficerEnquiryView();
    }

    /**
     * Reply to an enquiry by updating its reply and reply date.
     * 
     * @param enquiry The enquiry to reply to.
     * @param message The reply message.
     */
    @Override
    public void replyEnquiry(Enquiry enquiry, String message) {
        try {
            enquiry.setReply(message);
            if (message.isBlank()){
                view.displayInfo("Enquiry reply canceled");
                return;
            }
            enquiry.setReplyDate(new Date()); // Set the current date as the reply date
            boolean success = Enquiry.updateEnquiryDB(enquiry);
            if (success) {
                view.displaySuccess("Enquiry updated successfully.");
            } else {
                view.displayError("Failed to update the enquiry in the database.");
            }
        } catch (IOException e) {
            view.displayError("An error occurred while replying to enquiries, contact admin if error persist");
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
                if (enquiry.getProject().getProjectID() == project.getProjectID()) {
                    projectEnquiries.add(enquiry);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while retrieving project enquiries, contact admin if error persist");
        }
        return projectEnquiries;		
    }

    
    /** 
     * @param officer
     */
    // New method: main entry point for officer to handle enquiries (all logic here)
    public void enquiryActionMenu(HDBOfficer officer) {
        try {
            ArrayList<OfficerRegistration> officerRegistrations= OfficerRegistration.getOfficerRegistrationsByOfficerDB(officer);
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

            if (projectEnquiriesMap.size() == 0) {
                view.displayInfo("There are no enquiries from projects you are managing.");
                return;
            }

            // Main interaction loop (was in view, now in controller)
            List<Project> projectList = new ArrayList<>(projectEnquiriesMap.keySet());
            Project.sortProjectByName(projectList);
            ArrayList<Enquiry> enquiries;
            while (true) {
                int projectIndex = view.promptProjectSelection(projectList);
                if (projectIndex == -1) {
                    return;
                }
                if (projectIndex < 0 || projectIndex >= projectList.size()) {
                    view.displayError("Invalid selection. Please try again.");
                    continue;
                }
                Project selectedProject = projectList.get(projectIndex);
                enquiries = projectEnquiriesMap.get(selectedProject);
                if (enquiries.isEmpty()) {
                    view.displayInfo("No enquiries made for the selected project.");
                    continue;
                }
                while (true) {
                    view.displayEnquiries(enquiries);
                    int enquiryIndex = view.promptEnquirySelection(enquiries.size());
                    if (enquiryIndex == -1) break;
                    if (enquiryIndex < 0 || enquiryIndex >= enquiries.size()) {
                        view.displayError("Invalid selection. Please try again.");
                        continue;
                    }
                    Enquiry selectedEnquiry = enquiries.get(enquiryIndex);
                    String newText = view.promptReplyText();
                    replyEnquiry(selectedEnquiry, newText);
                }
            }
        } catch (IOException e) {
            view.displayError("An error occurred while retrieving project enquiries, contact admin if error persist");
        }
    }
}