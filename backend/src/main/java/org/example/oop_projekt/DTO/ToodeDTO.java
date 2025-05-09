package org.example.oop_projekt.DTO;

public record ToodeDTO(
    Long id,
    String tooteNimi,
    double tooteTukihind, // Fronti võiks saata alati parima hinna
    double tooteUhikuHind,// Fronti võiks saata alati parima hinna
    String uhik,
    String kasonSoodus,
    String pood,
    String toodePiltURL
) { }