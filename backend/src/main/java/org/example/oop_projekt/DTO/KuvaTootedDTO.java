package org.example.oop_projekt.DTO;

import java.util.List;

public record KuvaTootedDTO (
        List<ToodeDTO> tooted,
        long tooteidKokku
) {
}
