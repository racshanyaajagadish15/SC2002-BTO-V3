package enums;
import models.FlatType;

public enum FlatTypeSupporter {
    STUDIO, 
    ONE_ROOM, 
    TWO_ROOM, 
    THREE_ROOM;

    public static FlatType fromString(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Flat type name cannot be null or empty");
        }
        
        String normalized = name.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        
        try {
            FlatTypeSupporter type = valueOf(normalized);
            return new FlatType(type.name(), 0); 
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No enum constant for flat type: " + name);
        }
    }
}