package org.example.oop_projekt.DTO;

public record ToodeDTO(
    Long id,
    String tooteNimi,
    double tooteTükihind, // Fronti võiks saata alati parima hinna
    double tooteÜhikuHind,// Fronti võiks saata alati parima hinna
    String ühik,
    String kasonSoodus,
    String pood,
    String toodePiltURL
) { }