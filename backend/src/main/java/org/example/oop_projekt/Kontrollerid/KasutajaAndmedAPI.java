package org.example.oop_projekt.Kontrollerid;

import org.example.oop_projekt.DTO.KasutajaAndmedDTO;
import org.example.oop_projekt.DTO.MärksõnaDTO;
import org.example.oop_projekt.DTO.ToodeDTO;
import org.example.oop_projekt.Erindid.TokenKehtetuException;
import org.example.oop_projekt.teenuskiht.autentimine.AuthTeenus;
import org.example.oop_projekt.teenuskiht.äriloogika.ToodeTeenus;
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
            List<String> andmed = authTeenus.getKasutajaAndmed(kasutajaAndmed);
            return ResponseEntity.ok(Map.of("sonum", andmed));
        } catch (TokenKehtetuException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("sonum", e.getMessage()));
        }
    }
}
