package org.example.oop_projekt.teenuskiht.autentimine;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.oop_projekt.DTO.KasutajaAndmedDTO;
import org.example.oop_projekt.DTO.Registreerimine;
import org.example.oop_projekt.DTO.SisseLogimine;
import org.example.oop_projekt.DTO.TokenVerify;
import org.example.oop_projekt.Erindid.LoginFailException;
import org.example.oop_projekt.Erindid.RegistreerimineFailedException;
import org.example.oop_projekt.Erindid.TokenKehtetuException;
import org.example.oop_projekt.andmepääsukiht.Kasutaja;
import org.example.oop_projekt.andmepääsukiht.KasutajaRepository;
import org.example.oop_projekt.andmepääsukiht.Kliendikaardid;
import org.example.oop_projekt.andmepääsukiht.KliendikaardidRepository;
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

    @Autowired
    public AuthTeenus(KasutajaRepository kasutajaRepository, KliendikaardidRepository kliendikaardidRepository,
                      @Value("${jwt.secret}") String secret) {
        this.kasutajaRepository = kasutajaRepository;
        this.encoder = new BCryptPasswordEncoder();
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.kliendikaardidRepository = kliendikaardidRepository;
    }

    /**
     * Registreerib uue kasutaja
     * @param dto Kasutaja logimisinfo andmete objekt
     * @throws RegistreerimineFailedException
     */
    public void registreeriKasutaja(Registreerimine dto) throws RegistreerimineFailedException {
        // Sisselogimisandmete olemasolu kontroll
        if (dto.email().isEmpty() || dto.parool().isEmpty()) {
            throw new RegistreerimineFailedException("Kõik väljad peavad olema täidetud");
        }

        if (kasutajaRepository.findByEmail(dto.email()) != null) {
            throw new RegistreerimineFailedException("Selle meiliaadressiga kasutaja on juba olemas");
        }

        /*
        sobivad kõik eesti tähestiku tähed.
         - Peab olema 1 suurtäht ja 1 väiketäht
         - Peab olema 1 number
         - Peab olema vähemalt 8 tähemärki pikk
         */
        String pwRegex = "^(?=.*[a-zäöüõšž])(?=.*[A-ZÄÖÜÕŠŽ])(?=.*\\d)[\\p{L}\\d\\p{P}\\p{S}]{8,}$";

        if (!dto.parool().matches(pwRegex)) {
            throw new RegistreerimineFailedException("Parool ei vasta nõuetele");
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!dto.email().matches(emailRegex)) {
            throw new RegistreerimineFailedException("Meiliaadress ei vasta nõuetele");
        }

        String hashedParool = encoder.encode(dto.parool());



        Kasutaja kasutaja = new Kasutaja(dto.email(), hashedParool, new ArrayList<>());
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
    public void logiKasutajaSisse(SisseLogimine dto) throws LoginFailException {
        // Sisselogimisandmete olemasolu kontroll
        if (dto.getEmail().isEmpty() || dto.getParool().isEmpty()) {
            throw new LoginFailException("Kõik väljad peavad olema täidetud");
        }

        // Kasutaja andmed andmebaasist
        Kasutaja kasutaja = kasutajaRepository.findByEmail(dto.getEmail());

        // Kui sellise meiliga kasutajat pole,
        // siis ei saa sisse logida
        if (kasutaja == null) {
            throw new LoginFailException("Sisselogimine ebaõnnestus. Sellise meiliaadressiga kasutajat ei ole");
        }

        // Parooli õigsuse kontroll ja tagastus
        if (!encoder.matches(dto.getParool(), kasutaja.getParool())) {
            throw new LoginFailException("Sisselogimine ebaõnnestus. Vale parool");
        }
    }

    public void verifyToken(TokenVerify dto) throws TokenKehtetuException{
        String token = dto.getToken();

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

    public List<String> getKasutajaAndmed(KasutajaAndmedDTO kasutajaAndmed) throws TokenKehtetuException{
        // Tokeni check
        String token = kasutajaAndmed.token();
        verifyToken(new TokenVerify(token));

        Claims claim = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        String kasutajaMeil = claim.getSubject();
        Kasutaja kasutaja = kasutajaRepository.findByEmail(kasutajaMeil);

        // Kliendikaartide tagastamine
        if (kasutajaAndmed.andmetuup().equals("kliendikaardid")) {
            return kasutaja.getKliendikaardid().stream().map(Kliendikaardid::getPoeNimi).toList();
        }

        return new ArrayList<>();
    }

    public void setKasutajaAndmed(KasutajaAndmedDTO kasutajaAndmed) throws TokenKehtetuException{
        // Tokeni check
        String token = kasutajaAndmed.token();
        verifyToken(new TokenVerify(token));

        Claims claim = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        String kasutajaMeil = claim.getSubject();
        Kasutaja kasutaja = kasutajaRepository.findByEmail(kasutajaMeil);

        // Kliendikaartide uuendus
        if (kasutajaAndmed.andmetuup().equals("kliendikaardid")) {
            List<Kliendikaardid> vanadKaardid = kasutaja.getKliendikaardid();
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
        }
    }
}
