package urfumagistracy.backend;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignupRequest {

    private String username;
    private String email;
    public String password;
}
