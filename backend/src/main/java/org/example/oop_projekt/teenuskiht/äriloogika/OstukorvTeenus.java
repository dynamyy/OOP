package org.example.oop_projekt.teenuskiht.äriloogika;

import jakarta.transaction.Transactional;
import org.example.oop_projekt.andmepääsukiht.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

// Teenuseklass, mis sisaldab ostukorviga seotud äriloogikat
@Service
public class OstukorvTeenus {

    // Sõltuvused: JPA repository'd, mida kasutatakse andmebaasiga suhtlemiseks
    private final OstukorvRepository ostukorvRepository;
    private final ToodeOstukorvisRepository toodeOstukorvisRepository;

    // Konstruktoripõhine sõltuvuste süstimine (Spring süstib bean'id siia)
    public OstukorvTeenus(OstukorvRepository ostukorvRepository,
                          ToodeOstukorvisRepository toodeOstukorvisRepository) {
        this.ostukorvRepository = ostukorvRepository;
        this.toodeOstukorvisRepository = toodeOstukorvisRepository;
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

    /*
    // Tagastab lõpliku ostukorvi elementidest, mis kasutaja on välja valinud
    public Ostukorv getOstukorv(Map<String, String> märksõnad) {
        return ostukorvRepository.LooOstukorv(märksõnad);
    }
     */

}

