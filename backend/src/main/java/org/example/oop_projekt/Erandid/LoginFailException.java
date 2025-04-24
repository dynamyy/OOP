package org.example.oop_projekt.Erandid;

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
