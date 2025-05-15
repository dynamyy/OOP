package org.example.oop_projekt.Kontrollerid;

import org.example.oop_projekt.DTO.autentimine.TokenVerify;
import org.example.oop_projekt.DTO.toode.ToodeDTO;
import org.example.oop_projekt.mudel.Toode;
import org.example.oop_projekt.repository.ToodeRepository;
import org.example.oop_projekt.teenuskiht.ariloogika.ToodeTeenus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping(path = "/api/toode/{id}")
@CrossOrigin(origins = "http://localhost:5173")
public class KuvaUksiktoode {

    private final ToodeRepository toodeRepository;
    private final ToodeTeenus toodeTeenus;

    KuvaUksiktoode (ToodeRepository toodeRepository, ToodeTeenus toodeTeenus){
        this.toodeRepository = toodeRepository;
        this.toodeTeenus = toodeTeenus;
    }

    // Kasutame toodete kuvamiseks kasutajale
    @PostMapping
    public ResponseEntity<Object> getToode(@PathVariable("id") Long id, @RequestBody TokenVerify tokenVerify){
        Toode toode = toodeTeenus.leiaÜksikToode(id, tokenVerify);


        if (toode != null) {
            return ResponseEntity.ok().body(
                    Map.of("sonum", "Toode leitud", "tooteAndmed", new ToodeDTO(toode)));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("sonum", ("Ei leidnud toodet id-ga " + id)));
    }
}
