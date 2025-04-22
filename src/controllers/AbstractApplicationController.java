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
import views.ApplicantEnquiryView;

public abstract class AbstractApplicationController implements IApplicationController {

    public abstract ArrayList<Project> getApplicableProjects(Applicant applicant);

    public void projectAction(Applicant applicant) {
        ApplicationView applicationView = new ApplicationView();
        ArrayList<Project> applicableProjects = getApplicableProjects(applicant);
        showApplicationMenu(applicableProjects, applicant, applicationView);
    }

    public void applicationAction(Applicant applicant) {
        ApplicationView applicationView = new ApplicationView();
        showApplicationDetails(applicant, applicationView);
    }

    public boolean submitApplication(Project project, Applicant applicant, FlatType flatType) {
        try {
            Application.createApplicationDB(applicant, project, ApplicationStatus.PENDING.getStatus(), flatType);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

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

    // --- Controller logic for menu and flows ---

    public void showApplicationMenu(ArrayList<Project> applicableProjects, Applicant applicant, ApplicationView applicationView) {
        try {
            applicant.setApplication(Application.getApplicationByNricDB(applicant.getNric()));
        } catch (IOException e) {
            applicationView.displayError("Application system currently unavailable, please contact admin if error persist!");
            return;
        }
        if (applicant.getApplication() != null) {
            applicationView.displayInfo("You have an application. Unable to view projects.");
            return;
        } else if (applicableProjects == null || applicableProjects.size() == 0) {
            applicationView.displayInfo("You are uneligible to apply for any projects!");
            return;
        }
        while (true) {
            int option = applicationView.showApplicationMenuPrompt();
            switch (option) {
                case 0:
                    return;
                case 1:
                    if (showApplicableProjects(applicableProjects, applicant, applicationView)) {
                        return;
                    }
                    break;
                case 2:
                    if (showApplicableFilteredProjects(applicableProjects, applicant, applicationView)) {
                        return;
                    }
                    break;
                default:
                    applicationView.displayError("Invalid selection. Please try again.");
                    break;
            }
        }
    }

    public boolean showApplicableProjects(ArrayList<Project> applicableProjects, Applicant applicant, ApplicationView applicationView) {
        if (applicableProjects == null || applicableProjects.size() == 0) {
            applicationView.displayInfo("You are uneligible to apply for any projects!");
            return false;
        }
        ApplicantEnquiryView enquiryView = new ApplicantEnquiryView();
        while (true) {
            int projectIndex = applicationView.promptProjectSelection(applicableProjects);
            if (projectIndex == -1) {
                return false;
            }
            if (projectIndex < 0 || projectIndex >= applicableProjects.size()) {
                applicationView.displayError("Invalid selection. Please try again.");
                continue;
            }
            int action = applicationView.promptProjectAction();
            if (action == 0) {
                continue;
            }
            if (action == 1) {
                enquiryView.showCreateEnquiry(applicableProjects.get(projectIndex), applicant);
                continue;
            }
            // Application flow
            List<FlatType> flatTypes = applicableProjects.get(projectIndex).getFlatTypes();
            int flatTypeIndex;
            while (true) {
                flatTypeIndex = applicationView.promptFlatTypeSelection(flatTypes);
                if (flatTypeIndex == -1) break;
                if (flatTypeIndex >= 0 && flatTypeIndex < flatTypes.size()) break;
                applicationView.displayError("Invalid selection. Please try again.");
            }
            if (flatTypeIndex == -1) continue;
            boolean confirm = applicationView.promptApplicationConfirmation();
            if (confirm) {
                Project selectedProject = applicableProjects.get(projectIndex);
                FlatType selectedFlatType = flatTypes.get(flatTypeIndex);
                if (submitApplication(selectedProject, applicant, selectedFlatType)) {
                    applicationView.displaySuccess("Application have been submitted!");
                    return true;
                } else {
                    applicationView.displayError("Application unsuccessful, contact admin if error persist.");
                }
            }
        }
    }

    public boolean showApplicableFilteredProjects(ArrayList<Project> applicableProjects, Applicant applicant, ApplicationView applicationView) {
        List<String> filters = applicant.getFilter();
        while (true) {
            int option = applicationView.showFilterMenu(filters);
            ArrayList<Project> projectsToFilter = new ArrayList<>(applicableProjects);
            switch (option) {
                case 0:
                    return false;
                case 1:
                    filters.set(FilterIndex.PROJECT_NAME.getIndex(), applicationView.promptProjectNameFilter());
                    break;
                case 2:
                    filters.set(FilterIndex.NEIGHBOURHOOD.getIndex(), applicationView.promptNeighbourhoodFilter());
                    break;
                case 3: {
                    Double price = applicationView.promptMinPriceFilter();
                    if (price == null) break;
                    if (price == 0) {
                        filters.set(FilterIndex.PRICE_START.getIndex(), "");
                        break;
                    } else if (price < 0) {
                        applicationView.displayError("Number cannot be negative. Setting failed.");
                        break;
                    }
                    String priceEnd = filters.get(FilterIndex.PRICE_END.getIndex());
                    if (!priceEnd.isEmpty() && Double.parseDouble(priceEnd) < price) {
                        applicationView.displayError("Maximum price is smaller then minimum price. Setting failed.");
                        break;
                    }
                    filters.set(FilterIndex.PRICE_START.getIndex(), String.format("%.2f", price));
                    break;
                }
                case 4: {
                    Double price = applicationView.promptMaxPriceFilter();
                    if (price == null) break;
                    if (price == 0) {
                        filters.set(FilterIndex.PRICE_END.getIndex(), "");
                        break;
                    } else if (price < 0) {
                        applicationView.displayError("Number cannot be negative. Setting failed.");
                        break;
                    }
                    String priceStart = filters.get(FilterIndex.PRICE_START.getIndex());
                    if (!priceStart.isEmpty() && Double.parseDouble(priceStart) > price) {
                        applicationView.displayError("Minimum price is bigger then maximum price. Setting failed.");
                        break;
                    }
                    filters.set(FilterIndex.PRICE_END.getIndex(), String.format("%.2f", price));
                    break;
                }
                case 5: {
                    int selectedFlat = applicationView.promptFlatTypeFilter();
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
                    break;
                case 7:
                    Project.filterProject(projectsToFilter, filters);
                    if (projectsToFilter.size() == 0) {
                        applicationView.displayInfo("No result from search");
                    } else {
                        Project.sortProjectByName(projectsToFilter);
                        if (showApplicableProjects(projectsToFilter, applicant, applicationView)) {
                            return true;
                        }
                    }
                    break;
                case 8:
                    Project.filterProject(projectsToFilter, filters);
                    if (projectsToFilter.size() == 0) {
                        applicationView.displayInfo("No result from search");
                    } else {
                        Project.sortProjectByNeighbourhood(projectsToFilter);
                        if (showApplicableProjects(projectsToFilter, applicant, applicationView)) {
                            return true;
                        }
                    }
                    break;
                case 9:
                    Project.filterProject(projectsToFilter, filters);
                    if (projectsToFilter.size() == 0) {
                        applicationView.displayInfo("No result from search");
                    } else {
                        int order = applicationView.promptSortOrder();
                        if (order == 1) {
                            Project.sortProjectByPrice(projectsToFilter, true);
                        } else if (order == 2) {
                            Project.sortProjectByPrice(projectsToFilter, false);
                        } else {
                            applicationView.displayError("Invalid selection. Please try again.");
                            break;
                        }
                        if (showApplicableProjects(projectsToFilter, applicant, applicationView)) {
                            return true;
                        }
                    }
                    break;
                default:
                    applicationView.displayError("Invalid selection. Please try again.");
                    break;
            }
        }
    }

    public void showApplicationDetails(Applicant applicant, ApplicationView applicationView) {
        try {
            applicant.setApplication(Application.getApplicationByNricDB(applicant.getNric()));
        } catch (IOException e) {
            applicationView.displayError("Application system currently unavailable, please contact admin if error persist!");
            return;
        }
        Application application = applicant.getApplication();
        if (application == null) {
            applicationView.displayInfo("You do not have an application. Please apply for one and try again.");
            return;
        }
        while (true) {
            int option = applicationView.showApplicationDetails(application);
            if (application.getApplicationStatus().equals(ApplicationStatus.WITHDRAWAL_PENDING.getStatus()) ||
                application.getApplicationStatus().equals(ApplicationStatus.WITHDRAWAL_SUCCESSFUL.getStatus()) ||
                application.getApplicationStatus().equals(ApplicationStatus.WITHDRAWAL_UNSUCCESSFUL.getStatus())) {
                if (option == 0) return;
            } else {
                if (option == 0) return;
                if (option == 1) {
                    if (withdrawApplication(application)) {
                        applicationView.displaySuccess("Application withdrawal submitted.");
                        // Refresh details after withdrawal
                        showApplicationDetails(applicant, applicationView);
                    } else {
                        applicationView.displayError("Application withdrawal not sent due to error. If error persist, contact admin!");
                    }
                    return;
                }
            }
        }
    }
}