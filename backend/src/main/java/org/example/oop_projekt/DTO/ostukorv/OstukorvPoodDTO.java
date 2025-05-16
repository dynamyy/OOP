package org.example.oop_projekt.DTO.ostukorv;

import java.util.List;

public record OstukorvPoodDTO(
        String pood,
        List<ToodeOStukorvisArvutatudDTO> tooted
) {
}
