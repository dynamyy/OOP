package org.example.oop_projekt.Erindid;

public class LoginFailException extends RuntimeException{
    private final String message;


    public LoginFailException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
