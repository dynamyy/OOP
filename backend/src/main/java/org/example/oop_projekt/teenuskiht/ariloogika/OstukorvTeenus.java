package org.example.oop_projekt.teenuskiht.ariloogika;

import jakarta.transaction.Transactional;
import org.example.oop_projekt.DTO.autentimine.Token;
import org.example.oop_projekt.DTO.ostukorv.*;
import org.example.oop_projekt.DTO.toode.EbasobivToodeDTO;
import org.example.oop_projekt.DTO.toode.MarksonaDTO;
import org.example.oop_projekt.DTO.toode.TokenMarkSonaDTO;
import org.example.oop_projekt.annotatsioonid.verifyToken;
import org.example.oop_projekt.mudel.*;
import org.example.oop_projekt.repository.EbasobivToodeRepository;
import org.example.oop_projekt.repository.OstukorvRepository;
import org.example.oop_projekt.repository.PoodRepository;
import org.example.oop_projekt.repository.ToodeOstukorvisRepository;
import org.example.oop_projekt.repository.ToodeRepository;
import org.example.oop_projekt.repository.TooteMarksonaRepository;
import org.example.oop_projekt.teenuskiht.autentimine.AuthTeenus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
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
    private final AuthTeenus authTeenus;

    // Konstruktoripõhine sõltuvuste süstimine (Spring süstib bean'id siia)
    public OstukorvTeenus(OstukorvRepository ostukorvRepository,
                          ToodeOstukorvisRepository toodeOstukorvisRepository, TooteMarksonaRepository tooteMarksonaRepository, ToodeRepository toodeRepository, PoodRepository poodRepository,
                          ToodeTeenus toodeTeenus, EbasobivToodeRepository ebasobivToodeRepository, AuthTeenus authTeenus) {
        this.ostukorvRepository = ostukorvRepository;
        this.toodeOstukorvisRepository = toodeOstukorvisRepository;
        this.tooteMarksonaRepository = tooteMarksonaRepository;
        this.toodeRepository = toodeRepository;
        this.poodRepository = poodRepository;
        this.toodeTeenus = toodeTeenus;
        this.ebasobivToodeRepository = ebasobivToodeRepository;
        this.authTeenus = authTeenus;
    }

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


    @Transactional
    public void looOstukorv(OstukorvDTO ostukorv) {

        List<ToodeOstukorvis> tootedOstukorvis = new ArrayList<>(); // List, kuhu kõik uued tooted salvestatakse
        Ostukorv ostuKorv = new Ostukorv(ostukorv.nimi(), tootedOstukorvis);

        for (ToodeOstukorvisDTO toode : ostukorv.tooted()) {
            ToodeOstukorvis uusToodeOstukorvis = new ToodeOstukorvis(
                    ostuKorv,
                    new ArrayList<>(),
                    Integer.valueOf(toode.tooteKogus()),
                    new ArrayList<>());
            ostuKorv.getTootedOstukorvis().add(uusToodeOstukorvis);

            // Kõik uue toote märksõnad lisatakse andmebaasi
            for (MarksonaDTO marksona : toode.marksonad()) {
                TooteMarksona uusMarksona = new TooteMarksona(
                        marksona.marksona(),
                        uusToodeOstukorvis,
                        marksona.valikuVarv());
                uusToodeOstukorvis.getTooteMarksonad().add(uusMarksona);
            }

            // Kõik uue toote ebasobivad tooted lisatakse andmebaasi
            for (EbasobivToodeDTO ebasobivToode : toode.ebasobivadTooted()) {
                EbasobivToode dbEbasobivToode = ebasobivToodeRepository
                        .findByToode(toodeRepository.findToodeById(Long.parseLong(ebasobivToode.id())));
                if (dbEbasobivToode == null) {
                    List<ToodeOstukorvis> tootedOstukorvisEST = new ArrayList<>();
                    tootedOstukorvisEST.add(uusToodeOstukorvis);
                    EbasobivToode uusEbasobivToode = new EbasobivToode(
                            tootedOstukorvisEST,
                            toodeRepository.findToodeById(Long.parseLong(ebasobivToode.id()))
                    );
                    uusToodeOstukorvis.getEbasobivadTooted().add(uusEbasobivToode);
                } else {
                    uusToodeOstukorvis.getEbasobivadTooted().add(dbEbasobivToode);
                }
            }
        }
        ostukorvRepository.save(ostuKorv);
        if (!ostukorv.token().isEmpty()) {
            uuendaHindu(ostuKorv, new Token(ostukorv.token()));
        }
    }

    @verifyToken
    private void uuendaPoeHinnad(List<Kliendikaardid> kliendikaardid, Ostukorv ostukorv, Pood pood, Token token) {
        boolean omabKliendikaarti = kliendikaardid.stream().anyMatch(kliendikaart -> kliendikaart.getPoeNimi().equalsIgnoreCase(pood.getNimi()));

        List<ToodeOstukorvis> tootedOstukorvis = toodeOstukorvisRepository.findToodeOstukorvisByOstukorv(ostukorv);// See rida peaks toimima kohe alguses määrates
        for (ToodeOstukorvis ostukorviToode : tootedOstukorvis) {
            List<MarksonaDTO> marksonad = new ArrayList<>();
            for (TooteMarksona tooteMarksona : ostukorviToode.getTooteMarksonad()) {
                marksonad.add(new MarksonaDTO(tooteMarksona.getMarksona(), tooteMarksona.getVarv()));// SIIA LISASIN dto.token rea
            }

            TokenMarkSonaDTO tokenMarkSona = new TokenMarkSonaDTO(marksonad, token.token());
            List<Toode> sobivadTooted = toodeTeenus.valitudTootedAndmebaasist(tokenMarkSona);// Siin tuleb teha TokenMarkSOna DTO objekt
            Toode odavaimToode = sobivadTooted
                    .stream()
                    .filter(t -> t.getPood()
                            .equals(pood)).toList()
                    .stream()
                    .min(Comparator.comparingDouble(t -> omabKliendikaarti ? t.getHulgaHindKliendi() : t.getHulgaHind()
                    ))
                    .orElse(null);
            if (odavaimToode != null) {
                switch (pood.getNimi().toLowerCase()) {
                    case "coop" -> ostukorviToode.setCoopToode(odavaimToode);
                    case "prisma" -> ostukorviToode.setPrismaToode(odavaimToode);
                    case "barbora" -> ostukorviToode.setBarboraToode(odavaimToode);
                    case "rimi" -> ostukorviToode.setRimiToode(odavaimToode);
                    case "selver" -> ostukorviToode.setSelverToode(odavaimToode);
                }
            }
        }
    }
    
    
    @Transactional
    @verifyToken
    public void uuendaHindu(Ostukorv ostukorv, Token token) {

        ostukorv.setKasutaja(authTeenus.getKasutaja(token));
        List<Pood> poed = poodRepository.findAll();
        List<Kliendikaardid> kliendikaardid = authTeenus.getKliendikaardid(token);

        for (Pood pood : poed) {
            uuendaPoeHinnad(kliendikaardid, ostukorv, pood, token);
        }
        ostukorvRepository.save(ostukorv);
    }

    private Toode leiaPoeToode(String pood, ToodeOstukorvis toodeOstukorvis) {
        return switch (pood) {
            case "coop" -> toodeOstukorvis.getCoopToode();
            case "prisma" -> toodeOstukorvis.getPrismaToode();
            case "barbora" -> toodeOstukorvis.getBarboraToode();
            case "rimi" -> toodeOstukorvis.getRimiToode();
            case "selver" -> toodeOstukorvis.getSelverToode();
            default -> null;
        };
    }

    @verifyToken
    public OstukorvTootedDTO looOstukorviDTO(Ostukorv ostukorv, Token token) {

        OstukorvTootedDTO ostukorvTootedDTO = new OstukorvTootedDTO(ostukorv.getNimi(), new ArrayList<>());

        List<Pood> poed = poodRepository.findAll();
        List<Kliendikaardid> kliendikaardid = authTeenus.getKliendikaardid(token);

        for (Pood pood : poed) {
            OstukorvPoodDTO poodDTO = new OstukorvPoodDTO(pood.getNimi(), new ArrayList<>());

            boolean omabKliendikaarti = kliendikaardid
                    .stream()
                    .anyMatch(kliendikaart -> kliendikaart
                            .getPoeNimi().equalsIgnoreCase(pood.getNimi()));

            poodDTO.tooted().addAll(
                    ostukorv.getTootedOstukorvis().stream().map(toode -> {
                        Toode t = leiaPoeToode(pood.getNimi().toLowerCase(), toode);
                        if (!(t == null)) {
                            return new ToodeOStukorvisArvutatudDTO(
                                    t.getNimetus(),
                                    omabKliendikaarti ? t.getHindKliendi() : t.getTukiHind(),
                                    omabKliendikaarti ? t.getHulgaHindKliendi() : t.getHulgaHind(),
                                    t.getTootePiltURL(),
                                    toode.getKogus());
                        }
                        return null;
                    }).toList());
            ostukorvTootedDTO.poed().add(poodDTO);
        }
        return ostukorvTootedDTO;
    }
}

