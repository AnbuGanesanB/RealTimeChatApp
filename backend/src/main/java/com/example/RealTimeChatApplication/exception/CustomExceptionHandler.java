package com.example.RealTimeChatApplication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice("com.example.RealTimeChatApplication.controller")
@ControllerAdvice()
public class CustomExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String,String> handleValidationException(MethodArgumentNotValidException exception){
        Map<String,String> errors = new HashMap<>();
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        for(FieldError objError: fieldErrors){
            errors.putIfAbsent(objError.getField(),objError.getDefaultMessage());
        }
        return errors;
    }

    @ExceptionHandler({
            UserException.EmailAlreadyExistsException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictExceptions(RuntimeException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler({
            UserException.ShortUserNameException.class,
            UserException.FileTypeMismatchException.class,
            UserException.FileOverSizeException.class,
            ContactException.ShortContactNameException.class,
            GroupException.FileOverSizeException.class,
            GroupException.FileTypeMismatchException.class,
            GroupException.ShortGroupNameException.class,
            GroupException.MinimumMembersException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserValidationExceptions(RuntimeException ex) {
        return new ErrorResponse(ex.getMessage());
    }


}
