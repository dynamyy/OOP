package org.example.oop_projekt.Erindid;

public class TokenKehtetuException extends RuntimeException {
    private final String message;

    public TokenKehtetuException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
