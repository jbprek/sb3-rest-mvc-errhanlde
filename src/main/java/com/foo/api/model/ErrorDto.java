package com.foo.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;


//@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorDto (
    Instant time,
    int status,
    String message,
    String path,
    String method,
    List<ValidationError> errors) {


    public record ValidationError (
        String field,
        String message) {}
    }


