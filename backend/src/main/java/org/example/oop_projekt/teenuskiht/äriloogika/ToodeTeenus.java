package org.example.oop_projekt.teenuskiht.äriloogika;

import org.example.oop_projekt.DTO.MärksõnaDTO;
import org.example.oop_projekt.DTO.ToodeDTO;
import org.example.oop_projekt.andmepääsukiht.Pood;
import org.example.oop_projekt.andmepääsukiht.Toode;
import org.example.oop_projekt.andmepääsukiht.ToodeRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


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
                poodTeenus.lisaToode(pood, toode);
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

    // Meetod, mis kuvab kasutajale valitud märksõnaga tooted
    public List<ToodeDTO> valitudTootedAndmebaasist(List<MärksõnaDTO> märksõnad) {
        List<String> rohelised = new ArrayList<>();
        List<String> punased = new ArrayList<>();

        for (MärksõnaDTO märksõna : märksõnad) {

            if (märksõna.valikuVärv().equalsIgnoreCase("roheline")) {
                rohelised.add("%" + märksõna.märksõna() + "%"); // % märk laseb võrrelda substringe
            } else if (märksõna.valikuVärv().equalsIgnoreCase("punane")) {
                punased.add("%" + märksõna.märksõna() + "%");
            }
        }

        // Leiame kõik rohelised ja punased tooted
        List<ToodeDTO> rohelisedTooted = new ArrayList<>();
        for (String roheline : rohelised) {
            rohelisedTooted.addAll(toodeRepository.leiaToodeNimega(roheline));
        }

        List<ToodeDTO> punasedTooted = new ArrayList<>();

        for (String punane : punased) {
            punasedTooted.addAll(toodeRepository.leiaToodeNimega(punane));
        }

        // Eemaldame roheliste hulgast need, mis on punastes
        Set<String> punasteNimed = punasedTooted.stream()
                .map(ToodeDTO::tooteNimi)
                .collect(Collectors.toSet());

        List<ToodeDTO> lõplikudTooted = rohelisedTooted.stream()
                .filter(toode -> !punasteNimed.contains(toode.tooteNimi()))
                .collect(Collectors.toList());

        return lõplikudTooted;

    }

}
