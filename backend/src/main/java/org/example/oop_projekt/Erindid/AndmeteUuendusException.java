package org.example.oop_projekt.Erindid;

public class AndmeteUuendusException extends RuntimeException {
    private final String message;

    public AndmeteUuendusException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
      return message;
    }
}
