package org.example.oop_projekt.DTO.ostukorv;

import org.example.oop_projekt.DTO.autentimine.TokenDTO;

public record ToodeOstukorvisIdDTO(
        long id,
        String pood,
        String token
) implements TokenDTO {
}
