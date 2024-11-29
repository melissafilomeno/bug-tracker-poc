package com.myorg.bugTrackerPoc.exception;

import com.myorg.bugTrackerPoc.openapi.server.model.Error;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Custom ExceptionHandler for modifying JSON response body
 * @see com.myorg.bugTrackerPoc.openapi.server.model.Error
 */
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> createResponseEntity(@Nullable Object body, HttpHeaders headers,
                                                          HttpStatusCode statusCode, WebRequest request) {
        ProblemDetail problemDetail = (ProblemDetail)body;
        Error error = new Error();
        if(problemDetail != null) {
            error.setCode(String.valueOf(problemDetail.getStatus()));
            error.setMessage(problemDetail.getTitle());
        }else{
            error.setCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
            MessageSource messageSource = getMessageSource();
            if(messageSource != null) {
                error.setMessage(messageSource.getMessage(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                        null, request.getLocale()));
            }
        }
        return new ResponseEntity<>(error, headers, statusCode);
    }
}
