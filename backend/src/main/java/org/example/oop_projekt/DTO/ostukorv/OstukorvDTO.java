package org.example.oop_projekt.DTO.ostukorv;

import org.example.oop_projekt.DTO.autentimine.TokenDTO;

import java.util.List;

public record OstukorvDTO(
        String nimi,
        List<ToodeOstukorvisDTO> tooted,
        String token
) implements TokenDTO { }