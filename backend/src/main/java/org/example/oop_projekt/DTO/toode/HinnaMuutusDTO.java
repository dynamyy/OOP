package org.example.oop_projekt.DTO.toode;

import org.example.oop_projekt.DTO.autentimine.TokenDTO;

public record HinnaMuutusDTO (
        ToodeDTO toodeDTO,
        String token
) implements TokenDTO {
}
