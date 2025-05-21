package org.example.oop_projekt.DTO.ostukorv;

public record ToodeOStukorvisArvutatudDTO(
        String nimetus,
        double tukiHind,
        double hulgaHind,
        String piltURL,
        int kogus,
        long id
) {
}
