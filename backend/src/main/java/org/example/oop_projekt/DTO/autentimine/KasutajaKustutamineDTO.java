package org.example.oop_projekt.DTO.autentimine;

public record KasutajaKustutamineDTO(
        String token,
        String parool
) implements TokenDTO, ParoolDTO {
}
