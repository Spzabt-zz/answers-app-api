package org.bohdan.answers.api.utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;

public class ControllerUtil {
    public static ArrayList<String> bindErrors(BindingResult bindingResult) {
        ArrayList<String> errors = new ArrayList<>();
        bindingResult.getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(fieldName + ": " + errorMessage);
        });

        return errors;
    }
}
