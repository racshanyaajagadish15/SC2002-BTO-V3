package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    // Static methods for database operations

    /**
     * Create a new project in the database.
     * @param project The project to be created.
     * @return True if the project was successfully created, false otherwise.
     * @throws IOException If an I/O error occurs.
     */
    public static boolean createProjectDB(Project project) throws IOException {
        return ProjectDB.createProject(project);
    }

    /**
     * Retrieve all projects from the database.
     * @return A list of all projects.
     * @throws IOException If an I/O error occurs.
     */
    public static List<Project> getAllProjectsDB() throws IOException {
        return ProjectDB.getAllProjects();
    }

    /**
     * Retrieve projects managed by a specific HDB manager.
     * @param hdbManagerID The ID of the HDB manager.
     * @return A list of projects managed by the specified manager.
     * @throws IOException If an I/O error occurs.
     */
    public static List<Project> getProjectsByManagerDB(String hdbManagerID) throws IOException {
        return ProjectDB.getProjectsByManager(hdbManagerID);
    }

    /**
     * Update an existing project in the database.
     * @param updatedProject The updated project object.
     * @throws IOException If an I/O error occurs.
     */
    public static void updateProjectDB(Project updatedProject) throws IOException {
        if (updatedProject.getApplicationOpeningDate().after(updatedProject.getApplicationClosingDate())) {
            throw new IllegalArgumentException("Opening date must be before closing date");
        }

        if (updatedProject.getOfficerSlots() < 0) {
            throw new IllegalArgumentException("Officer slots cannot be negative");
        }

        ProjectDB.updateProject(updatedProject);
    }

    /**
     * Delete a project from the database by its name.
     * @param projectName The name of the project to delete.
     * @return True if the project was successfully deleted, false otherwise.
     * @throws IOException If an I/O error occurs.
     */
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

    /**
     * Filter projects based on visibility and optional filters.
     * @param filters A list of filters to apply.
     * @return A list of filtered projects.
     */
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

    // Instance methods

    /**
     * Check if the project has any applications.
     * @return True if the project has applications, false otherwise.
     */
    public boolean hasApplications() {
        try {
            return ApplicationDB.hasApplicationsForProject(this.projectID);
        } catch (IOException e) {
            throw new RuntimeException("Failed to check applications for project", e);
        }
    }

    /**
     * Check if the project matches a specific filter.
     * @param filter The filter to check against.
     * @return True if the project matches the filter, false otherwise.
     */
    private boolean matchesFilter(String filter) {
        String lowerFilter = filter.toLowerCase();
        return this.neighborhood.toLowerCase().contains(lowerFilter) ||
               this.projectName.toLowerCase().contains(lowerFilter) ||
               this.flatTypes.stream().anyMatch(ft -> ft.toString().toLowerCase().contains(lowerFilter));
    }

    // Getters and Setters

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