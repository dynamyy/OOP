package org.example.oop_projekt.Kontrollerid.Kasutaja;

import org.example.oop_projekt.DTO.autentimine.KasutajaAndmedDTO;
import org.example.oop_projekt.Erindid.AndmeteUuendusException;
import org.example.oop_projekt.Erindid.Autentimine.AuthException;
import org.example.oop_projekt.teenuskiht.autentimine.AuthTeenus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/kasutaja")
@CrossOrigin(origins = "http://localhost:5173")
public class KasutajaAndmedAPI {


    private final AuthTeenus authTeenus;

    KasutajaAndmedAPI (AuthTeenus authTeenus){
        this.authTeenus = authTeenus;
    }

    // Kasutame toodete kuvamiseks kasutajale
    @PostMapping
    public ResponseEntity<?> kuvaTooted(@RequestBody KasutajaAndmedDTO kasutajaAndmed){
        try {
            // Andmete get requesti handlimine
            if (kasutajaAndmed.tegevus().equals("get")) {
                List<String> andmed = authTeenus.getKasutajaAndmed(kasutajaAndmed);
                return ResponseEntity.ok(Map.of("sonum", andmed));
            }

            // Andmete post requesti handlimine
            authTeenus.setKasutajaAndmed(kasutajaAndmed);
            return ResponseEntity.ok(Map.of("sonum", "andmed uuendatud"));

        } catch (AuthException | AndmeteUuendusException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("sonum", e.getMessage()));
        }
    }
}
