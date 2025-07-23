package com.example.RealTimeChatApplication.exception;

public class ContactException {

    public static class ShortContactNameException extends RuntimeException{
        public ShortContactNameException(String message){ super(message);}
    }
}
