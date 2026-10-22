package app.entities;

import java.util.Set;

public interface IUser {
    Set<String> getRolesAsStrings();
    boolean verifyPassword(String pw);
    void addRole(Role role);
    void removeRole(String role);
}
