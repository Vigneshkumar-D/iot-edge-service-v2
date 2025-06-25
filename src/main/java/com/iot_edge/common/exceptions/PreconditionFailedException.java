package com.iot_edge.common.exceptions;

public class PreconditionFailedException extends Exception {
    public PreconditionFailedException(String message) {
        super(message);
    }
}