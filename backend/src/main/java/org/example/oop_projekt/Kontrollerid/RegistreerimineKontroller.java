package org.example.oop_projekt.Kontrollerid;

import org.example.oop_projekt.DTO.SisseLogimine;
import org.example.oop_projekt.teenuskiht.AuthTeenus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping(path = "api/registreeri")
public class RegistreerimineKontroller {
    private final AuthTeenus authTeenus;

    @Autowired
    public RegistreerimineKontroller(AuthTeenus authTeenus) {
        this.authTeenus = authTeenus;
    }

    @PostMapping
    public void registreeri(@RequestBody SisseLogimine sisselogimisinfo) {
        authTeenus.registreeriKasutaja(sisselogimisinfo);
        System.out.println("Loodud kasutaja " + sisselogimisinfo);
    }
}
