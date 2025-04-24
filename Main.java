import controllers.AuthenticatorController;

public class Main {
    public static void main(String[] args) {
        AuthenticatorController authenticatorController = new AuthenticatorController();
        while (true){
            authenticatorController.authenticate();
        }
    }
}