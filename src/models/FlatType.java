package models;
public class FlatType {
    private String flatType;
    private int numFlats;
    private double pricePerFlat;

    /**
     * Constructor for FlatType
     * @param flatType The type of flat (e.g., "2-bedroom", "3-bedroom")
     * @param numFlats The number of flats available of this type
     * @param pricePerFlat The price per flat
     */
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
}