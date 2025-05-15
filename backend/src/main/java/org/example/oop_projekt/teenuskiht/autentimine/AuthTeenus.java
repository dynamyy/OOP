package org.example.oop_projekt.teenuskiht.autentimine;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import org.example.oop_projekt.DTO.autentimine.*;
import org.example.oop_projekt.Erindid.AndmeteUuendusException;
import org.example.oop_projekt.Erindid.Autentimine.AuthException;
import org.example.oop_projekt.Erindid.Autentimine.KasutajaPuudubException;
import org.example.oop_projekt.Erindid.Autentimine.LoginFailException;
import org.example.oop_projekt.Erindid.RegistreerimineFailedException;
import org.example.oop_projekt.Erindid.Autentimine.TokenKehtetuException;
import org.example.oop_projekt.annotatsioonid.verifyParool;
import org.example.oop_projekt.annotatsioonid.verifyToken;
import org.example.oop_projekt.mudel.Kasutaja;
import org.example.oop_projekt.repository.KasutajaRepository;
import org.example.oop_projekt.mudel.Kliendikaardid;
import org.example.oop_projekt.repository.KliendikaardidRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AuthTeenus {
    private final BCryptPasswordEncoder encoder;
    private final KasutajaRepository kasutajaRepository;
    private final KliendikaardidRepository kliendikaardidRepository;
    private final SecretKey key;
    private final String pwRegex;

    @Autowired
    public AuthTeenus(KasutajaRepository kasutajaRepository, KliendikaardidRepository kliendikaardidRepository,
                      @Value("${jwt.secret}") String secret) {
        this.kasutajaRepository = kasutajaRepository;
        this.encoder = new BCryptPasswordEncoder();
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.kliendikaardidRepository = kliendikaardidRepository;

        /*
        sobivad kõik eesti tähestiku tähed.
         - Peab olema 1 suurtäht ja 1 väiketäht
         - Peab olema 1 number
         - Peab olema vähemalt 8 tähemärki pikk
         */
        this.pwRegex = "^(?=.*[a-zäöüõšž])(?=.*[A-ZÄÖÜÕŠŽ])(?=.*\\d)[\\p{L}\\d\\p{P}\\p{S}]{8,}$";
    }

    /**
     * Registreerib uue kasutaja
     * @param dto Kasutaja logimisinfo andmete objekt
     * @throws RegistreerimineFailedException Viga kui kasutaja on olemas; meiliaadress
     * või parool ei vasta nõuetele; kui sama meiliga kasutaja on juba olemas
     */
    public void registreeriKasutaja(Registreerimine dto) throws RegistreerimineFailedException {
        // Sisselogimisandmete olemasolu kontroll
        if (dto.email().isEmpty() || dto.parool().isEmpty()) {
            throw new RegistreerimineFailedException("Kõik väljad peavad olema täidetud");
        }

        if (kasutajaRepository.findByEmail(dto.email()) != null) {
            throw new RegistreerimineFailedException("Selle meiliaadressiga kasutaja on juba olemas");
        }

        if (!dto.parool().matches(pwRegex)) {
            throw new RegistreerimineFailedException("Parool ei vasta nõuetele");
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!dto.email().matches(emailRegex)) {
            throw new RegistreerimineFailedException("Meiliaadress ei vasta nõuetele");
        }

        String hashedParool = encoder.encode(dto.parool());



        Kasutaja kasutaja = new Kasutaja(dto.email(), hashedParool, new ArrayList<>(), new ArrayList<>());
        kasutajaRepository.save(kasutaja);

        for (String poeNimi : dto.kliendikaardid()) {
            Kliendikaardid kliendikaart = new Kliendikaardid(kasutaja, poeNimi);
            kliendikaardidRepository.save(kliendikaart);
        }
    }

    /**
     * Kontrollib kasutaja logimisinfo andmete õigsust
     * @param dto Kasutaja logimisinfo andmete objekt
     * @throws LoginFailException Viga tekib, kui kasutajat
     * ei eksisteeri või parool on vale.
     */
    @verifyParool
    public String logiKasutajaSisse(SisseLogimine dto) throws LoginFailException {
        // Sisselogimisandmete olemasolu kontroll
        if (dto.email().isEmpty() || dto.parool().isEmpty()) {
            throw new LoginFailException("Kõik väljad peavad olema täidetud");
        }

        // Kasutaja andmed andmebaasist
        Kasutaja kasutaja = kasutajaRepository.findByEmail(dto.email());

        // Kui sellise meiliga kasutajat pole,
        // siis ei saa sisse logida
        if (kasutaja == null) {
            throw new LoginFailException("Sisselogimine ebaõnnestus. Sellise meiliaadressiga kasutajat ei ole");
        }

        return TokenHandler.genereeriToken(dto.email());
    }

    public void verifyToken(TokenVerify dto) throws TokenKehtetuException{
        String token = dto.token();

        try {
            Jws<Claims> claimJws = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            Claims claim = claimJws.getPayload();

            if (!claim.getIssuer().equals("ostukorvivordlus")) {
                throw new TokenKehtetuException("Token on kehtetu. Vale väljastaja");
            }

            Date tokeniKehtivusaeg = claim.getExpiration();
            if (tokeniKehtivusaeg == null || tokeniKehtivusaeg.before(new Date())) {
                throw new TokenKehtetuException("Token on kehtetu. Aegunud");
            }
        } catch (JwtException e) {
            throw new TokenKehtetuException("Token on kehtetu. " + e.getMessage());
        }
    }

    @verifyToken
    public List<String> getKasutajaAndmed(KasutajaAndmedDTO kasutajaAndmed) throws AuthException {
        // Kliendikaartide tagastamine
        if (kasutajaAndmed.andmetuup().equals("kliendikaardid")) {
            return getKliendikaardid(kasutajaAndmed).stream().map(Kliendikaardid::getPoeNimi).toList();
        }

        // Muid get päringuid pole implementeeritud
        return new ArrayList<>();
    }

    @verifyToken
    public void setKasutajaAndmed(KasutajaAndmedDTO kasutajaAndmed) throws AuthException, AndmeteUuendusException{
        Kasutaja kasutaja = getKasutaja(kasutajaAndmed);

        // Kliendikaartide uuendus
        if (kasutajaAndmed.andmetuup().equals("kliendikaardid")) {
            List<Kliendikaardid> vanadKaardid = getKliendikaardid(kasutajaAndmed);
            List<String> uuedKaardid = kasutajaAndmed.uusListTuup();

            // Eemaldatud kliendikaartide andmebaasist kustutamine
            // Eemaldan uute hulgast juba andmebaasis olevad kaardid
            vanadKaardid.forEach(kaart -> {
                if (!uuedKaardid.contains(kaart.getPoeNimi())) kliendikaardidRepository.delete(kaart);
                    else uuedKaardid.remove(kaart.getPoeNimi());
                });

            // Lisatud kliendikaartide andmebaasi lisamine
            uuedKaardid.forEach(kaart -> {
                kliendikaardidRepository.save(new Kliendikaardid(kasutaja, kaart));
            });

            return;
        }

        if (kasutajaAndmed.andmetuup().equals("parool")) {
            String vanaParool = kasutajaAndmed.uusListTuup().get(0);
            String uusParool = kasutajaAndmed.uusListTuup().get(1);

            if (!encoder.matches(vanaParool, kasutaja.getParool())) {
                throw new AndmeteUuendusException("Parooli uuendamine ebaõnnestus. Vale parool");
            }

            if (!uusParool.matches(pwRegex)) {
                throw new AndmeteUuendusException("Uus parool ei vasta nõuetele");
            }

            kasutaja.setParool(encoder.encode(uusParool));
            kasutajaRepository.save(kasutaja);
        }
    }

    @verifyToken
    @verifyParool
    @Transactional
    public void kustutaKasutaja(KasutajaKustutamineDTO kustutamisAndmed) {
        // Kõigepealt tuleb kustutada seosed
        kliendikaardidRepository.deleteAllByKasutaja(getKasutaja(kustutamisAndmed));

        // Seejärel saab kustutada kasutaja
        kasutajaRepository.deleteByEmail(getEmail(kustutamisAndmed));
    }

    @verifyToken
    public String getEmail(TokenDTO dto) throws AuthException {
        String token = dto.token();
        Claims claim = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return claim.getSubject();
    }

    @verifyToken
    public Kasutaja getKasutaja(TokenDTO dto) throws AuthException {
        String token = dto.token();
        Claims claim = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        String kasutajaMeil = claim.getSubject();
        Kasutaja kasutaja = kasutajaRepository.findByEmail(kasutajaMeil);

        if (kasutaja == null) {
            throw new KasutajaPuudubException("Ei leidnud kasutajat andmebaasist");
        }

        return kasutaja;
    }

    @verifyToken
    public List<Kliendikaardid> getKliendikaardid(TokenDTO dto) throws AuthException {
        String token = dto.token();
        Claims claim = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        String kasutajaMeil = claim.getSubject();
        Kasutaja kasutaja = kasutajaRepository.findByEmail(kasutajaMeil);

        if (kasutaja == null) {
            throw new KasutajaPuudubException("Ei saa leida kliendikaarte. Ei leidnud kasutajat andmebaasist.");
        }

        return kasutaja.getKliendikaardid();
    }
}
