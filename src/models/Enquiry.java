package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import databases.EnquiryDB;

public class Enquiry {

	private int enquiryID;
	private String nric;
	private int projectID;
	private String enquiry;
	private String reply;
	private Date enquiryDate;
	private Date replyDate;

	/**
	 * 
	 * @param enquiryID
	 * @param nric
	 * @param projectID
	 * @param enquiry
	 * @param reply
	 * @param enquiryDate
	 * @param replyDate
	 */
	public Enquiry(int enquiryID, String nric, int projectID, String enquiry, String reply, Date enquiryDate, Date replyDate) {
		this.enquiryID = enquiryID;
		this.nric = nric;
		this.projectID = projectID;
		this.enquiry = enquiry;
		this.reply = reply;
		this.enquiryDate = enquiryDate;
		this.replyDate = replyDate;
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

	public int getEnquiryID() {
		return this.enquiryID;
	}

	public String getNric() {
		return this.nric;
	}

	public int getProjectID() {
		return this.projectID;
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
}