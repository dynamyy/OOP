package org.example.oop_projekt.Erindid;

public class ScrapeFailedException extends RuntimeException {
    private final String message;

    public ScrapeFailedException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
