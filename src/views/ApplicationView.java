package views;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import enums.FilterIndex;
import enums.ApplicationStatus;
import models.Application;
import models.FlatType;
import models.Project;
import utilities.ScannerUtility;

public class ApplicationView implements IDisplayResult {

    /**
     * Prompts the user to select a project from a list of applicable projects.
     * @param applicableProjects
     * @return
     */
    public int promptProjectSelection(ArrayList<Project> applicableProjects) {
        System.out.println("\n=========================================");
        System.out.println("            APPLY FOR PROJECT            ");
        System.out.println("=========================================");
        for (int i = 0; i < applicableProjects.size(); i++) {
            System.out.println("No. " + (i + 1));
            System.out.println("Name: " + applicableProjects.get(i).getProjectName());
            System.out.println("Neighborhood: " + applicableProjects.get(i).getNeighborhood());
            System.out.println("Applications Period: " + applicableProjects.get(i).getApplicationOpeningDate() + " -> " + applicableProjects.get(i).getApplicationClosingDate());
            System.out.println("Flat Types: ");
            for (FlatType flatType : applicableProjects.get(i).getFlatTypes()) {
                System.out.println("~> " + flatType.getFlatType() + " : " + flatType.getNumFlats() + " units available ($" + flatType.getPricePerFlat() + ")");
            }
            System.out.println("-----------------------------------------");
        }
        System.out.print("\nSelect a project number to enquire or apply (0 to go back): ");
        try {
            int projectIndex = ScannerUtility.SCANNER.nextInt() - 1;
            ScannerUtility.SCANNER.nextLine();
            return projectIndex;
        } catch (InputMismatchException e) {
            ScannerUtility.SCANNER.nextLine();
            return -2; // Indicate invalid input
        }
    }

    /**
     * Prompts the user to select an action for a project.
     * @return option
     */
    public int promptProjectAction() {
        while (true) {
            try {
                System.out.println("\nActions: ");
                System.out.println("1. Submit Enquiry");
                System.out.println("2. Submit Application");
                System.out.println("0. Back ");
                System.out.print("\nSelect action: ");
                int action = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine();
                if (action >= 0 && action <= 2) {
                    return action;
                }
                displayError("Invalid selection. Please try again.");
            } catch (InputMismatchException e) {
                ScannerUtility.SCANNER.nextLine();
                displayError("Invalid selection. Please try again.");
            }
        }
    }

    /**
     * Prompts the user to select a flat type from a list of available flat types.
     * @param flatTypes
     * @return option
     */
    public int promptFlatTypeSelection(List<FlatType> flatTypes) {
        int i = 0;
        System.out.println("\nFlat Types:");
        for (FlatType flatType : flatTypes) {
            System.out.println((i + 1) + ". " + flatType.getFlatType() + " : " + flatType.getNumFlats() + " units available ($" + flatType.getPricePerFlat() + ")");
            i++;
        }
        System.out.println("0. Back");
        System.out.print("\nSelect a flat type to apply: ");
        try {
            int flatTypeIndex = ScannerUtility.SCANNER.nextInt() - 1;
            ScannerUtility.SCANNER.nextLine();
            return flatTypeIndex;
        } catch (InputMismatchException e) {
            ScannerUtility.SCANNER.nextLine();
            return -2;
        }
    }

    /**
     * Prompts the user to confirm the application submission.
     * @return true if confirmed, false otherwise
     */
    public boolean promptApplicationConfirmation() {
        while (true) {
            System.out.println("\nNote: Only 1 application can be made and it cannot be modified. ");
            System.out.println("Confirm the application?");
            System.out.println("1. Yes");
            System.out.println("2. No");
            System.out.print("\nEnter option: ");
            try {
                int confirmation = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine();
                if (confirmation == 1) return true;
                if (confirmation == 2) return false;
                displayError("Invalid selection. Please try again.");
            } catch (InputMismatchException e) {
                ScannerUtility.SCANNER.nextLine();
                displayError("Invalid selection. Please try again.");
            }
        }
    }

    /**
     * Prompts the user to enter a reason for application withdrawal.
     * @param filters
     * @return option
     */
    public int showFilterMenu(List<String> filters) {
        System.out.println("\n=========================================");
        System.out.println("             FILTER PROJECTS             ");
        System.out.println("=========================================");
        System.out.println("Current filters");
        System.out.println("Project: " + (filters.get(FilterIndex.PROJECT_NAME.getIndex()).isEmpty() ? " - " : filters.get(FilterIndex.PROJECT_NAME.getIndex())));
        System.out.println("Neighbourhood: " + (filters.get(FilterIndex.NEIGHBOURHOOD.getIndex()).isEmpty() ? " - " : filters.get(FilterIndex.NEIGHBOURHOOD.getIndex())));
        System.out.println("Price start: " + (filters.get(FilterIndex.PRICE_START.getIndex()).isEmpty() ? " - " : "$" + filters.get(FilterIndex.PRICE_START.getIndex())));
        System.out.println("Price end: " + (filters.get(FilterIndex.PRICE_END.getIndex()).isEmpty() ? " - " : "$" + filters.get(FilterIndex.PRICE_END.getIndex())));
        System.out.println("Flat type: " + (filters.get(FilterIndex.FLAT_TYPE.getIndex()).isEmpty() ? " - " : filters.get(FilterIndex.FLAT_TYPE.getIndex())));
        System.out.println("=========================================");
        System.out.println("1. Set project name search");
        System.out.println("2. Set neighbourhood name search");
        System.out.println("3. Set minimum price");
        System.out.println("4. Set maximum price");
        System.out.println("5. Set flat type");
        System.out.println("6. Clear filters");
        System.out.println("7. View projects sorted by project name");
        System.out.println("8. View projects sorted by neighbourhood");
        System.out.println("9. View projects sorted by price");
        System.out.println("0. Exit");
        System.out.println("=========================================");
        System.out.print("\nSelect an option: ");
        try {
            int option = ScannerUtility.SCANNER.nextInt();
            ScannerUtility.SCANNER.nextLine();
            return option;
        } catch (InputMismatchException e) {
            ScannerUtility.SCANNER.nextLine();
            return -1;
        }
    }

    /**
     * Prompt project name filter
     * @return filter
     */
    public String promptProjectNameFilter() {
        System.out.print("\nEnter project name (Nothing to clear filter): ");
        return ScannerUtility.SCANNER.nextLine();
    }
    /**
     * Prompt neighbourhood name filter
     * @return filter
     */
    public String promptNeighbourhoodFilter() {
        System.out.print("\nEnter neighbourhood name (Nothing to clear filter): ");
        return ScannerUtility.SCANNER.nextLine();
    }
    /**
     * Prompt minimum price filter
     * @return filter
     */
    public Double promptMinPriceFilter() {
        while (true){
        System.out.print("\nEnter minimum price (0 to clear filter): ");
            try {
                double price = ScannerUtility.SCANNER.nextDouble();
                ScannerUtility.SCANNER.nextLine();
                return price;
            } catch (InputMismatchException e) {
                ScannerUtility.SCANNER.nextLine();
                displayError("Invalid Price. Please try again.");
            }
        }
    }
    /**
     * Prompt maximum price filter
     * @return filter
     */
    public Double promptMaxPriceFilter() {
        while (true){
            System.out.print("\nEnter maximum price (0 to clear filter): ");
            try {
                double price = ScannerUtility.SCANNER.nextDouble();
                ScannerUtility.SCANNER.nextLine();
                return price;
            } catch (InputMismatchException e) {
                ScannerUtility.SCANNER.nextLine();
                displayError("Invalid Price. Please try again.");
            }
        }
    }
    /**
     * Prompt flat type filter
     * @return filter
     */
    public int promptFlatTypeFilter() {
        while (true) {
            try {
                System.out.println("\nApplicable Flat Types:");
                System.out.println("1. 2~ROOM");
                System.out.println("2. 3~ROOM");
                System.out.println("3. Clear Filter");
                System.out.print("\nEnter Option: ");
                int selectedFlat = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine();
                if (selectedFlat >= 1 && selectedFlat <= 3) {
                    return selectedFlat;
                }
                displayInfo("Invalid option.");
            } catch (InputMismatchException e) {
                ScannerUtility.SCANNER.nextLine();
                displayInfo("Invalid option.");
            }
        }
    }
    /**
     * Prompt sort order
     * @return filter
     */
    public int promptSortOrder() {
        while (true) {
            try {
                System.out.println("\nHow would you like it sorted?");
                System.out.println("1. Ascending");
                System.out.println("2. Descending");
                System.out.print("\nEnter Option: ");
                int order = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine();
                if (order == 1 || order == 2) {
                    return order;
                }
                displayError("Invalid selection. Please try again.");
            } catch (InputMismatchException e) {
                ScannerUtility.SCANNER.nextLine();
                displayError("Invalid . Please try again.");
            }
        }
    }

    /**
     * Prompts the user to enter an option for viewing projects to apply for.
     * @return option
     */
    public int showApplicationMenuPrompt() {
        System.out.println("\n=========================================");
        System.out.println("              VIEW PROJECTS              ");
        System.out.println("=========================================");
        System.out.println("1. View all applicable projects");
        System.out.println("2. Search and filter for projects");
        System.out.println("0. Exit");
        System.out.println("=========================================");
        System.out.print("\nSelect an option: ");
        try {
            int option = ScannerUtility.SCANNER.nextInt();
            ScannerUtility.SCANNER.nextLine();
            return option;
        } catch (InputMismatchException e) {
            ScannerUtility.SCANNER.nextLine();
            return -1;
        }
    }

    /**
     * Displays the details of a specific application.
     * @param application
     * @return option
     */
    public int showApplicationDetails(Application application) {
        while (true){
            System.out.println("\n=========================================");
            System.out.println("           APPLICATION DETAILS           ");
            System.out.println("=========================================");
            System.out.println("Project Name: " + application.getProject().getProjectName());
            System.out.println("Project Neighbourhood: " + application.getProject().getNeighborhood());
            String flatTypeName = application.getFlatType();
            double flatTypePrice = application.getProject().getFlatTypes().stream()
                    .filter(flatType -> flatType.getFlatType().equals(flatTypeName))
                    .map(flatType -> flatType.getPricePerFlat())
                    .findFirst()
                    .orElse(0.0);
            System.out.println("Application Flat Type: " + flatTypeName + " ($" + flatTypePrice + ")");
            System.out.println("Application Status: " + application.getApplicationStatus());
            System.out.println("=========================================");
            int option = -1;
            if (application.getApplicationStatus().equals(ApplicationStatus.WITHDRAWAL_PENDING.getStatus()) ||
                application.getApplicationStatus().equals(ApplicationStatus.WITHDRAWAL_SUCCESSFUL.getStatus()) ||
                application.getApplicationStatus().equals(ApplicationStatus.WITHDRAWAL_UNSUCCESSFUL.getStatus())) {
                try {
                    System.out.println("Actions:");
                    System.out.println("0. Back");
                    System.out.print("\nEnter Option: ");
                    option = ScannerUtility.SCANNER.nextInt();
                    ScannerUtility.SCANNER.nextLine();
                    if (option == 0) {
                        return 0;
                    }
                    displayError("Invalid option. Please try again");
                } catch (InputMismatchException e) {
                    ScannerUtility.SCANNER.nextLine();
                }

            } else {
                try {
                    System.out.println("Actions:");
                    System.out.println("1. Withdraw Application");
                    System.out.println("0. Back");
                    System.out.print("\nEnter Option: ");
                    option = ScannerUtility.SCANNER.nextInt();
                    ScannerUtility.SCANNER.nextLine();
                    if (option == 1 || option == 0) {
                        return option;
                    }
                    displayError("Invalid option. Please try again");
                } catch (InputMismatchException e) {
                    ScannerUtility.SCANNER.nextLine();
                }
            }
        }
    }
}