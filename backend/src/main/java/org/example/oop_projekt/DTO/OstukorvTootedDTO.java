package org.example.oop_projekt.DTO;

import java.util.List;

public record OstukorvTootedDTO(
        String nimi,
        List<OstukorvPoodDTO> poed
) {
}
