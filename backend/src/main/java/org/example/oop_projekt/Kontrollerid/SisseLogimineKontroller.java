package org.example.oop_projekt.Kontrollerid;

import org.example.oop_projekt.DTO.SisseLogimine;
import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping(path = "api/sisse-logimine")
public class SisseLogimineKontroller {

    public SisseLogimineKontroller() {
    }

    @PostMapping
    public void kuvaBroneering(@RequestBody SisseLogimine sisseLogimine) {
        System.out.println(sisseLogimine);
    }

}
