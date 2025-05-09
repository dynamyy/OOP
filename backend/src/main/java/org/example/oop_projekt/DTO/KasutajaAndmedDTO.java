package org.example.oop_projekt.DTO;

import java.util.List;

public record KasutajaAndmedDTO (
        String token,
        String tegevus,
        String andmetuup,
        String uusSoneTuup,
        List<String> uusListTuup
) implements TokenDTO {
}