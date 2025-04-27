package org.example.oop_projekt.Kontrollerid;


import org.example.oop_projekt.andmepääsukiht.Ostukorv;
import org.example.oop_projekt.andmepääsukiht.ToodeOstukorvis;
import org.example.oop_projekt.teenuskiht.OstukorvTeenus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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


    /*
    //Selle kaudu saab lõpuks ostukorvi välja arvutada
    @PostMapping
    public Ostukorv getOstukorv(@RequestBody Map<String, String> märksõnad){
        return ostukorvTeenus.getOstukorv(märksõnad);
    }
     */


}