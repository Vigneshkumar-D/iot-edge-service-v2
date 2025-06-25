package com.iot_edge.common.exceptions;

public class RequestTimeoutException extends Exception {
    public RequestTimeoutException(String message) {
        super(message);
    }
}
