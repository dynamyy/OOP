package org.example.oop_projekt.teenuskiht.ariloogika;

import jakarta.transaction.Transactional;
import org.example.oop_projekt.DTO.*;
import org.example.oop_projekt.Erindid.Autentimine.AuthException;
import org.example.oop_projekt.annotatsioonid.verifyToken;
import org.example.oop_projekt.mudel.Kasutaja;
import org.example.oop_projekt.mudel.MuudetudToode;
import org.example.oop_projekt.mudel.Pood;
import org.example.oop_projekt.mudel.Toode;
import org.example.oop_projekt.repository.MuudetudTootedRepository;
import org.example.oop_projekt.repository.ToodeRepository;
import org.example.oop_projekt.teenuskiht.autentimine.AuthTeenus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.example.oop_projekt.specifications.ToodeSpecification;
import java.util.function.Function;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
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
    private final MuudetudTootedRepository muudetudTootedRepository;
    private final PoodTeenus poodTeenus;
    private final AuthTeenus authTeenus;
    private final Logger logger;

    public ToodeTeenus(ToodeRepository toodeRepository, MuudetudTootedRepository muudetudTootedRepository, PoodTeenus poodTeenus, AuthTeenus authTeenus) {
        this.toodeRepository = toodeRepository;
        this.muudetudTootedRepository = muudetudTootedRepository;
        this.poodTeenus = poodTeenus;
        this.logger = LoggerFactory.getLogger(ToodeTeenus.class);
        this.authTeenus = authTeenus;
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
    @verifyToken
    public List<Toode> valitudTootedAndmebaasist(TokenMarkSonaDTO tokeniMarkSona) {
        List<String> rohelised = new ArrayList<>();
        List<String> punased = new ArrayList<>();
        Kasutaja kasutaja = authTeenus.getKasutaja(tokeniMarkSona);

        for (MarksonaDTO marksona : tokeniMarkSona.marksonad()) {

            if (marksona.valikuVarv().equalsIgnoreCase("roheline")) {
                rohelised.add("%" + marksona.marksona() + "%"); // % märk laseb võrrelda substringe
            } else if (marksona.valikuVarv().equalsIgnoreCase("punane")) {
                punased.add("%" + marksona.marksona() + "%");
            }
        }

        Specification<Toode> spec = Specification
                .where(ToodeSpecification.nimetusSisaldabKoiki(rohelised))
                .and(ToodeSpecification.nimetusEiSisaldaUhtegi(punased));

        List<Toode> tulemused = toodeRepository.findAll(spec);

        List<MuudetudToode> muudetudTooted = muudetudTootedRepository
                .leiaKehtivadMuudetudTooted(kasutaja.getId(), LocalDateTime.now());

        Map<Long, MuudetudToode> muudetudMap = new HashMap<>();
        for (MuudetudToode mt : muudetudTooted) {
            muudetudMap.put(mt.getMuudetudTooteID(), mt);
        }


        for (Toode toode : tulemused) {
            MuudetudToode muudetud = muudetudMap.get(toode.getId());
            if (muudetud != null) {
                toode.setHindKliendi(muudetud.getTykihind());
                toode.setHulgaHindKliendi(muudetud.getYhikuhind());
            }
        }

        return tulemused;
    }


    /**
     * Pärib andmebaasist 50 toodet alates etteantud nihkest
     * @param paring Sisaldab märksõnade listi ja nihet
     * @return 50 toodet alates etteantud nihkest
     */
    public KuvaTootedDTO getNToodet(KuvaTootedParingDTO paring) {
        List<String> rohelised = new ArrayList<>();
        List<String> punased = new ArrayList<>();

        for (MarksonaDTO marksona : paring.marksonad()) {

            if (marksona.valikuVarv().equalsIgnoreCase("roheline")) {
                rohelised.add("%" + marksona.marksona() + "%"); // % märk laseb võrrelda substringe
            } else if (marksona.valikuVarv().equalsIgnoreCase("punane")) {
                punased.add("%" + marksona.marksona() + "%");
            }
        }

        int tooteidLehel = 50;
        int kusitavLeht = paring.nihe() / tooteidLehel;
        PageRequest pagerequest = PageRequest.of(kusitavLeht, tooteidLehel, Sort.by("id").ascending());

        Specification<Toode> spec = Specification
                .where(ToodeSpecification.nimetusSisaldabKoiki(rohelised))
                .and(ToodeSpecification.nimetusEiSisaldaUhtegi(punased));

        Page<Toode> leht = toodeRepository.findAll(spec, pagerequest);

        Map<Long, MuudetudToode> muudetudMap = new HashMap<>();
        try {
            Kasutaja kasutaja = authTeenus.getKasutaja(paring.token());
            List<MuudetudToode> muudetudTooted = muudetudTootedRepository
                    .leiaKehtivadMuudetudTooted(kasutaja, LocalDateTime.now());
            muudetudMap = muudetudTooted.stream().collect(Collectors.toMap(MuudetudToode::getMuudetudTooteID, Function.identity()));
        } catch (AuthException ignore) {} // Kui pole sisse logitud, siis ei näita lihtsalt muudetud tooteid

        Map<Long, MuudetudToode> finalMuudetudMap = muudetudMap;
        List<ToodeDTO> tootedDTOdena = leht.getContent().stream().map(toode -> {
            MuudetudToode muutus = finalMuudetudMap.get(toode.getId());
            double tukiHind = muutus != null ? muutus.getTykihind() : toode.getHindKliendi();
            double uhikuHind = muutus != null ? muutus.getYhikuhind() : toode.getHulgaHindKliendi();
            LocalDateTime muutmisAeg = muutus != null ? muutus.getMuutmisAeg() : toode.getViimatiUuendatud();

            return new ToodeDTO(
                    toode.getId(),
                    toode.getNimetus(),
                    tukiHind,
                    uhikuHind,
                    toode.getYhik(),
                    uhikuHind < tukiHind ? "true" : "false",
                    toode.getPood().getNimi(),
                    toode.getTootePiltURL(),
                    muutmisAeg
            );
        }).toList();

        long toodeteKoguarv = leht.getTotalElements();

        return new KuvaTootedDTO(tootedDTOdena, toodeteKoguarv);
    }


    //Meetod, mille abil saab muuta toote hindu läbi frontendi
    @verifyToken
    public void muudaTooteHind(HinnaMuutusDTO hinnaMuutusDTO) {

        ToodeDTO toode = hinnaMuutusDTO.toodeDTO();
        Kasutaja kasutaja = authTeenus.getKasutaja(hinnaMuutusDTO);

        // Kustutame vana kehtiva muudetud toote, kui see eksisteerib
        MuudetudToode vanaMuutus = muudetudTootedRepository.leiaKehtivMuudetudToodeKonkreetne(
                kasutaja.getId(),
                String.valueOf(toode.id()),
                LocalDateTime.now()
        );

        if (vanaMuutus != null) {
            muudetudTootedRepository.delete(vanaMuutus);
        }

        // Lisame uue muudetud toote
        MuudetudToode uusMuutus = new MuudetudToode(
                kasutaja,
                toode.tooteUhikuHind(),
                toode.tooteTukihind(),
                toode.viimatiUuendatud(),
                toode.id()
        );
        muudetudTootedRepository.save(uusMuutus);

        logger.info("Uuendasin toote '{}' hinda kasutajale '{}'", toode.tooteNimi(), kasutaja.getEmail());
    }



    public Toode leiaÜksikToode(Long id, TokenVerify token) {

        Optional<Toode> optionalToode = toodeRepository.findById(id);
        if (optionalToode.isEmpty()) {
            throw new NoSuchElementException("Toodet ID-ga " + id + " ei leitud.");
        }

        Toode toode = optionalToode.get();

        try {
            Kasutaja kasutaja = authTeenus.getKasutaja(token);
            MuudetudToode muudetud = muudetudTootedRepository.leiaKehtivMuudetudToodeKonkreetne(
                    kasutaja.getId(),
                    id.toString(),
                    LocalDateTime.now()
            );

            if (muudetud != null) {
                toode.setHindKliendi(muudetud.getTykihind());
                toode.setHulgaHindKliendi(muudetud.getYhikuhind());
                toode.setViimatiUuendatud(muudetud.getMuutmisAeg());
            }
        } catch (AuthException ignore) {}

        return toode;
    }


}
