package org.example.oop_projekt.Kontrollerid.Kasutaja;

import org.example.oop_projekt.DTO.autentimine.Registreerimine;
import org.example.oop_projekt.Erindid.RegistreerimineFailedException;
import org.example.oop_projekt.teenuskiht.autentimine.AuthTeenus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "api/registreeri")
public class RegistreerimineKontroller {
    private final AuthTeenus authTeenus;

    @Autowired
    public RegistreerimineKontroller(AuthTeenus authTeenus) {
        this.authTeenus = authTeenus;
    }

    @PostMapping
    public ResponseEntity<?> registreeri(@RequestBody Registreerimine sisselogimisinfo) {
        try {
            authTeenus.registreeriKasutaja(sisselogimisinfo);
            return ResponseEntity.ok(Map.of("sonum", "Registreerimine edukas"));
        } catch (RegistreerimineFailedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("sonum", e.getMessage()));
        }
    }
}
