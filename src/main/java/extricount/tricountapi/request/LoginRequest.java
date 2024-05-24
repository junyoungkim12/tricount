package extricount.tricountapi.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String loginId;
    private String password;
}
