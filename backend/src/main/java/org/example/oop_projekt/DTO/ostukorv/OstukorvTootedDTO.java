package org.example.oop_projekt.DTO.ostukorv;

import java.util.List;

public record OstukorvTootedDTO(
        String nimi,
        List<OstukorvPoodDTO> poed
) {
}
