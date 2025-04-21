package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import databases.EnquiryDB;

public class Enquiry {

    private int enquiryID;
    private String nric;
    private Project project; // Changed from projectID to Project
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

    public Enquiry(String enquiry, String nric, Project project) {
        this.enquiry = enquiry;
        this.nric = nric;
        this.project = project;
        this.reply = "";
        this.enquiryDate = new Date();
        this.replyDate = null;
    }

    public static boolean createEnquiryDB(Enquiry enquiry) throws NumberFormatException, IOException {
        return EnquiryDB.createEnquiry(enquiry);
    }

    public static boolean updateEnquiryDB(Enquiry enquiry) throws NumberFormatException, IOException {
        return EnquiryDB.updateEnquiry(enquiry);
    }

    public static boolean deleteEnquiryDB(int ID) throws NumberFormatException, IOException {
        return EnquiryDB.deleteEnquiryByID(ID);
    }

    public static ArrayList<Enquiry> getEnquiriesByNricDB(String nric) throws NumberFormatException, IOException {
        return EnquiryDB.getEnquiriesByNricDB(nric);
    }

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
                if (enquiry.getProjectID() == project.getProjectID()) {
                    projectEnquiries.add(enquiry);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while retrieving project enquiries: " + e.getMessage());
        }
        return projectEnquiries;
    }

    public int getEnquiryID() {
        return this.enquiryID;
    }

    public String getNric() {
        return this.nric;
    }

    public int getProjectID() {
        return this.project.getProjectID();
    }

    public Project getProject() {
        return this.project;
    }

    public String getEnquiry() {
        return this.enquiry;
    }

    public String getReply() {
        return this.reply;
    }

    public Date getEnquiryDate() {
        return this.enquiryDate;
    }

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

    public void setProject(Project project) {
        this.project = project;
    }
}