package org.example.oop_projekt.DTO.toode;

import org.example.oop_projekt.mudel.Toode;

import java.time.LocalDateTime;

public record ToodeDTO(
    Long id,
    String tooteNimi,
    double tooteTukihind, // Fronti võiks saata alati parima hinna
    double tooteUhikuHind,// Fronti võiks saata alati parima hinna
    String uhik,
    String kasonSoodus,
    String pood,
    String toodePiltURL,
    LocalDateTime viimatiUuendatud
) {
    public ToodeDTO(Toode toode) {
        this(
                toode.getId(),
                toode.getNimetus(),
                toode.getHindKliendi(),
                toode.getHulgaHindKliendi(),
                toode.getYhik(),
                toode.getHulgaHindKliendi() < toode.getHindKliendi() ? "true" : "false",
                toode.getPood().getNimi(),
                toode.getTootePiltURL(),
                toode.getViimatiUuendatud()
        );
    }
}