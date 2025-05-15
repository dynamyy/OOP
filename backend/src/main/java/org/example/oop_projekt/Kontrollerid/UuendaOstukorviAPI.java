package org.example.oop_projekt.Kontrollerid;

import org.example.oop_projekt.DTO.OstukorvIdDTO;
import org.example.oop_projekt.DTO.Token;
import org.example.oop_projekt.repository.OstukorvRepository;
import org.example.oop_projekt.teenuskiht.ariloogika.OstukorvTeenus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/ostukorv/uuenda")
@CrossOrigin(origins = "http://localhost:5173")
public class UuendaOstukorviAPI {

    private final OstukorvTeenus ostukorvTeenus;
    private final OstukorvRepository ostukorvRepository;

    @Autowired
    public UuendaOstukorviAPI(OstukorvTeenus ostukorvTeenus, OstukorvRepository ostukorvRepository) {
        this.ostukorvTeenus = ostukorvTeenus;
        this.ostukorvRepository = ostukorvRepository;
    }

    @PostMapping
    public ResponseEntity<?> uuendaOstukorvi(@RequestBody OstukorvIdDTO ostukorvIdDTO){//Java teisendab automaatselt jsoni DTO-ks
        try {
            ostukorvTeenus.uuendaHindu(ostukorvRepository.findOstukorvById(ostukorvIdDTO.id()), new Token(ostukorvIdDTO.token()));
            return ResponseEntity.ok(Map.of("sonum", "Ostukorv uuendatud"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("sonum", e.getMessage()));
        }
    }
}
