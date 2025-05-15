package org.example.oop_projekt.DTO.ostukorv;

import org.example.oop_projekt.DTO.toode.MarksonaDTO;
import org.example.oop_projekt.DTO.toode.EbasobivToodeDTO;

import java.util.List;

public record ToodeOstukorvisDTO(
        List<MarksonaDTO> marksonad,
        String tooteKogus,
        List<EbasobivToodeDTO> ebasobivadTooted
) { }