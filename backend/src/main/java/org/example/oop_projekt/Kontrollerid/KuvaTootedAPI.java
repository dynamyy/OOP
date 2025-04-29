package org.example.oop_projekt.Kontrollerid;

import org.example.oop_projekt.DTO.MärksõnaDTO;
import org.example.oop_projekt.DTO.ToodeDTO;
import org.example.oop_projekt.teenuskiht.äriloogika.ToodeTeenus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/tooted")
@CrossOrigin(origins = "http://localhost:5173")
public class KuvaTootedAPI {


    private ToodeTeenus toodeTeenus;

    KuvaTootedAPI (ToodeTeenus toodeTeenus){
        this.toodeTeenus = toodeTeenus;
    }

    // Kasutame toodete kuvamiseks kasutajale
    @PostMapping
    public List<ToodeDTO> kuvaTooted(@RequestBody List<MärksõnaDTO> märksõnad){
        return toodeTeenus.valitudTootedAndmebaasist(märksõnad);
    }
}
