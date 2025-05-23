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
import org.example.oop_projekt.teenuskiht.autentimine.AuthTeenus;
import org.springframework.stereotype.Service;

import java.util.*;

// Teenuseklass, mis sisaldab ostukorviga seotud äriloogikat
@Service
public class OstukorvTeenus {

    // Sõltuvused: JPA repository'd, mida kasutatakse andmebaasiga suhtlemiseks
    private final OstukorvRepository ostukorvRepository;
    private final ToodeOstukorvisRepository toodeOstukorvisRepository;
    private final ToodeRepository toodeRepository;
    private final PoodRepository poodRepository;
    private final ToodeTeenus toodeTeenus;
    private final EbasobivToodeRepository ebasobivToodeRepository;
    private final AuthTeenus authTeenus;

    // Konstruktoripõhine sõltuvuste süstimine (Spring süstib bean'id siia)
    public OstukorvTeenus(OstukorvRepository ostukorvRepository,
                          ToodeOstukorvisRepository toodeOstukorvisRepository, ToodeRepository toodeRepository, PoodRepository poodRepository,
                          ToodeTeenus toodeTeenus, EbasobivToodeRepository ebasobivToodeRepository, AuthTeenus authTeenus) {
        this.ostukorvRepository = ostukorvRepository;
        this.toodeOstukorvisRepository = toodeOstukorvisRepository;
        this.toodeRepository = toodeRepository;
        this.poodRepository = poodRepository;
        this.toodeTeenus = toodeTeenus;
        this.ebasobivToodeRepository = ebasobivToodeRepository;
        this.authTeenus = authTeenus;
    }


    @Transactional
    public Long looOstukorv(OstukorvDTO ostukorvDTO) {

        List<ToodeOstukorvis> tootedOstukorvis = new ArrayList<>(); // List, kuhu kõik uued tooted salvestatakse
        Ostukorv ostukorv = new Ostukorv(ostukorvDTO.nimi(), tootedOstukorvis);

        for (ToodeOstukorvisDTO toode : ostukorvDTO.tooted()) {
            ToodeOstukorvis uusToodeOstukorvis = new ToodeOstukorvis(
                    ostukorv,
                    new ArrayList<>(),
                    toode.tooteKogus(),
                    new ArrayList<>());
            ostukorv.getTootedOstukorvis().add(uusToodeOstukorvis);

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
                long toodeId = ebasobivToode.id();
                lisaEbasobivToodeAndmebaasi(toodeId, uusToodeOstukorvis);
            }
        }
        ostukorvRepository.save(ostukorv);
        if (!ostukorvDTO.token().isEmpty()) {
            uuendaHindu(ostukorv, new Token(ostukorvDTO.token()));
        }
        return ostukorv.getId();
    }

    private void lisaEbasobivToodeAndmebaasi(long toodeId, ToodeOstukorvis toodeOstukorvis) {
        EbasobivToode dbEbasobivToode = ebasobivToodeRepository
                .findByToode(toodeRepository.findToodeById(toodeId));
        if (dbEbasobivToode == null) {
            List<ToodeOstukorvis> tootedOstukorvisEST = new ArrayList<>();
            tootedOstukorvisEST.add(toodeOstukorvis);
            EbasobivToode uusEbasobivToode = new EbasobivToode(
                    tootedOstukorvisEST,
                    toodeRepository.findToodeById(toodeId)
            );
            toodeOstukorvis.getEbasobivadTooted().add(uusEbasobivToode);
        } else {
            toodeOstukorvis.getEbasobivadTooted().add(dbEbasobivToode);
        }
    }

    private void uuendaPoeHinnad(List<Kliendikaardid> kliendikaardid, Ostukorv ostukorv, Pood pood, Token token) {
        boolean omabKliendikaarti = kliendikaardid.stream().anyMatch(kliendikaart -> kliendikaart.getPoeNimi().equalsIgnoreCase(pood.getNimi()));

        List<ToodeOstukorvis> tootedOstukorvis = toodeOstukorvisRepository.findToodeOstukorvisByOstukorv(ostukorv);// See rida peaks toimima kohe alguses määrates
        for (ToodeOstukorvis ostukorviToode : tootedOstukorvis) {
            uuendaTooteHind(ostukorviToode, pood, token, omabKliendikaarti);
        }
    }

    @verifyToken
    public void uuendaTooteHind(ToodeOstukorvis ostukorviToode, Pood pood, Token token, boolean omabKliendikaarti) {
        List<MarksonaDTO> marksonad = new ArrayList<>();
        for (TooteMarksona tooteMarksona : ostukorviToode.getTooteMarksonad()) {
            marksonad.add(new MarksonaDTO(tooteMarksona.getMarksona(), tooteMarksona.getVarv()));
        }

        List<EbasobivToode> ebasobivadTooted = ostukorviToode.getEbasobivadTooted();

        TokenMarkSonaDTO tokenMarkSona = new TokenMarkSonaDTO(marksonad, token.token());
        List<Toode> sobivadTooted = toodeTeenus.valitudTootedAndmebaasist(tokenMarkSona)
                .stream()
                .filter(toode -> ebasobivadTooted.stream()
                        .noneMatch(ebasobivToode -> ebasobivToode.getToode().equals(toode)))
                .toList();
        Toode odavaimToode = sobivadTooted
                .stream()
                .filter(t -> t.getPood()
                .equals(pood))
                .min(Comparator.comparingDouble(t -> omabKliendikaarti ? t.getHindKliendi() : t.getTukiHind()
                ))
                .orElse(null);
        if (odavaimToode != null) {
            switch (pood.getNimi().toLowerCase()) {
                case "coop" -> ostukorviToode.setCoopToode(odavaimToode);
                case "prisma" -> ostukorviToode.setPrismaToode(odavaimToode);
                case "maxima" -> ostukorviToode.setBarboraToode(odavaimToode);
                case "rimi" -> ostukorviToode.setRimiToode(odavaimToode);
                case "selver" -> ostukorviToode.setSelverToode(odavaimToode);
            }
        }
    }

    @verifyToken
    @Transactional
    public void jargmineToode(long id, String pood, Token token) {
        ToodeOstukorvis toodeOstukorvis = toodeOstukorvisRepository.findToodeOstukorvisById(id);
        Pood tootePood = poodRepository.findPoodByNimi(pood);
        Toode hetkeToode = leiaPoeToode(tootePood.getNimi(), toodeOstukorvis);
        toodeOstukorvis.getEbasobivadTooted().add(new EbasobivToode());

        assert hetkeToode != null;
        lisaEbasobivToodeAndmebaasi(hetkeToode.getId(), toodeOstukorvis);

        List<Kliendikaardid> kliendikaardid = authTeenus.getKliendikaardid(token);
        boolean omabKliendikaarti = kliendikaardid.stream().anyMatch(kliendikaart ->
                kliendikaart.getPoeNimi().equalsIgnoreCase(tootePood.getNimi()));
        uuendaTooteHind(toodeOstukorvis, tootePood, token, omabKliendikaarti);
        toodeOstukorvisRepository.save(toodeOstukorvis);
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
        return switch (pood.toLowerCase()) {
            case "coop" -> toodeOstukorvis.getCoopToode();
            case "prisma" -> toodeOstukorvis.getPrismaToode();
            case "maxima" -> toodeOstukorvis.getBarboraToode();
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
                                    toode.getKogus(),
                                    toode.getId());
                        }
                        return null;
                    }).toList());
            ostukorvTootedDTO.poed().add(poodDTO);
        }
        return ostukorvTootedDTO;
    }

    @verifyToken
    public void kustutaOstukorv(Ostukorv ostukorv, Token token) {
        ostukorvRepository.deleteById(ostukorv.getId());
    }
}

