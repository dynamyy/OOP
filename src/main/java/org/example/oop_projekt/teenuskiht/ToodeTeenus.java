package org.example.oop_projekt.teenuskiht;

import org.example.oop_projekt.andmepääsukiht.Pood;
import org.example.oop_projekt.andmepääsukiht.Toode;
import org.example.oop_projekt.andmepääsukiht.ToodeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
     * Lisab toote andmebaasi, kui seda seal veel ei ole.
     * @param tooted List toodetest, mis andmebaasi lisatakse.
     */
    public void lisaTootedAndmebaasi(List<Toode> tooted) {
        for (Toode toode : tooted) {
            Toode dbToode = toodeRepository.findToodeByNimetusAndPood(toode.getNimetus(), toode.getPood());
            Pood pood = toode.getPood();
            pood = poodTeenus.getPoodToodetega(pood.getId());

            if (dbToode == null) {
                pood.lisaToode(toode);
                toodeRepository.save(toode);
            } else {
                dbToode.setHindKliendi(toode.getHindKliendi());
                dbToode.setHulgaHind(toode.getHulgaHind());
                dbToode.setHulgaHindKliendi(toode.getHulgaHindKliendi());
                dbToode.setTukiHind(toode.getTukiHind());
                toodeRepository.save(dbToode);
            }
        }
    }
}
