package enums;
import models.FlatType;

public enum FlatTypeName {
    TWO_ROOM("2-ROOM"), 
    THREE_ROOM("3-ROOM");

    private final String flatTypeName;

    FlatTypeName(String flatTypeName){
        this.flatTypeName = flatTypeName;
    }
    public String getflatTypeName() {
        return flatTypeName;
    }

    // public static FlatType fromString(String name) {
    //     if (name == null || name.trim().isEmpty()) {
    //         throw new IllegalArgumentException("Flat type name cannot be null or empty");
    //     }
        
    //     String normalized = name.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        
    //     try {
    //         FlatTypeSupporter type = valueOf(normalized);
    //         return new FlatType(type.name(), 0); 
    //     } catch (IllegalArgumentException e) {
    //         throw new IllegalArgumentException("No enum constant for flat type: " + name);
    //     }
    // }
}