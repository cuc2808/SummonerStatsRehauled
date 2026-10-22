package app.daos;

import app.entities.Role;
import app.entities.User;
import io.javalin.validation.ValidationException;

public interface IUserDAO {
    User getVerifiedUser(String username, String password) throws ValidationException; // used for login
    User createUser(String username, String password); // used for register
    Role createRole(String role);
    User addUserRole(String username, String role);
}