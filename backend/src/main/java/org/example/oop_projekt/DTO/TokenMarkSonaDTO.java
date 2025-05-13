package org.example.oop_projekt.DTO;

import java.util.List;

public record TokenMarkSonaDTO(
        List<MarksonaDTO> marksonad,
        String token
) implements TokenDTO {}
