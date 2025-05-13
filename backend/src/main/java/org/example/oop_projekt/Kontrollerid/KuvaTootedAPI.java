package org.example.oop_projekt.Kontrollerid;

import org.example.oop_projekt.DTO.HinnaMuutusDTO;
import org.example.oop_projekt.DTO.MarksonaDTO;
import org.example.oop_projekt.DTO.ToodeDTO;
import org.example.oop_projekt.teenuskiht.ariloogika.ToodeTeenus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<ToodeDTO> kuvaTooted(@RequestBody List<MarksonaDTO> marksonad){
        return toodeTeenus.tootedDTOdeks(toodeTeenus.valitudTootedAndmebaasist(marksonad));
    }
}
