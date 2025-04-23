package controllers;

import java.util.ArrayList;

import models.HDBOfficer;
import models.Project;

public interface IOfficerJoinProjectController {

	/**
	 * 
	 * @param project
	 */
	boolean registerForProject(HDBOfficer officer, Project project);

	ArrayList<Project> getRegistrableProjects(HDBOfficer officer);
}