package views;

/**
 * IDisplayResult class is implemented by view classes to display messages
 */
public interface IDisplayResult {
    /**
     * Displays a message indicating that the operation was erroneous.
     * @param message The message to display.
     */
    default void displayError(String message) {
        System.out.println("[ERROR] " + message);
    }
    /**
     * Displays a message indicating that the operation was successful.
     * @param message The message to display.
     */
    default void displaySuccess(String message) {
        System.out.println("[SUCCESS] " + message);
    }
    /**
     * Displays a message indicating some information on the operation.
     * @param message The message to display.
     */
    default void displayInfo(String message) {
        System.out.println("[INFO] " + message);
    }
}
