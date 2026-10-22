package app.entities;

import jakarta.persistence.*;
import lombok.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "users")
public class User implements IUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;
    private String password;

    @ToString.Exclude
    @ManyToMany
    private Set<Role> roles = new HashSet<>();



    public User(String username, String password) {
        this.username = username;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @Override
    public Set<String> getRolesAsStrings() {
        return roles.stream()
                .map(Role::getRole)
                .collect(Collectors.toSet());
    }

    public boolean verifyPassword(String pw) {
        return BCrypt.checkpw(pw, this.password);
    }

    @Override
    public void addRole(Role role) {
        roles.add(role);
    }

    @Override
    public void removeRole(String role) {
        roles.removeIf(r -> r.getRole().equals(role));
    }
}
