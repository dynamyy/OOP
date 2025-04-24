package org.example.oop_projekt.Kontrollerid;

import org.example.oop_projekt.DTO.SisseLogimine;
import org.example.oop_projekt.Erandid.LoginFailException;
import org.example.oop_projekt.teenuskiht.AuthTeenus;
import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping(path = "api/sisse-logimine")
public class SisseLogimineKontroller {
    private final AuthTeenus authTeenus;

    @Autowired
    public SisseLogimineKontroller(AuthTeenus authTeenus) {
        this.authTeenus = authTeenus;
    }

    @PostMapping
    public void kuvaBroneering(@RequestBody SisseLogimine sisselogimisinfo) {

        try {
            authTeenus.logiKasutajaSisse(sisselogimisinfo);
            System.out.println("Edukalt sisse logitud: " + sisselogimisinfo);
        } catch (LoginFailException e) {
            System.out.println(e.getMessage());
        }

    }

}
