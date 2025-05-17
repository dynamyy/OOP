package org.example.oop_projekt.Kontrollerid;


import org.example.oop_projekt.DTO.ostukorv.OstukorvDTO;
import org.example.oop_projekt.teenuskiht.ariloogika.OstukorvTeenus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping(path = "/api/ostukorv")
public class OstukorvAPI {

    private final OstukorvTeenus ostukorvTeenus;

    @Autowired
    public OstukorvAPI(OstukorvTeenus ostukorvTeenus){
        this.ostukorvTeenus = ostukorvTeenus;
    }

    //Selle kaudu saab lõpuks ostukorvi välja arvutada
    //Sisendiks on hetkel märksõnad ja nende tõeväärtus, ilmselt peab seda hiljem muutma Märksõnade DTO-ks
    @PostMapping
    public ResponseEntity<?> getOstukorv(@RequestBody OstukorvDTO ostukorv){//Java teisendab automaatselt jsoni DTO-ks
        try {
            ostukorvTeenus.looOstukorv(ostukorv);
            return ResponseEntity.ok(Map.of("sonum", "Ostukorv edukalt loodud"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("sonum", e.getMessage()));
        }
    }
}