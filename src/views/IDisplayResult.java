package views;

public interface IDisplayResult {

    default void displayError(String message) {
        System.out.println("[ERROR] " + message);
    }
    
    default void displaySuccess(String message) {
        System.out.println("[SUCCESS] " + message);
    }
    
    default void displayInfo(String message) {
        System.out.println("[INFO] " + message);
    }
}
