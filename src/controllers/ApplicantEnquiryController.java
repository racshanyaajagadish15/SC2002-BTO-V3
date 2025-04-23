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

public class ApplicantEnquiryController implements IApplicantEnquiryController {
    private final ApplicantEnquiryView view;

    public ApplicantEnquiryController() {
        this.view = new ApplicantEnquiryView();
    }

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

    private Map<Project, ArrayList<Enquiry>> mapEnquiriesToProjects(ArrayList<Enquiry> enquiries) {
        Map<Project, ArrayList<Enquiry>> projectEnquiriesMap = new HashMap<>();
        for (Enquiry enquiry : enquiries) {
            Project project = enquiry.getProject();
            projectEnquiriesMap.computeIfAbsent(
            projectEnquiriesMap.keySet().stream()
                .filter(p -> p.getProjectID() == project.getProjectID())
                .findFirst()
                .orElse(project),
            _ -> new ArrayList<>()
            ).add(enquiry);
        }
        return projectEnquiriesMap;
    }

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

    // Handles the create enquiry flow
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

    public boolean deleteEnquiry(int id) {
        try {
            Enquiry.deleteEnquiryDB(id);
            return true;
        } catch (NumberFormatException | IOException e) {
            return false;
        }
    }
}