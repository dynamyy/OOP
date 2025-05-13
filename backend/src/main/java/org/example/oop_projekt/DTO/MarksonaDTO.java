package org.example.oop_projekt.DTO;


public record MarksonaDTO(
        String marksona,
        String valikuVarv,
        String token
) implements TokenDTO{}
