package org.bohdan.answers.api.exceptions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserDoesNotSignInException extends RuntimeException {

    List<String> errors;

    public UserDoesNotSignInException(ArrayList<String> errors, String message) {
        super(message);
        this.errors = errors;
    }
}
