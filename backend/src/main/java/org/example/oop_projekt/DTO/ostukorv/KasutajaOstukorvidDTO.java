package org.example.oop_projekt.DTO.ostukorv;

import java.util.List;

public record KasutajaOstukorvidDTO(
        List<OstukorvNimiIdDTO> ostukorvid
) {
}
