package controllers;

import models.HDBManager;
import views.ManagerMainView;

public class ManagerMainController {

    public void managerSelectMenu(HDBManager manager) {
        System.out.println("Logged in as Manager: " + manager.getName());
        ManagerMainView view = new ManagerMainView(manager); // Pass the manager to the view
        view.showManagerMenu();
    }
}
