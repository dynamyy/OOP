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

    @Transactional
    public Pood getPoodToodetega(Long id) {
        Pood pood = poodRepository.findById(id).orElseThrow();
        pood.getTooted().size(); // Toodete arvu leidmine sunnib poe lazy olekust välja ja seda saab kasutada.
                                // Mulle see lahendus hetkel eriti ei meeldi, aga ei osanud muud välja mõelda.
        return pood;
    }

    public void lisaToode(Pood pood, Toode toode) {
        Set<Toode> tooted = pood.getTooted();
        tooted.add(toode);
        pood.setTooted(tooted);
    }

}
