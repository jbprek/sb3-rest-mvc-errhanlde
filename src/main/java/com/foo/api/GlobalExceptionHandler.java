package com.foo.api;

import com.foo.api.model.CustomErrorResponse;
import com.foo.service.OfficeDaoNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e, WebRequest request) {
        log.warn("ConstraintViolationException:", e);

        Function<ConstraintViolation, CustomErrorResponse.ValidationError> constraintViolationMapper = v -> {
            var arr = v.getPropertyPath().toString().split("\\.");
            var node = arr.length == 0 ? "": arr[arr.length - 1];
            return new CustomErrorResponse.ValidationError(node, v.getMessage());
        };
        var validationErrors = e.getConstraintViolations().stream()
                .map(constraintViolationMapper)
                .collect(Collectors.toList());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid URL parameters", request, validationErrors);
    }

    @Override
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {

        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(v -> new CustomErrorResponse.ValidationError(v.getField(), v.getDefaultMessage()))
                .collect(Collectors.toList());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid payload", request, fieldErrors);
    }

    @ExceptionHandler(OfficeDaoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNoSuchElementFoundException(OfficeDaoNotFoundException itemNotFoundException, WebRequest request) {
        log.error("Failed to find the requested element", itemNotFoundException);
        return buildErrorResponse(itemNotFoundException, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtException(Exception ex, WebRequest request) {
        log.error("Unknown error occurred", ex);
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    public ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        return buildErrorResponse(ex, status, request);
    }

    private ResponseEntity<Object> buildErrorResponse(
            HttpStatusCode httpStatus, String message,
            WebRequest request,
            List<CustomErrorResponse.ValidationError> errors) {
        var customErrorResponse =
                new CustomErrorResponse(Instant.now(), httpStatus.value(), message, getPath(request),
                        getMethod(request), errors);

        return ResponseEntity.status(httpStatus).body(customErrorResponse);
    }

    private ResponseEntity<Object> buildErrorResponse(Exception exception,
                                                      HttpStatusCode httpStatus,
                                                      WebRequest request) {
        return buildErrorResponse(httpStatus, exception.getMessage(), request, Collections.emptyList());
    }

    private static String getPath(WebRequest request) {
        var url = ((ServletWebRequest) request).getRequest().getRequestURI();
        return url.substring(url.indexOf('/'));
    }

    private static String getMethod(WebRequest request) {
        return ((ServletWebRequest) request).getHttpMethod().name();
    }
}
