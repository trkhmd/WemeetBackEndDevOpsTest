package cal.api.wemeet.models.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class UserLoginEntry {
    @NotEmpty(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    @NotEmpty(message = "Password is required")
    private String password;
    
    public UserLoginEntry() {
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    
}
