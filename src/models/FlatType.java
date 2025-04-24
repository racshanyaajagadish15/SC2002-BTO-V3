package models;

import java.util.ArrayList;

import utilities.LoggerUtility;
import utilities.ScannerUtility;

public class FlatType {
    private String flatType;
    private int numFlats;
    private double pricePerFlat;

    /**
     * Constructor for FlatType
     * @param flatType The type of flat (e.g., "2-bedroom", "3-bedroom")
     * @param numFlats The number of flats available of this type
    * @param pricePerFlat The price of this flat type
     */

    public FlatType(){

    }
    public FlatType(String flatType, int numFlats, double pricePerFlat) {
        validateFlatType(flatType);
        validateNumFlats(numFlats);

        this.flatType = flatType.trim().toUpperCase();
        this.numFlats = numFlats;
        this.pricePerFlat = pricePerFlat;
    }

    // Validation methods
    private void validateFlatType(String flatType) {
        if (flatType == null || flatType.trim().isEmpty()) {
            throw new IllegalArgumentException("Flat type cannot be null or empty");
        }
        // Normalize the flat type to uppercase and trim spaces
        String normalizedType = flatType.trim().toUpperCase();
        if (!normalizedType.equals("2-ROOM") && !normalizedType.equals("3-ROOM")) {
            throw new IllegalArgumentException("Invalid flat type. Only '2-ROOM' or '3-ROOM' are allowed.");
        }
    }

    private void validateNumFlats(int numFlats) {
        if (numFlats < 0) {
            throw new IllegalArgumentException("Number of flats cannot be negative");
        }
    }

    public String getFlatType() {
        return this.flatType;
    }

    public int getNumFlats() {
        return this.numFlats;
    }

    public double getPricePerFlat() {
        return this.pricePerFlat;
    }

    @Override
    public String toString() {
        return flatType + ": " + numFlats + " units, $" + String.format("%,.2f", pricePerFlat);
    }

    public void setNumFlats(int numFlats) {
        validateNumFlats(numFlats);
        this.numFlats = numFlats;
    }
    public void setPricePerFlat(double pricePerFlat) {
        if (pricePerFlat < 0) {
            throw new IllegalArgumentException("Price per flat cannot be negative");
        }
        this.pricePerFlat = pricePerFlat;
    }
    public void setFlatType(String flatType) {
        validateFlatType(flatType);
        this.flatType = flatType.trim().toUpperCase();
    }

    public void addFlatType(ArrayList<FlatType> flatTypes) {
        System.out.print("Enter Flat Type Name (e.g., 2-Room, 3-Room): ");
        String flatTypeName = ScannerUtility.SCANNER.nextLine();

        int numUnits;
        while (true) {
            System.out.print("Enter Number of Units: ");
            if (ScannerUtility.SCANNER.hasNextInt()) {
                numUnits = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine(); // Consume newline
                if (numUnits > 0) break; // Valid input
                else System.out.println("[ERROR] Number of units must be greater than 0.");
            } else {
                System.out.println("[ERROR] Please enter a valid number.");
                ScannerUtility.SCANNER.nextLine(); // Clear invalid input
            }
        }

        double pricePerFlat;
        while (true) {
            System.out.print("Enter Price per Flat: ");
            if (ScannerUtility.SCANNER.hasNextDouble()) {
                pricePerFlat = ScannerUtility.SCANNER.nextDouble();
                ScannerUtility.SCANNER.nextLine(); // Consume newline
                if (pricePerFlat > 0) break; // Valid input
                else System.out.println("[ERROR] Price per flat must be greater than 0.");
            } else {
                System.out.println("[ERROR] Please enter a valid price.");
                ScannerUtility.SCANNER.nextLine(); // Clear invalid input
            }
        }

        flatTypes.add(new FlatType(flatTypeName, numUnits, pricePerFlat));
        LoggerUtility.logInfo("Added Flat Type: " + flatTypeName + " with " + numUnits + " units at $" + pricePerFlat);
        System.out.println("Flat Type added successfully.");
    }

    public void editExistingFlatType(ArrayList<FlatType> flatTypes) {
        if (flatTypes.isEmpty()) {
            System.out.println("[ERROR] No flat types available to edit.");
            return;
        }
    
        // Display existing flat types
        System.out.println("Existing Flat Types:");
        for (int i = 0; i < flatTypes.size(); i++) {
            FlatType ft = flatTypes.get(i);
            System.out.println((i + 1) + ". " + ft.getFlatType() + " - Units: " + ft.getNumFlats() + ", Price: $" + ft.getPricePerFlat());
        }
    
        int indexToEdit;
        while (true) {
            System.out.print("Enter the index of the Flat Type to edit (1-based): ");
            if (ScannerUtility.SCANNER.hasNextInt()) {
                indexToEdit = ScannerUtility.SCANNER.nextInt() - 1;
                ScannerUtility.SCANNER.nextLine(); // Consume newline
                if (indexToEdit >= 0 && indexToEdit < flatTypes.size()) break; // Valid index
                else System.out.println("[ERROR] Invalid index. Please try again.");
            } else {
                System.out.println("[ERROR] Please enter a valid index.");
                ScannerUtility.SCANNER.nextLine(); // Clear invalid input
            }
        }
    
        FlatType flatType = flatTypes.get(indexToEdit);
    
        System.out.print("Enter new Flat Type Name (current: " + flatType.getFlatType() + "): ");
        String newFlatTypeName = ScannerUtility.SCANNER.nextLine();
        if (!newFlatTypeName.isBlank()) {
            flatType.setFlatType(newFlatTypeName);
        }
    
        while (true) {
            System.out.print("Enter new Number of Units (current: " + flatType.getNumFlats() + "): ");
            if (ScannerUtility.SCANNER.hasNextInt()) {
                int newNumUnits = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine(); // Consume newline
                if (newNumUnits > 0) {
                    flatType.setNumFlats(newNumUnits);
                    break;
                } else {
                    System.out.println("[ERROR] Number of units must be greater than 0.");
                }
            } else {
                System.out.println("[ERROR] Please enter a valid number.");
                ScannerUtility.SCANNER.nextLine(); // Clear invalid input
            }
        }
    
        while (true) {
            System.out.print("Enter new Price per Flat (current: $" + flatType.getPricePerFlat() + "): ");
            if (ScannerUtility.SCANNER.hasNextDouble()) {
                double newPricePerFlat = ScannerUtility.SCANNER.nextDouble();
                ScannerUtility.SCANNER.nextLine(); // Consume newline
                if (newPricePerFlat > 0) {
                    flatType.setPricePerFlat(newPricePerFlat);
                    break;
                } else {
                    System.out.println("[ERROR] Price per flat must be greater than 0.");
                }
            } else {
                System.out.println("[ERROR] Please enter a valid price.");
                ScannerUtility.SCANNER.nextLine(); // Clear invalid input
            }
        }
    
        LoggerUtility.logInfo("Updated Flat Type: " + flatType.getFlatType());
        System.out.println("Flat Type updated successfully.");
    }

    public void removeFlatType(ArrayList<FlatType> flatTypes) {
        if (flatTypes.isEmpty()) {
            System.out.println("[ERROR] No flat types available to remove.");
            return;
        }
    
        // Display existing flat types
        System.out.println("Existing Flat Types:");
        for (int i = 0; i < flatTypes.size(); i++) {
            FlatType ft = flatTypes.get(i);
            System.out.println((i + 1) + ". " + ft.getFlatType() + " - Units: " + ft.getNumFlats() + ", Price: $" + ft.getPricePerFlat());
        }
    
        int indexToRemove;
        while (true) {
            System.out.print("Enter the index of the Flat Type to remove (1-based): ");
            if (ScannerUtility.SCANNER.hasNextInt()) {
                indexToRemove = ScannerUtility.SCANNER.nextInt() - 1;
                ScannerUtility.SCANNER.nextLine(); // Consume newline
                if (indexToRemove >= 0 && indexToRemove < flatTypes.size()) {
                    FlatType removedFlatType = flatTypes.remove(indexToRemove);
                    LoggerUtility.logInfo("Removed Flat Type: " + removedFlatType.getFlatType());
                    System.out.println("Flat Type removed successfully.");
                    break;
                } else {
                    System.out.println("[ERROR] Invalid index. Please try again.");
                }
            } else {
                System.out.println("[ERROR] Please enter a valid index.");
                ScannerUtility.SCANNER.nextLine(); // Clear invalid input
            }
        }
    }
}