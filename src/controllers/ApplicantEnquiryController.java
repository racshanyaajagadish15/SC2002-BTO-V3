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
    private final ApplicantEnquiryView view;

    public ApplicantEnquiryController() {
        this.view = new ApplicantEnquiryView();
    }

    @Override
    public void enquiryActionMenu(Applicant applicant) {
        try {
            ArrayList<Enquiry> enquiries = Enquiry.getEnquiriesByNricDB(applicant.getNric());
            Map<Project, ArrayList<Enquiry>> projectEnquiriesMap = mapEnquiriesToProjects(enquiries);
            view.showEnquiriesMenu(projectEnquiriesMap);
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
            k -> new ArrayList<>()
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

    public void editEnquiry(Enquiry enquiry, String newText) {
        try {
            enquiry.setEnquiry(newText);
            enquiry.setEnquiryDate(new Date());
            boolean success = Enquiry.updateEnquiryDB(enquiry);
            if (!success) {
                System.out.println("Failed to update the enquiry in the database.");
            } else {
                System.out.println("Enquiry updated successfully.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while editting to enquiries, contact admin if error persist");
        }
    }

    public boolean deleteEnquiry(int id) {
        try {
            Enquiry.deleteEnquiryDB(id);
            return true;
        } catch (NumberFormatException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }
}