package org.bohdan.answers.api.exceptions;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.log4j.Log4j2;
import org.bohdan.answers.api.domain.Views;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@Log4j2
@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exception(Exception ex, WebRequest request) throws Exception {

        log.error("Exception during execution of application", ex);

        return handleException(ex, request);
    }

    @ExceptionHandler(UserNotRegisteredException.class)
    @JsonView(Views.FullErrorFields.class)
    public ResponseEntity<ErrorDto> handleUserNotRegisteredException(UserNotRegisteredException ex) {

        List<String> errors = ex.getErrors();
        
        return getErrorDtoResponseEntity(ex, errors);
    }

    @ExceptionHandler(UserDoesNotSignInException.class)
    @JsonView(Views.FullErrorFields.class)
    public ResponseEntity<ErrorDto> handleUserDoesNotSignInException(UserDoesNotSignInException ex) {

        List<String> errors = ex.getErrors();

        return getErrorDtoResponseEntity(ex, errors);
    }

    private ResponseEntity<ErrorDto> getErrorDtoResponseEntity(Exception ex, List<String> errors) {

        log.error("Exception during execution of application", ex);

        int status = getStatus(ex);

        return ResponseEntity
                .status(status)
                .body(ErrorDto
                        .builder()
                        .error(ex.getMessage())
                        .fieldErrors(errors)
                        .timestamp(System.currentTimeMillis())
                        .build());
    }

    private int getStatus(Exception ex) {
        ResponseStatus annotation = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        return (annotation != null) ? annotation.value().value() : HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
}