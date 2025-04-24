package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import databases.ApplicationDB;
import databases.ProjectDB;
import enums.FilterIndex;

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
     * Retrieve a singular project from the database.
     * @return a singular project.
     * @throws IOException If an I/O error occurs.
     */
    public static Project getProjectByIdDB(int id) throws IOException {
        return ProjectDB.getProjectsById(id);
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

        return ProjectDB.deleteProject(project);
    }

    public static void filterProject(ArrayList<Project> projects,  List<String> filters){
        ArrayList<Project> filteredProjects = new ArrayList<Project>();
        for (Project project : projects){
            // Check if filter is "" then check its filter content, skip if doesnt fit
            if (!filters.get(FilterIndex.PROJECT_NAME.getIndex()).isEmpty() && !project.getProjectName().toLowerCase().contains(filters.get(FilterIndex.PROJECT_NAME.getIndex()).toLowerCase())){
                continue;
            }
            else if (!filters.get(FilterIndex.NEIGHBOURHOOD.getIndex()).isEmpty() && !project.getNeighborhood().toLowerCase().contains(filters.get(FilterIndex.NEIGHBOURHOOD.getIndex()).toLowerCase())){
                continue;
            }
            else {
                // Check if price range is met
                String priceStartStr = filters.get(FilterIndex.PRICE_START.getIndex());
                String priceEndStr = filters.get(FilterIndex.PRICE_END.getIndex());
                
                if (!priceStartStr.isEmpty() || !priceEndStr.isEmpty()) {
                    double priceStart = priceStartStr.isEmpty() ? Double.MIN_VALUE : Double.parseDouble(priceStartStr);
                    double priceEnd = priceEndStr.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(priceEndStr);
					List<FlatType> filteredFlatTypes = new ArrayList<FlatType>();                    
                    for (FlatType flatType : project.getFlatTypes()) {
                        double price = flatType.getPricePerFlat();
                        if (price >= priceStart && price <= priceEnd) {
                            filteredFlatTypes.add(flatType);
                        }
                    }
                    if (filteredFlatTypes.size() == 0) continue;
                    project.setFlatTypes(filteredFlatTypes);
                }
                // Check flat type
                if (!filters.get(FilterIndex.FLAT_TYPE.getIndex()).isEmpty()){
                    List<FlatType> filteredFlatTypes = new ArrayList<FlatType>();                    
                    for (FlatType flatType : project.getFlatTypes()) {
                        if (flatType.getFlatType().equals(filters.get(FilterIndex.FLAT_TYPE.getIndex()))) {
                            filteredFlatTypes.add(flatType);
                        }
                    }
                    if (filteredFlatTypes.size() == 0) continue;
                    project.setFlatTypes(filteredFlatTypes);
                }

                // Matches all criterias
                filteredProjects.add(project);
            }
        }
        projects.clear();
        projects.addAll(filteredProjects);
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
    
    /**
     * Sort projects based by project name alphabetically.
     * @param projects A list of projects to apply.
     */
    public static void sortProjectByName(List<Project> projects){
        Collections.sort(projects, (p1, p2) -> p1.getProjectName().compareToIgnoreCase(p2.getProjectName()));
    }

    /**
     * Sort projects based by neighbourhood alphabetically.
     * @param projects A list of projects to apply.
     */
    public static void sortProjectByNeighbourhood(List<Project> projects) {
        Collections.sort(projects, (p1, p2) -> p1.getNeighborhood().compareToIgnoreCase(p2.getNeighborhood()));
    }

    /**
     * Sort projects based by price.
     * @param projects A list of projects to apply.
     */
    public static void sortProjectByPrice(List<Project> projects, Boolean isAscending) {
        List<Project> expandedProjects = new ArrayList<>();
        // Split projects by flat types
        for (Project project : projects) {
            for (FlatType flatType : project.getFlatTypes()) {
                Project splitProject = new Project(
                    project.getProjectID(),
                    project.getProjectName(),
                    project.getProjectManager(),
                    project.getNeighborhood(),
                    Collections.singletonList(flatType),
                    project.getApplicationOpeningDate(),
                    project.getApplicationClosingDate(),
                    project.getOfficerSlots(),
                    project.getProjectVisibility()
                );
                expandedProjects.add(splitProject);
            }
        }
        // Sort the expanded projects by price
        Collections.sort(expandedProjects, (p1, p2) -> {
            double price1 = p1.getFlatTypes().get(0).getPricePerFlat();
            double price2 = p2.getFlatTypes().get(0).getPricePerFlat();
            return isAscending ? Double.compare(price1, price2) : Double.compare(price2, price1);
        });

        // Clear the original list and add the sorted projects
        projects.clear();
        projects.addAll(expandedProjects);
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

    /**
     * Add an officer's name to the ManagerList for this project.
     * 
     * @param officerName The name of the officer to add.
     */
    public void addOfficerToManagerList(String officerName) {
        if (this.projectManager == null) {
            throw new IllegalStateException("Project does not have an assigned manager.");
        }

        // Ensure the ManagerList is initialized
        if (this.projectManager.getManagedProjects() == null) {
            this.projectManager.setManagedProjects(new ArrayList<>());
        }

        // Add the officer's name to the ManagerList
        List<Project> managedProjects = this.projectManager.getManagedProjects();
        if (!managedProjects.contains(this)) {
            managedProjects.add(this);
        }

        System.out.println("[SUCCESS] Officer " + officerName + " added to the ManagerList for Project: " + this.projectName);
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

    public ArrayList<FlatType> getFlatTypes() {
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

    public void setProjectID(int projectID) {
        this.projectID = projectID;
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