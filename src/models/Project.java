package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.IOException;

import databases.ApplicationDB;
import databases.ProjectDB;

public class Project {

    private int projectID;
    private String projectName;
    private HDBManager projectManager;
    private boolean projectVisibility;
    private String neighborhood;
    private ArrayList<FlatType> flatTypes;
    private Date applicationOpeningDate;
    private Date applicationClosingDate;
    private int officerSlots;

    /**
     * Constructor for Project
     * @param projectID The unique ID of the project
     * @param projectName Name of the project
     * @param manager The HDB manager responsible for the project
     * @param neighborhood Neighborhood where the project is located
     * @param flatTypes List of flat types available in the project
     * @param openingDate Application opening date
     * @param closingDate Application closing date
     * @param officerSlots Number of officer slots available
     * @param visibility Visibility status of the project
     */
    public Project(int projectID, String projectName, HDBManager manager, String neighborhood, 
                  List<FlatType> flatTypes, Date openingDate, Date closingDate, 
                  int officerSlots, boolean visibility) {
        this.projectID = projectID;
        this.projectName = projectName;
        this.projectManager = manager;
        this.neighborhood = neighborhood;
        this.flatTypes = new ArrayList<>(flatTypes);
        this.applicationOpeningDate = openingDate;
        this.applicationClosingDate = closingDate;
        this.officerSlots = officerSlots;
        this.projectVisibility = visibility;
    }

    public static void createApplicationDB(Applicant applicant, Project project, String applicationStatus) {
        Date now = new Date();
        if (now.before(project.getApplicationOpeningDate())) {
            throw new IllegalStateException("Applications are not open yet");
        }
        if (now.after(project.getApplicationClosingDate())) {
            throw new IllegalStateException("Application period has ended");
        }

        if (!applicant.isEligibleForHDB()) {
            throw new IllegalArgumentException("Applicant is not eligible for HDB");
        }
        Application.createApplication(applicant, project, applicationStatus);
    }

    public static List<Project> getAllProjectsDB() throws IOException {
        return ProjectDB.getAllProjects();
    }

    public static List<Project> getHDBManagerProjectsDB(int hdbManagerID) throws IOException {
        return ProjectDB.getProjectsByManager(hdbManagerID);
    }

    public static List<Project> getFilteredProjectsDB(List<String> filters) {
        List<Project> allProjects;
        try {
            allProjects = ProjectDB.getAllProjects();
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch all projects from the database", e);
        }
        List<Project> filteredProjects = new ArrayList<>();

        for (Project project : allProjects) {
            if (!project.getProjectVisibility()) {
                continue; 
            }
            if (filters != null && !filters.isEmpty()) {
                boolean matchesAllFilters = true;
                for (String filter : filters) {
                    if (!project.matchesFilter(filter)) {
                        matchesAllFilters = false;
                        break;
                    }
                }
                if (!matchesAllFilters) continue;
            }

            filteredProjects.add(project);
        }

        return filteredProjects;
    }
	private boolean matchesFilter(String filter) {
        String lowerFilter = filter.toLowerCase();
        return this.neighborhood.toLowerCase().contains(lowerFilter) ||
               this.projectName.toLowerCase().contains(lowerFilter) ||
               this.flatTypes.stream().anyMatch(ft -> ft.toString().toLowerCase().contains(lowerFilter));
    }

    public static void updateProjectDB(Project updatedProject) throws IOException {
        if (updatedProject.getApplicationOpeningDate().after(updatedProject.getApplicationClosingDate())) {
            throw new IllegalArgumentException("Opening date must be before closing date");
        }

        if (updatedProject.getOfficerSlots() < 0) {
            throw new IllegalArgumentException("Officer slots cannot be negative");
        }

        ProjectDB.updateProject(updatedProject);
    }

    public static boolean deleteProjectDB(String projectName) throws IOException {
        Project project = ProjectDB.getProjectByName(projectName);
        if (project == null) {
            throw new IllegalArgumentException("Project not found");
        }

        // Prevent deletion if applications exist
        if (project.hasApplications()) {
            throw new IllegalStateException("Cannot delete project with existing applications");
        }

        return ProjectDB.deleteProject(projectName);
    }

    public boolean hasApplications() {
		try {
			return ApplicationDB.hasApplicationsForProject(this.projectID);
		} catch (IOException e) {
			throw new RuntimeException("Failed to check applications for project", e);
    }
}

    // Getters
    public int getProjectID() {
        return projectID;
    }

    public String getProjectName() {
        return projectName;
    }

    public HDBManager getProjectManager() {
        return projectManager;
    }

    public boolean getProjectVisibility() {
        return projectVisibility;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public List<FlatType> getFlatTypes() {
        return new ArrayList<>(flatTypes);
    }

    public Date getApplicationOpeningDate() {
        return applicationOpeningDate;
    }

    public Date getApplicationClosingDate() {
        return applicationClosingDate;
    }

    public int getOfficerSlots() {
        return officerSlots;
    }

    // Setters
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setProjectManager(HDBManager projectManager) {
        this.projectManager = projectManager;
    }

    public void setProjectVisibility(boolean projectVisibility) {
        this.projectVisibility = projectVisibility;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public void setFlatTypes(List<FlatType> flatTypes) {
        this.flatTypes = new ArrayList<>(flatTypes);
    }

    public void setApplicationOpeningDate(Date applicationOpeningDate) {
        this.applicationOpeningDate = applicationOpeningDate;
    }

    public void setApplicationClosingDate(Date applicationClosingDate) {
        this.applicationClosingDate = applicationClosingDate;
    }

    public void setOfficerSlots(int officerSlots) {
        this.officerSlots = officerSlots;
    }

    @Override
    public String toString() {
        return "Project{" +
                "projectID=" + projectID +
                ", projectName='" + projectName + '\'' +
                ", neighborhood='" + neighborhood + '\'' +
                ", flatTypes=" + flatTypes +
                ", openingDate=" + applicationOpeningDate +
                ", closingDate=" + applicationClosingDate +
                '}';
    }
}