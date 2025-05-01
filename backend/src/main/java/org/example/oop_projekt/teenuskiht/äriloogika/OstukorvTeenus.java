package org.example.oop_projekt.teenuskiht.äriloogika;

import jakarta.transaction.Transactional;
import org.example.oop_projekt.DTO.EbasobivToodeDTO;
import org.example.oop_projekt.DTO.MärksõnaDTO;
import org.example.oop_projekt.DTO.ToodeOstukorvisDTO;
import org.example.oop_projekt.andmepääsukiht.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// Teenuseklass, mis sisaldab ostukorviga seotud äriloogikat
@Service
public class OstukorvTeenus {

    // Sõltuvused: JPA repository'd, mida kasutatakse andmebaasiga suhtlemiseks
    private final OstukorvRepository ostukorvRepository;
    private final ToodeOstukorvisRepository toodeOstukorvisRepository;
    private final TooteMarksonaRepository tooteMarksonaRepository;
    private final ToodeRepository toodeRepository;
    private final PoodRepository poodRepository;
    private final ToodeTeenus toodeTeenus;
    private final EbasobivToodeRepository ebasobivToodeRepository;

    // Konstruktoripõhine sõltuvuste süstimine (Spring süstib bean'id siia)
    public OstukorvTeenus(OstukorvRepository ostukorvRepository,
                          ToodeOstukorvisRepository toodeOstukorvisRepository, TooteMarksonaRepository tooteMarksonaRepository, ToodeRepository toodeRepository, PoodRepository poodRepository,
                          ToodeTeenus toodeTeenus, EbasobivToodeRepository ebasobivToodeRepository) {
        this.ostukorvRepository = ostukorvRepository;
        this.toodeOstukorvisRepository = toodeOstukorvisRepository;
        this.tooteMarksonaRepository = tooteMarksonaRepository;
        this.toodeRepository = toodeRepository;
        this.poodRepository = poodRepository;
        this.toodeTeenus = toodeTeenus;
        this.ebasobivToodeRepository = ebasobivToodeRepository;
    }

//    // Meetod, mis lisab antud toote ostukorvi
//    @Transactional // Annotatsioon selleks, et kõik andmebaasi muudatused toimuksid korraga
//    public void lisaToodeOstukorvi(Ostukorv ostukorv, Toode toode) {
//
//        // Võetakse olemasolev tootenimekiri ostukorvist
//        List<ToodeOstukorvis> tootedOstukorvis = ostukorv.getTootedOstukorvis();
//
//        // Kontrollitakse, kas see toode on juba ostukorvis olemas
//        ToodeOstukorvis toodeOstukorvis = toodeOstukorvisRepository
//                .findToodeOstukorvisByToodeAndOstukorv(toode, ostukorv);
//
//        if (toodeOstukorvis == null) {
//            // Kui toodet pole veel korvis: luuakse uus ToodeOstukorvis objekt
//            ToodeOstukorvis uusToode = new ToodeOstukorvis(ostukorv, toode, 1);
//            tootedOstukorvis.add(uusToode); // Lisatakse ostukorvi tootenimekirja
//            ostukorv.setTootedOstukorvis(tootedOstukorvis); // Uuendatakse ostukorvi
//            toodeOstukorvisRepository.save(uusToode); // Salvestatakse andmebaasi
//        } else {
//            // Kui toode on juba olemas, suurendatakse kogust
//            Integer kogus = toodeOstukorvis.getKogus();
//            toodeOstukorvis.setKogus(kogus + 1);
//            toodeOstukorvisRepository.save(toodeOstukorvis); // Salvestatakse uuendatud kogus
//        }
//    }

    // Meetod, mis vähendab antud toodete arvu ostukorvis sisendarvu võrra
    @Transactional // Annotatsioon selleks, et kõik andmebaasi muudatused toimuksid korraga
    public void muudaKogust(Ostukorv ostukorv, ToodeOstukorvis toodeOstukorvis, int toodeteArv) {

        // Võetakse olemasolev tootenimekiri ostukorvist
        List<ToodeOstukorvis> tootedOstukorvis = ostukorv.getTootedOstukorvis();

        // Toote koguse vähendamine vastavalt sellele, kui palju tooteid eemaldada soovitakse
        int kogus = toodeOstukorvis.getKogus() - toodeteArv;

        if (kogus < 1) {
            // Kui kogus on väiksem kui 1, siis eemaldatakse toode ostukorvist
            tootedOstukorvis.remove(toodeOstukorvis); // Eemaldatakse toode ostukorvist
            ostukorv.setTootedOstukorvis(tootedOstukorvis); // Uuendatakse ostukorvi tootenimekiri
            ostukorvRepository.save(ostukorv); // Salvestatakse ostukorv andmebaasi
            toodeOstukorvisRepository.delete(toodeOstukorvis); // Eemaldatakse toodeOstukorvis andmebaasist
        } else {
            // Kui kogus on suurem või võrdne 1, siis uuendatakse toote kogust
            toodeOstukorvis.setKogus(kogus); // Uuendatakse kogus
            toodeOstukorvisRepository.save(toodeOstukorvis); // Salvestatakse uuendatud kogus andmebaasi
        }
    }

    /**
     * Loob uue ostukorvi koos toodete, märksõnade ja ebasobivate toodetega.
     *
     * @param tooted List {@link ToodeOstukorvisDTO} tüüpi andmeobjekte, mis sisaldavad kasutaja valitud tooteid ja nende seoseid.
     * @return Salvestatud {@link Ostukorv} objekt koos kõigi seotud andmetega (tooted, märksõnad, ebasobivad tooted).
     */
    @Transactional
    public Ostukorv looOstukorv(List<ToodeOstukorvisDTO> tooted) {

        List<ToodeOstukorvis> tootedOstukorvis = new ArrayList<>(); // List, kuhu kõik uued tooted salvestatakse
        Ostukorv ostuKorv = new Ostukorv(tootedOstukorvis);

        for (ToodeOstukorvisDTO toode : tooted) {
            ToodeOstukorvis uusToodeOstukorvis = new ToodeOstukorvis();

            // Kõik märksõnad lisatakse andmebaasi
            for (MärksõnaDTO marksona : toode.marksonad()) {
                TooteMarksona uusMarksona = new TooteMarksona(
                        marksona.märksõna(),
                        uusToodeOstukorvis,
                        marksona.valikuVärv());
                tooteMarksonaRepository.save(uusMarksona);
            }

            // Kõik ebasobivad tooted lisatakse andmebaasi
            for (EbasobivToodeDTO ebasobivToode : toode.ebasobivadTooted()) {
                EbasobivToode uusEbasobivToode = new EbasobivToode(
                        uusToodeOstukorvis,
                        toodeRepository.findToodeByNimetusAndPood(
                                ebasobivToode.nimetus(),
                                poodRepository.findPoodByNimi(ebasobivToode.pood())
                        )
                );
                ebasobivToodeRepository.save(uusEbasobivToode);
            }

            toodeOstukorvisRepository.save(uusToodeOstukorvis);
        }

        ostukorvRepository.save(ostuKorv);
        uuendaHindu(ostuKorv);

        return ostuKorv;
    }

    @Transactional
    public void uuendaHindu(Ostukorv ostukorv) {

        List<Pood> poed = poodRepository.findAll();
        List<Kliendikaardid> kliendikaardid = new ArrayList<>();

    }
}

