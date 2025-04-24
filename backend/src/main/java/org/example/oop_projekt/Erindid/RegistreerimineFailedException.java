package org.example.oop_projekt.Erindid;

public class RegistreerimineFailedException extends RuntimeException{
    private final String message;


    public RegistreerimineFailedException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
