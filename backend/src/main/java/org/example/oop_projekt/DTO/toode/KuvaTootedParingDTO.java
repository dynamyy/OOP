package org.example.oop_projekt.DTO.toode;

import org.example.oop_projekt.DTO.autentimine.TokenVerify;

import java.util.List;

public record KuvaTootedParingDTO (
        List<MarksonaDTO> marksonad,
        int nihe,
        TokenVerify token
) {
}
