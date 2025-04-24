package controllers;

import java.util.ArrayList;
import models.Enquiry;
import models.HDBOfficer;
import models.Project;

/**
 * Interface for Officer Enquiry Controller.
 */
public interface IOfficerEnquiryController {
    
    void replyEnquiry(Enquiry enquiry, String message);
    public ArrayList<Enquiry> getProjectEnquiries(Project project);
    void enquiryActionMenu(HDBOfficer officer);
}