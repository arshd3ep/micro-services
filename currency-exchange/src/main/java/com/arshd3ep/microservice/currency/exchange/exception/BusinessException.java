package com.arshd3ep.microservice.currency.exchange.exception;

public class BusinessException extends RuntimeException{
    public BusinessException(String message){
        super(message);
    }
    public BusinessException(String message, Exception e){
        super(message, e);
    }
}
