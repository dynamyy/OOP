package org.example.oop_projekt.DTO;

import lombok.Getter;

@Getter
public class SisseLogimine {

    private String email;
    private String parool;

    @Override
    public String toString() {
        return "SisseLogimine{" +
                "email='" + email + '\'' +
                ", parool='" + parool + '\'' +
                '}';
    }
}
