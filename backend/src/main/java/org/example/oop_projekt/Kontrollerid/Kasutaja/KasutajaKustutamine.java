package org.example.oop_projekt.Kontrollerid.Kasutaja;

import org.example.oop_projekt.DTO.KasutajaKustutamineDTO;
import org.example.oop_projekt.Erindid.Autentimine.AuthException;
import org.example.oop_projekt.teenuskiht.autentimine.AuthTeenus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/kustutaKasutaja")
@CrossOrigin(origins = "http://localhost:5173")
public class KasutajaKustutamine {
    private final AuthTeenus authTeenus;

    @Autowired
    public KasutajaKustutamine(AuthTeenus authTeenus) {
        this.authTeenus = authTeenus;
    }

    @PostMapping
    public ResponseEntity<?> kustuta(@RequestBody KasutajaKustutamineDTO kustutamisandmed) {
        try {
            authTeenus.kustutaKasutaja(kustutamisandmed);
            return ResponseEntity.ok(Map.of("sonum", "Kasutaja kustutamine edukas"));
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("sonum", e.getMessage()));
        }
    }
}
