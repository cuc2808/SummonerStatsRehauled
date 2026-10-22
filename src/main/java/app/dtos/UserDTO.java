package app.dtos;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String id;
    private String username;
    private String password;
    private Set<String> roles;

    public UserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserDTO(String username, Set<String> roles) {
        this.username = username;
        this.roles = roles;
    }
}
