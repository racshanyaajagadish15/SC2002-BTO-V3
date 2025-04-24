import controllers.AuthenticatorController;

/**
 * Main class to run the application.
 * It initializes the AuthenticatorController and starts the authentication process.
 * The application runs in a loop until the user chooses to exit.
 * The main method is the entry point of the application.
 */

public class Main {
    public static void main(String[] args) {
        AuthenticatorController authenticatorController = new AuthenticatorController();
        while (true){
            authenticatorController.authenticate();
        }
    }
}