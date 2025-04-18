package org.example.oop_projekt.DTO;

public class SisseLogimine {

    private String email;
    private String parool;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getParool() {
        return parool;
    }

    public void setParool(String parool) {
        this.parool = parool;
    }

    @Override
    public String toString() {
        return "SisseLogimine{" +
                "email='" + email + '\'' +
                ", parool='" + parool + '\'' +
                '}';
    }
}
