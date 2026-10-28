package app;

import app.config.ApplicationConfig;
import app.daos.UserDAO;
import app.entities.Role;
import app.entities.User;
import app.routes.SecurityRoutes;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        //APPLICATION SETUP
        ApplicationConfig app = new ApplicationConfig();
        app.appStart();

        UserDAO userDAO = new UserDAO();
        userDAO.createUser("admin28cuc8peter", "gabriellugter123");
        userDAO.createRole("ADMIN");
        userDAO.addUserRole("admin28cuc8peter", "ADMIN");
    }
}
