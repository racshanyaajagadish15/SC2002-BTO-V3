package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Applicant;
import models.Enquiry;
import models.Project;
import views.ApplicantEnquiryView;


/**
 * This class is responsible for handling the enquiry process for applicants.
 * It implements the IApplicantEnquiryController interface and provides methods to manage enquiries.
 */
public class ApplicantEnquiryController implements IApplicantEnquiryController {
    
    /**
     * The view used for displaying information to the user.
     */
    private final ApplicantEnquiryView view;


    /**
     * Constructor for the ApplicantEnquiryController class.
     * Initializes the view used for displaying information to the user.
     */
    public ApplicantEnquiryController() {
        this.view = new ApplicantEnquiryView();
    }

    /**
     * This method retrieves a list of applicable projects for the given applicant.
     * It checks the applicant's marital status and age to determine the applicable flat types and projects.
     * 
     * @param applicant The applicant for whom to retrieve applicable projects.
     * @return A list of applicable projects for the applicant, or null if an error occurs.
     */
    @Override
    public void enquiryActionMenu(Applicant applicant) {
        try {
            ArrayList<Enquiry> enquiries = Enquiry.getEnquiriesByNricDB(applicant.getNric());
            Map<Project, ArrayList<Enquiry>> projectEnquiriesMap = mapEnquiriesToProjects(enquiries);
            if (projectEnquiriesMap.size() == 0) {
                view.displayInfo("You have not made any enquiries!");
                return;
            }
            List<Project> projectList = new ArrayList<>(projectEnquiriesMap.keySet());
            Project.sortProjectByName(projectList);

            while (true) {
                int projectIndex = view.showEnquiriesMenu(projectEnquiriesMap, projectList);
                if (projectIndex == -1) {
                    return;
                }
                if (projectIndex < 0 || projectIndex >= projectList.size()) {
                    view.displayError("Invalid selection. Please try again.");
                    continue;
                }
                Project selectedProject = projectList.get(projectIndex);
                ArrayList<Enquiry> enquiriesForProject = projectEnquiriesMap.get(selectedProject);

                if (enquiriesForProject.isEmpty()) {
                    view.displayInfo("No enquiries found for the selected project.");
                    continue;
                }

                while (true) {
                    int enquiryIndex = view.showEnquiriesList(enquiriesForProject);
                    if (enquiryIndex == -1) break;
                    if (enquiryIndex < 0 || enquiryIndex >= enquiriesForProject.size()) {
                        view.displayError("Invalid selection. Please try again.");
                        continue;
                    }
                    Enquiry selectedEnquiry = enquiriesForProject.get(enquiryIndex);
                    if (!selectedEnquiry.getReply().isEmpty()) {
                        view.displayInfo("This enquiry has already been replied to and cannot be modified.");
                        continue;
                    }
                    while (true) {
                        int option = view.showEnquiryOptions();
                        if (option == 1) {
                            String newText = view.promptNewEnquiryText();
                            if (newText.isBlank()){
                                view.displayInfo("Enquiry was not modifed.");
                                break;
                            }
                            editEnquiry(selectedEnquiry, newText);
                            break;
                        } else if (option == 2) {
                            boolean deleted = deleteEnquiry(selectedEnquiry.getEnquiryID());
                            if (deleted) {
                                enquiriesForProject.remove(enquiryIndex);
                                view.displaySuccess("Enquiry deleted successfully.");
                            } else {
                                view.displayError("Failed to delete enquiry.");
                            }
                            break;
                        } else if (option == 0) {
                            break;
                        } else {
                            view.displayError("Invalid option. Please try again.");
                        }
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            view.displayError("Cannot show enquiries due to error, contact admin if error persist.");
        }
    }

    /**
     * This method maps enquiries to their respective projects.
     * It creates a map where the key is the project and the value is a list of enquiries for that project.
     * 
     * @param enquiries The list of enquiries to be mapped to projects.
     * @return A map of projects to their respective enquiries.
     */

    private Map<Project, ArrayList<Enquiry>> mapEnquiriesToProjects(ArrayList<Enquiry> enquiries) {
        Map<Project, ArrayList<Enquiry>> projectEnquiriesMap = new HashMap<>();
        for (Enquiry enquiry : enquiries) {
            Project project = enquiry.getProject();
            Project existingProject = projectEnquiriesMap.keySet().stream()
                .filter(p -> p.getProjectID() == project.getProjectID())
                .findFirst()
                .orElse(project);
            projectEnquiriesMap.computeIfAbsent(existingProject, _ -> new ArrayList<>()).add(enquiry);
        }
        return projectEnquiriesMap;
    }

    /**
     * This method submits an enquiry to the database.
     * It creates a new enquiry in the database and returns true if successful, false otherwise.
     * 
     * @param enquiry The enquiry to be submitted.
     * @return True if the enquiry was successfully submitted, false otherwise.
     */

    @Override
    public boolean submitEnquiry(Enquiry enquiry) {
        try {
            boolean success = Enquiry.createEnquiryDB(enquiry);
            return success;
        } catch (Exception e) {
            view.displayError("An error occurred: " + e.getMessage());
            return false;
        }
    }

    /**
     * This This method creates a new enquiry flow for the given project and applicant.
     * 
     * @return A list of all enquiries.
     */
    public void createEnquiryFlow(Project project, Applicant applicant) {
        String enquiryText = view.showCreateEnquiry(project);
        if (enquiryText.trim().isEmpty()) {
            view.displayInfo("Enquiry was not created.");
            return;
        }
        Enquiry newEnquiry = new Enquiry(enquiryText, applicant.getNric(), project);
        boolean success = submitEnquiry(newEnquiry);
        if (success) {
            view.displaySuccess("Enquiry created successfully.");
        } else {
            view.displayError("Failed to create enquiry.");
        }
    }

    /**
     * This method edits an existing enquiry.
     * It updates the enquiry text and date in the database.
     * 
     * @param enquiry The enquiry to be edited.
     * @param newText The new text for the enquiry.
     */
    public void editEnquiry(Enquiry enquiry, String newText) {
        try {
            enquiry.setEnquiry(newText);
            enquiry.setEnquiryDate(new Date());
            boolean success = Enquiry.updateEnquiryDB(enquiry);
            if (!success) {
                view.displayError("Failed to update the enquiry in the database.");
            } else {
                view.displaySuccess("Enquiry updated successfully.");
            }
        } catch (IOException e) {
            view.displayError("An error occurred while editing enquiries, contact admin if error persist");
        }
    }

    /**
     * This method deletes an enquiry from the database.
     * 
     * @param id The ID of the enquiry to be deleted.
     * @return True if the enquiry was successfully deleted, false otherwise.
     */
    public boolean deleteEnquiry(int id) {
        try {
            Enquiry.deleteEnquiryDB(id);
            return true;
        } catch (NumberFormatException | IOException e) {
            return false;
        }
    }
}