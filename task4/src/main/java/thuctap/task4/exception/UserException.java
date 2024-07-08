package thuctap.task4.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
public class UserException extends Exception {
    boolean status;
    String message;
    int code;
    Object data;

    public UserException(String message, boolean status, int code) {

        this.status = status;
        this.message = message;
        this.code = code;
    }


}
