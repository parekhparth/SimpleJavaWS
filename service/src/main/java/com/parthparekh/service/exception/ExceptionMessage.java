package com.parthparekh.service.exception;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

/**
 * Exception message object
 *
 * @author: Parth Parekh (parthparekh [at] gatech [dot] edu)
 **/
@JsonPropertyOrder(value = {"errorMessage"})
public class ExceptionMessage {
    private String errorMessage = "something went wrong";

       public ExceptionMessage(String errorMessage) {
           this.errorMessage = errorMessage;
       }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
