package org.example.oop_projekt.Kontrollerid;


import org.example.oop_projekt.DTO.autentimine.Token;
import org.example.oop_projekt.DTO.ostukorv.ToodeOstukorvisIdDTO;
import org.example.oop_projekt.mudel.Kliendikaardid;
import org.example.oop_projekt.mudel.Pood;
import org.example.oop_projekt.mudel.ToodeOstukorvis;
import org.example.oop_projekt.repository.PoodRepository;
import org.example.oop_projekt.repository.ToodeOstukorvisRepository;
import org.example.oop_projekt.teenuskiht.ariloogika.OstukorvTeenus;
import org.example.oop_projekt.teenuskiht.autentimine.AuthTeenus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/ostukorv/uuenda-toodet")
@CrossOrigin(origins = "http://localhost:5173")
public class UuendaOstukorviToodet {

    private final OstukorvTeenus ostukorvTeenus;
    private final ToodeOstukorvisRepository toodeOstukorvisRepository;
    private final PoodRepository poodRepository;
    private final AuthTeenus authTeenus;

    @Autowired
    public UuendaOstukorviToodet(OstukorvTeenus ostukorvTeenus, ToodeOstukorvisRepository toodeOstukorvisRepository, PoodRepository poodRepository, AuthTeenus authTeenus){
        this.ostukorvTeenus = ostukorvTeenus;
        this.toodeOstukorvisRepository = toodeOstukorvisRepository;
        this.poodRepository = poodRepository;
        this.authTeenus = authTeenus;
    }

    // Uuendab mingit kindlat toodet mingis ostukorvis
    @PostMapping
    public ResponseEntity<?> uuendaToode(@RequestBody ToodeOstukorvisIdDTO toodeOstukorvisIdDTO){//Java teisendab automaatselt jsoni DTO-ks
        try {
            long id = toodeOstukorvisIdDTO.id();
            ToodeOstukorvis toode = toodeOstukorvisRepository.findToodeOstukorvisById(id);
            Pood pood = poodRepository.findPoodByNimi(toodeOstukorvisIdDTO.pood().toUpperCase());
            Token token = new Token(toodeOstukorvisIdDTO.token());
            List<Kliendikaardid> kliendikaardid = authTeenus.getKliendikaardid(token);
            boolean omabKliendikaarti = kliendikaardid.stream().anyMatch(kliendikaart -> kliendikaart.getPoeNimi().equalsIgnoreCase(pood.getNimi()));
            ostukorvTeenus.uuendaTooteHind(toode, pood, token, omabKliendikaarti);
            return ResponseEntity.ok(Map.of("sonum", "Ostukorv edukalt loodud", "id", id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("sonum", e.getMessage()));
        }
    }
}