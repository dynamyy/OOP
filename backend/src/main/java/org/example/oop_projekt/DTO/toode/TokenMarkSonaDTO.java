package org.example.oop_projekt.DTO.toode;

import org.example.oop_projekt.DTO.autentimine.TokenDTO;

import java.util.List;

public record TokenMarkSonaDTO(
        List<MarksonaDTO> marksonad,
        String token
) implements TokenDTO {}
