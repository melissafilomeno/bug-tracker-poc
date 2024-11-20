package com.myorg.bugTrackerPoc.exception;

import org.openapitools.client.model.Error;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Custom ExceptionHandler for modifying JSON response body
 * @see org.openapitools.client.model.Error
 */
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> createResponseEntity(@Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        ProblemDetail problemDetail = (ProblemDetail)body;
        Error error = new Error();
        error.setCode(String.valueOf(problemDetail.getStatus()));
        error.setMessage(problemDetail.getTitle());
        return new ResponseEntity(error, headers, statusCode);
    }
}
