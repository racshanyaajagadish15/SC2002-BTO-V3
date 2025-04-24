package enums;

/**
 * This enum represents the various rooms for project flat types
 */

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

}