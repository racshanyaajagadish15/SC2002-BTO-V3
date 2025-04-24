package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import enums.ApplicationStatus;
import enums.FilterIndex;
import enums.FlatTypeName;
import models.Applicant;
import models.Application;
import models.FlatType;
import models.Project;
import views.ApplicationView;

/**
 * Abstract controller for managing application-related actions.
 * Provides methods for handling application flows, project actions, and filtering logic.
 */

public abstract class AbstractApplicationController implements IApplicationController {

    /**
     * The view associated with this controller.
     */
    protected final ApplicationView view;

    /**
     * Constructor for AbstractApplicationController.
     * Initializes the view instance.
     */
    public AbstractApplicationController() {
        this.view = new ApplicationView();
    }
    
    /**
     * Abstract method to get applicable projects for a given applicant.
     * Must be implemented by subclasses to provide specific project filtering logic.
     *
     * @param applicant The applicant for whom to retrieve applicable projects.
     * @return A list of applicable projects for the given applicant.
     */
    public abstract ArrayList<Project> getApplicableProjects(Applicant applicant);

    /**
     * Abstract method to get the project ID for a given applicant.
     * Must be implemented by subclasses to provide specific project ID retrieval logic.
     *
     * @param applicant The applicant for whom to retrieve the project ID.
     * @return The project ID for the given applicant.
     */
    public void projectAction(Applicant applicant) {
        ArrayList<Project> applicableProjects = getApplicableProjects(applicant);
        showApplicationMenu(applicableProjects, applicant);
    }

    /**
     * Abstract method to show the application details for a given applicant.
     * Must be implemented by subclasses to provide specific application detail display logic.
     *
     * @param applicant The applicant for whom to show application details.
     */
    public void applicationAction(Applicant applicant) {
        showApplicationDetails(applicant);
    }

    /**
     * Submits an application for a given project and applicant.
     *
     * @param project The project for which to submit the application.
     * @param applicant The applicant submitting the application.
     * @param flatType The type of flat being applied for.
     * @return true if the application was successfully submitted, false otherwise.
     */
    public boolean submitApplication(Project project, Applicant applicant, FlatType flatType) {
        try {
            Application.createApplicationDB(applicant, project, ApplicationStatus.PENDING.getStatus(), flatType);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Withdraws an application for a given applicant.
     *
     * @param application The application to withdraw.
     * @return true if the withdrawal was successful, false otherwise.
     */
    public boolean withdrawApplication(Application application) {
        application.setApplicationStatus(ApplicationStatus.WITHDRAWAL_PENDING.getStatus());
        try {
            Application.updateApplicationDB(application);
            return true;
        } catch (IOException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Displays the application menu for a given applicant.
     * Allows the applicant to view applicable projects and apply for them.
     *
     * @param applicableProjects The list of applicable projects for the applicant.
     * @param applicant The applicant viewing the menu.
     */

    public void showApplicationMenu(ArrayList<Project> applicableProjects, Applicant applicant) {
        try {
            applicant.setApplication(Application.getApplicationByNricDB(applicant.getNric()));
        } catch (IOException e) {
            view.displayError("Application system currently unavailable, please contact admin if error persist!");
            return;
        }
        if (applicant.getApplication() != null) {
            view.displayInfo("You have an application. Unable to view projects.");
            return;
        } else if (applicableProjects == null || applicableProjects.size() == 0) {
            view.displayInfo("You are uneligible to apply for any projects!");
            return;
        }
        while (true) {
            int option = view.showApplicationMenuPrompt();
            switch (option) {
                case 0:
                    return;
                case 1:
                    if (showApplicableProjects(applicableProjects, applicant)) {
                        return;
                    }
                    break;
                case 2:
                    if (showApplicableFilteredProjects(applicableProjects, applicant)) {
                        return;
                    }
                    break;
                default:
                    view.displayError("Invalid selection. Please try again.");
                    break;
            }
        }
    }

    /**
     * Displays applicable projects for a given applicant.
     * Allows the applicant to select a project and apply for it or create an enquiry.
     *
     * @param applicableProjects The list of applicable projects for the applicant.
     * @param applicant The applicant viewing the projects.
     * @return true if the application was successfully submitted, false otherwise.
     */
    public boolean showApplicableProjects(ArrayList<Project> applicableProjects, Applicant applicant) {
        if (applicableProjects == null || applicableProjects.size() == 0) {
            view.displayInfo("You are uneligible to apply for any projects!");
            return false;
        }
        ApplicantEnquiryController enquiryController = new ApplicantEnquiryController();
        while (true) {
            int projectIndex = view.promptProjectSelection(applicableProjects);
            if (projectIndex == -1) {
                return false;
            }
            if (projectIndex < 0 || projectIndex >= applicableProjects.size()) {
                view.displayError("Invalid selection. Please try again.");
                continue;
            }
            int action = view.promptProjectAction();
            if (action == 0) {
                continue;
            }
            if (action == 1) {
                enquiryController.createEnquiryFlow(applicableProjects.get(projectIndex), applicant);
                continue;
            }
            // Application flow
            List<FlatType> flatTypes = applicableProjects.get(projectIndex).getFlatTypes();
            int flatTypeIndex;
            while (true) {
                flatTypeIndex = view.promptFlatTypeSelection(flatTypes);
                if (flatTypeIndex == -1) break;
                if (flatTypeIndex >= 0 && flatTypeIndex < flatTypes.size()) break;
                view.displayError("Invalid selection. Please try again.");
            }
            if (flatTypeIndex == -1) continue;
            boolean confirm = view.promptApplicationConfirmation();
            if (confirm) {
                Project selectedProject = applicableProjects.get(projectIndex);
                FlatType selectedFlatType = flatTypes.get(flatTypeIndex);
                if (submitApplication(selectedProject, applicant, selectedFlatType)) {
                    view.displaySuccess("Application have been submitted!");
                    return true;
                } else {
                    view.displayError("Application unsuccessful, contact admin if error persist.");
                }
            }
        }
    }

    /**
     * Displays a filtered list of applicable projects for a given applicant.
     * Allows the applicant to set filters and view the filtered projects.
     *
     * @param applicableProjects The list of applicable projects for the applicant.
     * @param applicant The applicant viewing the filtered projects.
     * @return true if the application was successfully submitted, false otherwise.
     */
    public boolean showApplicableFilteredProjects(ArrayList<Project> applicableProjects, Applicant applicant) {
        List<String> filters = applicant.getFilter();
        while (true) {
            int option = view.showFilterMenu(filters);
            ArrayList<Project> projectsToFilter = new ArrayList<>(applicableProjects);
            switch (option) {
                case 0:
                    return false;
                case 1:
                    filters.set(FilterIndex.PROJECT_NAME.getIndex(), view.promptProjectNameFilter());
                    break;
                case 2:
                    filters.set(FilterIndex.NEIGHBOURHOOD.getIndex(), view.promptNeighbourhoodFilter());
                    break;
                case 3: {
                    Double price = view.promptMinPriceFilter();
                    if (price == 0) {
                        filters.set(FilterIndex.PRICE_START.getIndex(), "");
                        break;
                    } else if (price < 0) {
                        view.displayError("Number cannot be negative. Setting failed.");
                        break;
                    }
                    String priceEnd = filters.get(FilterIndex.PRICE_END.getIndex());
                    if (!priceEnd.isEmpty() && Double.parseDouble(priceEnd) < price) {
                        view.displayError("Maximum price is smaller then minimum price. Setting failed.");
                        break;
                    }
                    filters.set(FilterIndex.PRICE_START.getIndex(), String.format("%.2f", price));
                    break;
                }
                case 4: {
                    Double price = view.promptMaxPriceFilter();
                    if (price == 0) {
                        filters.set(FilterIndex.PRICE_END.getIndex(), "");
                        break;
                    } else if (price < 0) {
                        view.displayError("Number cannot be negative. Setting failed.");
                        break;
                    }
                    String priceStart = filters.get(FilterIndex.PRICE_START.getIndex());
                    if (!priceStart.isEmpty() && Double.parseDouble(priceStart) > price) {
                        view.displayError("Minimum price is bigger then maximum price. Setting failed.");
                        break;
                    }
                    filters.set(FilterIndex.PRICE_END.getIndex(), String.format("%.2f", price));
                    break;
                }
                case 5: {
                    int selectedFlat = view.promptFlatTypeFilter();
                    if (selectedFlat == 1) {
                        filters.set(FilterIndex.FLAT_TYPE.getIndex(), FlatTypeName.TWO_ROOM.getflatTypeName());
                    } else if (selectedFlat == 2) {
                        filters.set(FilterIndex.FLAT_TYPE.getIndex(), FlatTypeName.THREE_ROOM.getflatTypeName());
                    } else if (selectedFlat == 3) {
                        filters.set(FilterIndex.FLAT_TYPE.getIndex(), "");
                    }
                    break;
                }
                case 6:
                    filters.clear();
                    filters.addAll(List.of("", "", "", "", ""));
                    view.displayInfo("Filters cleared.");
                    break;
                case 7:
                    Project.filterProject(projectsToFilter, filters);
                    if (projectsToFilter.size() == 0) {
                        view.displayInfo("No result from search");
                    } else {
                        Project.sortProjectByName(projectsToFilter);
                        if (showApplicableProjects(projectsToFilter, applicant)) {
                            return true;
                        }
                    }
                    break;
                case 8:
                    Project.filterProject(projectsToFilter, filters);
                    if (projectsToFilter.size() == 0) {
                        view.displayInfo("No result from search");
                    } else {
                        Project.sortProjectByNeighbourhood(projectsToFilter);
                        if (showApplicableProjects(projectsToFilter, applicant)) {
                            return true;
                        }
                    }
                    break;
                case 9:
                    Project.filterProject(projectsToFilter, filters);
                    if (projectsToFilter.size() == 0) {
                        view.displayInfo("No result from search");
                    } else {
                        int order = view.promptSortOrder();
                        if (order == 1) {
                            Project.sortProjectByPrice(projectsToFilter, true);
                        } else if (order == 2) {
                            Project.sortProjectByPrice(projectsToFilter, false);
                        } else {
                            view.displayError("Invalid selection. Please try again.");
                            break;
                        }
                        if (showApplicableProjects(projectsToFilter, applicant)) {
                            return true;
                        }
                    }
                    break;
                default:
                    view.displayError("Invalid selection. Please try again.");
                    break;
            }
        }
    }

    /**
     * Displays the application details for a given applicant.
     * Allows the applicant to view their application status and withdraw their application if applicable.
     *
     * @param applicant The applicant whose application details are to be displayed.
     */

    public void showApplicationDetails(Applicant applicant) {
        try {
            applicant.setApplication(Application.getApplicationByNricDB(applicant.getNric()));
        } catch (IOException e) {
            view.displayError("Application system currently unavailable, please contact admin if error persist!");
            return;
        }
        Application application = applicant.getApplication();
        if (application == null) {
            view.displayInfo("You do not have an application. Please apply for one and try again.");
            return;
        }
        while (true) {
            int option = view.showApplicationDetails(application);
            if (application.getApplicationStatus().equals(ApplicationStatus.WITHDRAWAL_PENDING.getStatus()) ||
                application.getApplicationStatus().equals(ApplicationStatus.WITHDRAWAL_SUCCESSFUL.getStatus()) ||
                application.getApplicationStatus().equals(ApplicationStatus.WITHDRAWAL_UNSUCCESSFUL.getStatus())) {
                if (option == 0) return;
            } else {
                if (option == 0) return;
                if (option == 1) {
                    if (withdrawApplication(application)) {
                        view.displaySuccess("Application withdrawal submitted.");
                        // Refresh details after withdrawal
                        showApplicationDetails(applicant);
                    } else {
                        view.displayError("Application withdrawal not sent due to error. If error persist, contact admin!");
                    }
                    return;
                }
            }
        }
    }
}