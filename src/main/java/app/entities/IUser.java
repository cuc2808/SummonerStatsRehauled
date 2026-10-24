package app.entities;

import java.util.List;
import java.util.Set;

public interface IUser {
    Set<String> getRolesAsStrings();
    boolean verifyPassword(String pw);
    void addRole(Role role);
    void removeRole(String role);
    List<String> trackAccount(String puuid);
    List<String> stopTrackingAccount(String puuid);
    String setMainAccount(String puuid);
    String removeMainAccount(String puuid);
}
