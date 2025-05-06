package org.example.oop_projekt.Kontrollerid;

import org.example.oop_projekt.DTO.HinnaMuutusDTO;
import org.example.oop_projekt.DTO.MärksõnaDTO;
import org.example.oop_projekt.DTO.ToodeDTO;
import org.example.oop_projekt.mudel.Toode;
import org.example.oop_projekt.teenuskiht.äriloogika.ToodeTeenus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/muudatootehind")
@CrossOrigin(origins = "http://localhost:5173")
public class MuudaTooteHindAPI {


    private ToodeTeenus toodeTeenus;

    MuudaTooteHindAPI (ToodeTeenus toodeTeenus){
        this.toodeTeenus = toodeTeenus;
    }

    /*
    // Kasutame toote hinna vahetamiseks frontendi kaudu
    @PostMapping
    public ResponseEntity<String> kuvaTooted(@RequestBody HinnaMuutusDTO hinnaMuutusDTO){
        toodeTeenus.muudaTooteHind(hinnaMuutusDTO);
        return ResponseEntity.ok("Toote hind muudetud!");//Lisa siia ka toote nimi ja hind, mis sisestati andmebaasi
    }

     */
}
