package com.myorg.bugTrackerPoc.exception;

/**
 * JSON Response body for REST API failures
 *
 * @param code - error code
 * @param message - detailed message of error
 */
public record RestErrorBody (int code, String message){

}
