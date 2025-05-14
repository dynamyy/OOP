package org.example.oop_projekt.Kontrollerid;

import org.example.oop_projekt.DTO.*;
import org.example.oop_projekt.Erindid.Autentimine.AuthException;
import org.example.oop_projekt.teenuskiht.ariloogika.ToodeTeenus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping(path = "/api/tooted")
@CrossOrigin(origins = "http://localhost:5173")
public class KuvaTootedAPI {


    private final ToodeTeenus toodeTeenus;

    KuvaTootedAPI (ToodeTeenus toodeTeenus){
        this.toodeTeenus = toodeTeenus;
    }

    // Kasutame toodete kuvamiseks kasutajale
    @PostMapping
    public ResponseEntity<KuvaTootedDTO> kuvaTooted(@RequestBody KuvaTootedParingDTO toodeteParing){
        // Järgmiste kuvatavate toodete leidmine
        KuvaTootedDTO vastus = toodeTeenus.getNToodet(toodeteParing);

        return ResponseEntity.ok().body(vastus);
    }
}
