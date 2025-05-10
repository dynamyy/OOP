package org.example.oop_projekt.Kontrollerid.Kasutaja;

import org.example.oop_projekt.DTO.SisseLogimine;
import org.example.oop_projekt.Erindid.Autentimine.AuthException;
import org.example.oop_projekt.teenuskiht.autentimine.AuthTeenus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping(path = "api/sisse-logimine")
public class SisseLogimineKontroller {
    private final AuthTeenus authTeenus;

    @Autowired
    public SisseLogimineKontroller(AuthTeenus authTeenus) {
        this.authTeenus = authTeenus;
    }

    @PostMapping
    public ResponseEntity<?> kuvaBroneering(@RequestBody SisseLogimine sisselogimisinfo) {

        try {
            String token = authTeenus.logiKasutajaSisse(sisselogimisinfo);
            return ResponseEntity.ok(Map.of("sonum", "Sisselogimine edukas", "token", token));
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("sonum", e.getMessage()));
        }

    }

}
