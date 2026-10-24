package app.dtos;

import app.dtos.accountdtos.AccountOverviewDTO;
import app.dtos.accountdtos.AccountPreviewDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
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
    private List<AccountPreviewDTO> trackedAccounts = new ArrayList<>();
    private AccountPreviewDTO mainAccountPuuid = null;

    public UserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserDTO(String username, Set<String> roles) {
        this.username = username;
        this.roles = roles;
    }
}
