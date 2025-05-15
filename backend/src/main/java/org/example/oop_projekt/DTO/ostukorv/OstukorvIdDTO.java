package org.example.oop_projekt.DTO.ostukorv;

import org.example.oop_projekt.DTO.autentimine.TokenDTO;

public record OstukorvIdDTO(
        String token,
        Long id
) implements TokenDTO {
}
