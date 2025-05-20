package org.example.oop_projekt.Kontrollerid;


import org.example.oop_projekt.DTO.autentimine.Token;
import org.example.oop_projekt.DTO.ostukorv.ToodeOstukorvisIdDTO;
import org.example.oop_projekt.teenuskiht.ariloogika.OstukorvTeenus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/ostukorv/uuenda-toodet")
@CrossOrigin(origins = "http://localhost:5173")
public class UuendaOstukorviToodet {

    private final OstukorvTeenus ostukorvTeenus;

    @Autowired
    public UuendaOstukorviToodet(OstukorvTeenus ostukorvTeenus){
        this.ostukorvTeenus = ostukorvTeenus;
    }

    // Uuendab mingit kindlat toodet mingis ostukorvis
    @PostMapping
    public ResponseEntity<?> uuendaToode(@RequestBody ToodeOstukorvisIdDTO toodeOstukorvisIdDTO){//Java teisendab automaatselt jsoni DTO-ks
        try {
            System.out.println(toodeOstukorvisIdDTO);
            ostukorvTeenus.jargmineToode(
                    toodeOstukorvisIdDTO.id(),
                    toodeOstukorvisIdDTO.pood(),
                    new Token(toodeOstukorvisIdDTO.token())
            );
            return ResponseEntity.ok(Map.of("sonum", "Valitud järgmine toode"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("sonum", e.getMessage()));
        }
    }
}