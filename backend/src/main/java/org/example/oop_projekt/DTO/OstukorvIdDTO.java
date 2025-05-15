package org.example.oop_projekt.DTO;

public record OstukorvIdDTO(
        String token,
        Long id
) implements TokenDTO {
}
