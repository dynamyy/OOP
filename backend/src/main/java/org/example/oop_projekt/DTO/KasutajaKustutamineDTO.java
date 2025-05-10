package org.example.oop_projekt.DTO;

public record KasutajaKustutamineDTO(
        String token,
        String parool
) implements TokenDTO, ParoolDTO {
}
