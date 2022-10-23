package cal.api.wemeet.models.dto.response;

import org.springframework.http.HttpStatus;

public class SimpleResponse {

    private int status;
    private String message;

    public SimpleResponse() {
    }

    public SimpleResponse(HttpStatus status,String message) {
        this.status = status.value();
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    
}
