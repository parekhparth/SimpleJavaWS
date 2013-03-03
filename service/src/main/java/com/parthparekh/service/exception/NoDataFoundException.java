package com.parthparekh.service.exception;

/**
 * Exception for no data found
 *
 * @author: Parth Parekh (parthparekh [at] gatech [dot] edu)
 **/
public class NoDataFoundException extends RuntimeException {

    public NoDataFoundException() {
        super("no data found");
    }

    public NoDataFoundException(String message) {
        super(message);
    }
}