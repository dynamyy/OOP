package org.example.oop_projekt.DTO;

import java.util.List;

public record OstukorvDTO(
        String nimi,
        List<ToodeOstukorvisDTO> tooted
) { }