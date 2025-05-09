package org.example.oop_projekt.DTO;

import java.util.List;

public record ToodeOstukorvisDTO(
        List<MarksonaDTO> marksonad,
        String tooteKogus,
        List<EbasobivToodeDTO> ebasobivadTooted
) { }