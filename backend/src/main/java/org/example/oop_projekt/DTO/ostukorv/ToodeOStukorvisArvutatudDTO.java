package org.example.oop_projekt.DTO.ostukorv;

public record ToodeOStukorvisArvutatudDTO(
        double tukiHind,
        double hulgaHind,
        String piltURL,
        int kogus
) {
}
