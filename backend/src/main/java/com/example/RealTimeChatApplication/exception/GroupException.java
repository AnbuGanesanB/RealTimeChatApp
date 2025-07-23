package com.example.RealTimeChatApplication.exception;

public class GroupException {

    public static class ShortGroupNameException extends RuntimeException{
        public ShortGroupNameException(String message){ super(message);}
    }

    public static class FileTypeMismatchException extends RuntimeException{
        public FileTypeMismatchException(String message){ super(message);}
    }

    public static class FileOverSizeException extends RuntimeException{
        public FileOverSizeException(String message){ super(message);}
    }

    public static class MinimumMembersException extends RuntimeException{
        public MinimumMembersException(String message){ super(message);}
    }

}
