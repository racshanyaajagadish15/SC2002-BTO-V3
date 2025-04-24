package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import databases.EnquiryDB;
/**
 * Enquiry class representing a Enquiry in the system.
 */
public class Enquiry {

    private int enquiryID;
    private String nric;
    private Project project;
    private String enquiry;
    private String reply;
    private Date enquiryDate;
    private Date replyDate;

    /**
     * 
     * @param enquiryID
     * @param nric
     * @param project
     * @param enquiry
     * @param reply
     * @param enquiryDate
     * @param replyDate
     */
    public Enquiry(int enquiryID, String nric, Project project, String enquiry, String reply, Date enquiryDate, Date replyDate) {
        this.enquiryID = enquiryID;
        this.nric = nric;
        this.project = project;
        this.enquiry = enquiry;
        this.reply = reply;
        this.enquiryDate = enquiryDate;
        this.replyDate = replyDate;
    }

    /**
     * Constructor for Enquiry
     * @param enquiry The enquiry text
     * @param nric The NRIC of the applicant
     * @param project The project associated with the enquiry
     */

    public Enquiry(String enquiry, String nric, Project project) {
        this.enquiry = enquiry;
        this.nric = nric;
        this.project = project;
        this.reply = "";
        this.enquiryDate = new Date();
        this.replyDate = null;
    }

    /*
     * createEnquiryDB method to create a new enquiry in the database.
     * This method takes an Enquiry object as a parameter and returns a boolean indicating success or failure.
     * @param enquiry The Enquiry object to be created in the database.
     * @return true if the enquiry was created successfully, false otherwise.
     * @throws NumberFormatException if there is an error in number formatting.
     * @throws IOException if there is an error in file handling.
     */
    public static boolean createEnquiryDB(Enquiry enquiry) throws NumberFormatException, IOException {
        return EnquiryDB.createEnquiry(enquiry);
    }

    /*
     * updateEnquiryDB method to update an existing enquiry in the database.
     * This method takes an Enquiry object as a parameter and returns a boolean indicating success or failure.
     * @param enquiry The Enquiry object to be updated in the database.
     * @return true if the enquiry was updated successfully, false otherwise.
     * @throws NumberFormatException if there is an error in number formatting.
     * @throws IOException if there is an error in file handling.
     */

    public static boolean updateEnquiryDB(Enquiry enquiry) throws NumberFormatException, IOException {
        return EnquiryDB.updateEnquiry(enquiry);
    }

    /*
     * deleteEnquiryDB method to delete an enquiry from the database.
     * This method takes an enquiry ID as a parameter and returns a boolean indicating success or failure.
     * @param ID The ID of the enquiry to be deleted from the database.
     * @return true if the enquiry was deleted successfully, false otherwise.
     * @throws NumberFormatException if there is an error in number formatting.
     * @throws IOException if there is an error in file handling.
     */

    public static boolean deleteEnquiryDB(int ID) throws NumberFormatException, IOException {
        return EnquiryDB.deleteEnquiryByID(ID);
    }

    /*
     * getEnquiryByNricDB method to retrieve an enquiry by NRIC from the database.
     * This method takes an NRIC as a parameter and returns an ArrayList of Enquiry objects.
     * @param nric The NRIC of the applicant whose enquiry is to be retrieved.
     * @return An ArrayList of Enquiry objects associated with the specified NRIC.
     * @throws NumberFormatException if there is an error in number formatting.
     * @throws IOException if there is an error in file handling.
     */

    public static ArrayList<Enquiry> getEnquiriesByNricDB(String nric) throws NumberFormatException, IOException {
        return EnquiryDB.getEnquiriesByNricDB(nric);
    }

    /*
     * getEnquiryByIDDB method to retrieve an enquiry by ID from the database.
     * This method takes an enquiry ID as a parameter and returns an Enquiry object.
     * @param ID The ID of the enquiry to be retrieved.
     * @return An Enquiry object associated with the specified ID.
     * @throws NumberFormatException if there is an error in number formatting.
     * @throws IOException if there is an error in file handling.
     */

    public static ArrayList<Enquiry> getAllEnquiriesDB() throws NumberFormatException, IOException {
        return EnquiryDB.getAllEnquiries();		
    }

    /**
     * Retrieve enquiries for a specific project.
     * 
     * @param project The project for which to retrieve enquiries.
     * @return A list of enquiries for the specified project.
     */
    public static ArrayList<Enquiry> getProjectEnquiries(Project project) {
        ArrayList<Enquiry> projectEnquiries = new ArrayList<>();
        try {
            ArrayList<Enquiry> allEnquiries = Enquiry.getAllEnquiriesDB();
            for (Enquiry enquiry : allEnquiries) {
                if (enquiry.getProject().getProjectID() == project.getProjectID()) {
                    projectEnquiries.add(enquiry);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while retrieving project enquiries: " + e.getMessage());
        }
        return projectEnquiries;
    }

    /**
     * Retrieve enquiries for a specific project by its ID.
     * 
     * @return The ID of an enquiry
     */

    public int getEnquiryID() {
        return this.enquiryID;
    }

    /*
     * @return the nric
     */
    public String getNric() {
        return this.nric;
    }

    /**
     * @return the projectID
     */
    public int getProjectID() {
        return this.project.getProjectID();
    }

    /**
     * @return the projectName
     */
    public Project getProject() {
        return this.project;
    }

    /**
     * @return the enquiry
     */
    public String getEnquiry() {
        return this.enquiry;
    }
    /**
     * @return the reply
     */
    public String getReply() {
        return this.reply;
    }
    /**
     * @return the enquiryDate
     */
    public Date getEnquiryDate() {
        return this.enquiryDate;
    }
    /**
     * @return the replyDate
     */
    public Date getReplyDate() {
        return this.replyDate;
    }

    /**
     * 
     * @param enquiryID
     */
    public void setEnquiryID(int enquiryID) {
        this.enquiryID = enquiryID;
    }

    /**
     * 
     * @param enquiry
     */
    public void setEnquiry(String enquiry) {
        this.enquiry = enquiry;
    }

    /**
     * 
     * @param reply
     */
    public void setReply(String reply) {
        this.reply = reply;
    }

    /**
     * 
     * @param enquiryDate
     */
    public void setEnquiryDate(Date enquiryDate) {
        this.enquiryDate = enquiryDate;
    }

    /**
     * 
     * @param replyDate
     */
    public void setReplyDate(Date replyDate) {
        this.replyDate = replyDate;
    }

    /**
     * 
     * @param nric
     */
    public void setProject(Project project) {
        this.project = project;
    }
}