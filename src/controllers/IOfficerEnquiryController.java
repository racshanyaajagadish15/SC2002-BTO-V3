package controllers;

import java.util.ArrayList;
import models.Enquiry;
import models.HDBOfficer;
import models.Project;

public interface IOfficerEnquiryController {
    void replyEnquiry(Enquiry enquiry, String message);
    ArrayList<Enquiry> getProjectEnquiries(Project project);
    void enquiryActionMenu(HDBOfficer officer);
}