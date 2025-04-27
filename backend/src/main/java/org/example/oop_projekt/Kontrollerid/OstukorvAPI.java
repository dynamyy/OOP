package org.example.oop_projekt.Kontrollerid;


import org.example.oop_projekt.DTO.MärksõnaDTO;
import org.example.oop_projekt.DTO.OstukorvDTO;
import org.example.oop_projekt.andmepääsukiht.Ostukorv;
import org.example.oop_projekt.andmepääsukiht.ToodeOstukorvis;
import org.example.oop_projekt.teenuskiht.äriloogika.OstukorvTeenus;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Ostukorv getOstukorv(@RequestBody OstukorvDTO ostukorv){//Java teisendab automaatselt jsoni DTO-ks
        return ostukorvTeenus.looOstukorv(ostukorv);
    }
}