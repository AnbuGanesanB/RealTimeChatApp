package com.example.RealTimeChatApplication.exception;

public class UserException {

    public static class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class PasswordNotCorrectException extends RuntimeException{
        public PasswordNotCorrectException(String message){
            super(message);
        }
    }

    public static class ShortUserNameException extends RuntimeException{
        public ShortUserNameException(String message){ super(message);}
    }

    public static class FileTypeMismatchException extends RuntimeException{
        public FileTypeMismatchException(String message){ super(message);}
    }

    public static class FileOverSizeException extends RuntimeException{
        public FileOverSizeException(String message){ super(message);}
    }
}
