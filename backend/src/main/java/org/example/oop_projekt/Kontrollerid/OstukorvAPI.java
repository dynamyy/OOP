package org.example.oop_projekt.Kontrollerid;


import org.example.oop_projekt.DTO.ToodeOstukorvisDTO;
import org.example.oop_projekt.teenuskiht.äriloogika.OstukorvTeenus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/ostukorv")
@CrossOrigin(origins = "http://localhost:5137")
public class OstukorvAPI {

    private OstukorvTeenus ostukorvTeenus;

    @Autowired
    public OstukorvAPI(OstukorvTeenus ostukorvTeenus){
        this.ostukorvTeenus = ostukorvTeenus;
    }



    //Selle kaudu saab lõpuks ostukorvi välja arvutada
    //Sisendiks on hetkel märksõnad ja nende tõeväärtus, ilmselt peab seda hiljem muutma Märksõnade DTO-ks
    @PostMapping
    public ResponseEntity<?> getOstukorv(@RequestBody List<ToodeOstukorvisDTO> tootedOstukorvis){//Java teisendab automaatselt jsoni DTO-ks
        try {
            ostukorvTeenus.looOstukorv(tootedOstukorvis);
            return ResponseEntity.ok(Map.of("sonum", "Ostukorv edukalt loodud"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("sonum", e.getMessage()));
        }

    }
}