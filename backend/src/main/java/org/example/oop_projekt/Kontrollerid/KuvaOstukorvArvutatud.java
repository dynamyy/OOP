package org.example.oop_projekt.Kontrollerid;

import org.example.oop_projekt.DTO.autentimine.Token;
import org.example.oop_projekt.mudel.Ostukorv;
import org.example.oop_projekt.repository.OstukorvRepository;
import org.example.oop_projekt.teenuskiht.ariloogika.OstukorvTeenus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequestMapping(path = "/api/ostukorv/{id}")
@CrossOrigin(origins = "http://localhost:5173")
public class KuvaOstukorvArvutatud {

    private final OstukorvTeenus ostukorvTeenus;
    private final OstukorvRepository ostukorvRepository;

    public KuvaOstukorvArvutatud(OstukorvTeenus ostukorvTeenus, OstukorvRepository ostukorvRepository) {
        this.ostukorvTeenus = ostukorvTeenus;
        this.ostukorvRepository = ostukorvRepository;
    }

    // Kasutame toodete kuvamiseks kasutajale
    @PostMapping
    public ResponseEntity<Object> getToode(@PathVariable("id") Long id, @RequestBody Token token){
        Ostukorv ostukorv = ostukorvRepository.findOstukorvById(id);

        if (ostukorv != null) {
            return ResponseEntity.ok().body(
                    Map.of("sonum", "Ostukorv leitud", "ostukorvAndmed", ostukorvTeenus.looOstukorviDTO(ostukorv, token)));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("sonum", ("Ei leidnud toodet id-ga " + id)));
    }

}
