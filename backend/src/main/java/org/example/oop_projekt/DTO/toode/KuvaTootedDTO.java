package org.example.oop_projekt.DTO.toode;

import java.util.List;

public record KuvaTootedDTO (
        List<ToodeDTO> tooted,
        long tooteidKokku
) {
}
