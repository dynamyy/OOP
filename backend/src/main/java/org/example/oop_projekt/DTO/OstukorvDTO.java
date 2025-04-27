package org.example.oop_projekt.DTO;

import org.example.oop_projekt.andmepääsukiht.Toode;
import org.example.oop_projekt.andmepääsukiht.ToodeOstukorvis;

import java.util.List;

public record OstukorvDTO(
        //Siia tuleks lisada välju vastavalt sellele, mida front meile saadab
        List<String> tooted,
        List<String> märksõnad
) {
}
