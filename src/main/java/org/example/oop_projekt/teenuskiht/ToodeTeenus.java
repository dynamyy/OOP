package org.example.oop_projekt.teenuskiht;

import org.example.oop_projekt.andmepääsukiht.Pood;
import org.example.oop_projekt.andmepääsukiht.Toode;
import org.example.oop_projekt.andmepääsukiht.ToodeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Teenus, mis vastutab parsimistulemuste lisamise eest andmebaasi. Võtab toodete objektide
 * järjendi ning kui toode juba on andmebaasis, siis vajadusel uuendab selle infot,
 * vastasel korral loob aga lisab andmebaasi.
 */
@Service
public class ToodeTeenus {

    private final ToodeRepository toodeRepository;
    private final PoodTeenus poodTeenus;

    public ToodeTeenus(ToodeRepository toodeRepository, PoodTeenus poodTeenus) {
        this.toodeRepository = toodeRepository;
        this.poodTeenus = poodTeenus;
    }

    /**
     * Tagastab Listi kõikidest toodetest.
     * @return List kõikidest toodetetst.
     */
    public List<Toode> getTooted() {
        return toodeRepository.findAll();
    }

    /**
     * Lisab toote andmebaasi, kui seda seal veel ei ole,
     * kui on, siis muudab vajadusel selle andmeid.
     * @param tooted
     */
    public void lisaTootedAndmebaasi(List<Toode> tooted) {
        for (Toode toode : tooted) {

            Toode dbToode = toodeRepository.findToodeByNimetus(toode.getNimetus());
            Pood pood = toode.getPoed().iterator().next();
            pood = poodTeenus.getPoodToodetega(pood.getId());

            // Kui toode on olemas, siis lisab
            if (dbToode != null) {
                dbToode.lisaPood(pood);
                pood.lisaToode(dbToode);
                toodeRepository.save(dbToode);
            } else { // Kui toodet pole, lisab selle.
                toode.lisaPood(pood);
                pood.lisaToode(toode);
                toodeRepository.save(toode);
            }
        }
    }
}
