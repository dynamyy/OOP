package org.example.oop_projekt.Kontrollerid;

import org.example.oop_projekt.DTO.HinnaMuutusDTO;
import org.example.oop_projekt.Erindid.AndmeteUuendusException;
import org.example.oop_projekt.Erindid.Autentimine.AuthException;
import org.example.oop_projekt.teenuskiht.ariloogika.ToodeTeenus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/toode/muuda")
@CrossOrigin(origins = "http://localhost:5173")
public class MuudaTooteHindAPI {


    private final ToodeTeenus toodeTeenus;

    MuudaTooteHindAPI (ToodeTeenus toodeTeenus){
        this.toodeTeenus = toodeTeenus;
    }

    @PostMapping
    public ResponseEntity<?> kuvaTooted(@RequestBody HinnaMuutusDTO hinnaMuutusDTO){
        try {
            toodeTeenus.muudaTooteHind(hinnaMuutusDTO);
            return ResponseEntity.ok(Map.of("sonum", hinnaMuutusDTO.toodeDTO().tooteNimi() + " andmed muudetud!"));
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("sonum",
                    ("Ei saanud hinda muuta. Pole sisse logitud: " + e.getMessage())));
        }
    }
}
