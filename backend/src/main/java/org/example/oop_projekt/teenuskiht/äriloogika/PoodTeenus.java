package org.example.oop_projekt.teenuskiht.äriloogika;

import jakarta.transaction.Transactional;
import org.example.oop_projekt.andmepääsukiht.Pood;
import org.example.oop_projekt.andmepääsukiht.PoodRepository;
import org.example.oop_projekt.andmepääsukiht.Toode;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PoodTeenus {

    private final PoodRepository poodRepository;

    public PoodTeenus(PoodRepository poodRepository) {
        this.poodRepository = poodRepository;
    }

    public void lisaToode(Pood pood, Toode toode) {
        pood.getTooted().add(toode);
    }

}
