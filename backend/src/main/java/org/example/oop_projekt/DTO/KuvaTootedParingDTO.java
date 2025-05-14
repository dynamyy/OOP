package org.example.oop_projekt.DTO;

import java.util.List;

public record KuvaTootedParingDTO (
        List<MarksonaDTO> marksonad,
        int nihe,
        String token
) implements TokenDTO {
}
