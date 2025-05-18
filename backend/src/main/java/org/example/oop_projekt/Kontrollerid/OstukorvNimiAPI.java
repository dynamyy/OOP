package org.example.oop_projekt.Kontrollerid;

import org.example.oop_projekt.DTO.autentimine.Token;
import org.example.oop_projekt.DTO.ostukorv.KasutajaOstukorvidDTO;
import org.example.oop_projekt.DTO.ostukorv.OstukorvNimiIdDTO;
import org.example.oop_projekt.Erindid.Autentimine.AuthException;
import org.example.oop_projekt.mudel.Ostukorv;
import org.example.oop_projekt.repository.OstukorvRepository;
import org.example.oop_projekt.teenuskiht.autentimine.AuthTeenus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/ostukorv/nimed")
@CrossOrigin(origins = "http://localhost:5173")
public class OstukorvNimiAPI {

    private final OstukorvRepository ostukorvRepository;
    private final AuthTeenus authTeenus;

    @Autowired
    public OstukorvNimiAPI(OstukorvRepository ostukorvRepository, AuthTeenus authTeenus) {
        this.ostukorvRepository = ostukorvRepository;
        this.authTeenus = authTeenus;
    }

    @PostMapping
    public ResponseEntity<?> getOstukorv(@RequestBody Token token){//Java teisendab automaatselt jsoni DTO-ks

        try {
            List<Ostukorv> ostukorvid = ostukorvRepository.findOstukorvByKasutaja(authTeenus.getKasutaja(token));
            KasutajaOstukorvidDTO kasutajaOstukorvid = new KasutajaOstukorvidDTO(new ArrayList<>());
            ostukorvid.forEach(ostukorv -> kasutajaOstukorvid.ostukorvid().add(new OstukorvNimiIdDTO(ostukorv.getNimi(), ostukorv.getId())));
            return ResponseEntity.ok(Map.of("sonum", "Ostukorvid käes", "ostukorvid", kasutajaOstukorvid));
        } catch(AuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("sonum", e.getMessage()));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("sonum", e.getMessage()));
        }
    }
}
