package controllers;

import java.util.ArrayList;
import models.Enquiry;
import models.HDBOfficer;
import models.Project;

/**
 * Interface for Officer Enquiry Controller.
 */
public interface IOfficerEnquiryController {
    /**
     * Replies to an enquiry with a given message.
     *
     * @param enquiry The enquiry to reply to.
     * @param message The reply message.
     */
    void replyEnquiry(Enquiry enquiry, String message);

    /**
     * Retrieves all enquiries related to a specific project.
     *
     * @param project The project whose enquiries are to be retrieved.
     * @return A list of enquiries related to the project.
     */
    public ArrayList<Enquiry> getProjectEnquiries(Project project);

    /**
     * Displays the enquiry action menu for a given officer.
     *
     * @param officer The officer for whom the menu is displayed.
     */
    void enquiryActionMenu(HDBOfficer officer);
}