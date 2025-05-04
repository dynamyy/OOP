package org.example.oop_projekt.teenuskiht.äriloogika;

import org.example.oop_projekt.mudel.Pood;
import org.example.oop_projekt.repository.PoodRepository;
import org.example.oop_projekt.mudel.Toode;
import org.springframework.stereotype.Service;

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
