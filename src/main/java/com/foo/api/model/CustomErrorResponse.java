package com.foo.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
//@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CustomErrorResponse {
    private final Instant time;
    private final int status;
    private final String message;
    private final String path;
    private final String method;
    private final List<ValidationError> errors;

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class ValidationError {
        private final String field;
        private final String message;
    }
//
//    public void addValidationError(String field, String message){
//        if(Objects.isNull(errors)){
//            errors = new ArrayList<>();
//        }
//        errors.add(new ValidationError(field, message));
//    }
}
