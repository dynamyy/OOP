package org.example.oop_projekt.teenuskiht.ariloogika;

import jakarta.transaction.Transactional;
import org.example.oop_projekt.DTO.HinnaMuutusDTO;
import org.example.oop_projekt.DTO.MarksonaDTO;
import org.example.oop_projekt.DTO.ToodeDTO;
import org.example.oop_projekt.annotatsioonid.verifyToken;
import org.example.oop_projekt.mudel.Pood;
import org.example.oop_projekt.mudel.Toode;
import org.example.oop_projekt.repository.ToodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.example.oop_projekt.specifications.ToodeSpecification;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.*;


/**
 * Teenus, mis vastutab parsimistulemuste lisamise eest andmebaasi. Võtab toodete objektide
 * järjendi ning kui toode juba on andmebaasis, siis vajadusel uuendab selle infot,
 * vastasel korral loob aga lisab andmebaasi.
 */
@Service
public class ToodeTeenus {

    private final ToodeRepository toodeRepository;
    private final PoodTeenus poodTeenus;
    private final Logger logger;

    public ToodeTeenus(ToodeRepository toodeRepository, PoodTeenus poodTeenus) {
        this.toodeRepository = toodeRepository;
        this.poodTeenus = poodTeenus;
        this.logger = LoggerFactory.getLogger(ToodeTeenus.class);
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
    @Transactional
    public void lisaTootedAndmebaasi(List<Toode> tooted) {

        List<Toode> uuedTooted = new ArrayList<>();

        Pood pood = tooted.getFirst().getPood();
        LocalDateTime uuendusAeg = LocalDateTime.now();

        int uuendatudToodeteArv = 0;
        for (Toode toode : tooted) {
            Toode dbToode;
            try {
                dbToode = toode.getPood().getNimi().equalsIgnoreCase("prisma") ?
                        toodeRepository.findToodeByTooteKood(toode.getTooteKood()) :
                        toodeRepository.findToodeByNimetusAndPood(toode.getNimetus(), toode.getPood());
            } catch (IncorrectResultSizeDataAccessException e) {
                logger.warn("Leidsin andmebaasist mitu vastet {} tootele {}, tootekood {}", toode.getPood(), toode.getNimetus(), toode.getTooteKood());
                continue;
            }


            if (dbToode == null) {
                toode.setViimatiUuendatud(uuendusAeg);
                uuedTooted.add(toode);
            } else {
                dbToode.setHindKliendi(toode.getHindKliendi());
                dbToode.setHulgaHind(toode.getHulgaHind());
                dbToode.setHulgaHindKliendi(toode.getHulgaHindKliendi());
                dbToode.setTukiHind(toode.getTukiHind());
                dbToode.setTootePiltURL(toode.getTootePiltURL());
                dbToode.setViimatiUuendatud(uuendusAeg);
                uuendatudToodeteArv++;
                toodeRepository.save(dbToode);
            }
        }
        logger.info("Uuendasin andmebaasis {} {} toodet", uuendatudToodeteArv, pood.getNimi());

        for (Toode uusToode : uuedTooted) {
            poodTeenus.lisaToode(pood, uusToode);
            toodeRepository.save(uusToode);
        }
        logger.info("Lisasin andmebaasi {} uut {} toodet", uuedTooted.size(), pood.getNimi());

        // Vanade toodete kustutamine, mis pole seotud ühegi kasutaja ostukorviga
        List<Long> tootedKustutamiseks = toodeRepository.leiaTootedKustutamiseks(pood, uuendusAeg);
        toodeRepository.deleteByIds(tootedKustutamiseks);
        logger.info("Kustutasin andmebaasist {} aegunud {} toodet", tootedKustutamiseks.size(), pood.getNimi());
    }


    // Meetod, mis kuvab kasutajale valitud märksõnaga tooted
    public List<Toode> valitudTootedAndmebaasist(List<MarksonaDTO> marksonad) {
        List<String> rohelised = new ArrayList<>();
        List<String> punased = new ArrayList<>();

        for (MarksonaDTO marksona : marksonad) {

            if (marksona.valikuVarv().equalsIgnoreCase("roheline")) {
                rohelised.add("%" + marksona.marksona() + "%"); // % märk laseb võrrelda substringe
            } else if (marksona.valikuVarv().equalsIgnoreCase("punane")) {
                punased.add("%" + marksona.marksona() + "%");
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

        Specification<Toode> spec = Specification
                .where(ToodeSpecification.nimetusSisaldabKoiki(rohelised))
                .and(ToodeSpecification.nimetusEiSisaldaUhtegi(punased));

        List<Toode> tulemused = toodeRepository.findAll(spec);

        return tulemused;
    }

    public List<ToodeDTO> tootedDTOdeks(List<Toode> tooted) {
        return tooted.stream().map(ToodeDTO::new).distinct().toList();
    }


    //Meetod, mille abil saab muuta toote hindu läbi frontendi
    @verifyToken
    public void muudaTooteHind(HinnaMuutusDTO hinnaMuutusDTO) {
        ToodeDTO toode = hinnaMuutusDTO.toodeDTO();
        logger.info("sain andmed {} uuendamiseks.", toode.tooteNimi());


        // throw AndmeteUuendusException, kui sellist toodet pole andmebaasis vms

        //toodeRepository.uuendaTooteHinda(4, 1);//Hind, mis läheb kliendihinna asemele.
    }


}
