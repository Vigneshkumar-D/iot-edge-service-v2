package com.iot_edge.common.exceptions;

public class RequestTooLargeException extends Exception {
    public RequestTooLargeException(String message) {
        super(message);
    }
}