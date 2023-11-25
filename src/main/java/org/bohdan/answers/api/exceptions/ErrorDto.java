package org.bohdan.answers.api.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bohdan.answers.api.domain.Views;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorDto {

    @JsonView(Views.IdName.class)
    String error;

    @JsonProperty("error_description")
    @JsonView(Views.FullErrorDescription.class)
    String errorDescription;

    @JsonProperty("field_errors")
    @JsonView(Views.FullErrorFields.class)
    List<String> fieldErrors;

    @JsonView(Views.IdName.class)
    private long timestamp;
}
