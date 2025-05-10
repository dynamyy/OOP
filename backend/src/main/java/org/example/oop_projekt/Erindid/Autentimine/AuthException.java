package org.example.oop_projekt.Erindid.Autentimine;

public class AuthException extends RuntimeException {
    private final String message;
    public AuthException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
