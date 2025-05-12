package org.example.oop_projekt.DTO;

public record HinnaMuutusDTO (
        ToodeDTO toodeDTO,
        String token
) implements TokenDTO {
}
