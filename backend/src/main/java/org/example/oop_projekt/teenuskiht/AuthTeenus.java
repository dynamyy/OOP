package org.example.oop_projekt.teenuskiht;

import org.example.oop_projekt.DTO.SisseLogimine;
import org.example.oop_projekt.Erindid.LoginFailException;
import org.example.oop_projekt.andmepääsukiht.Kasutaja;
import org.example.oop_projekt.andmepääsukiht.KasutajaRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthTeenus {
    private final BCryptPasswordEncoder encoder;
    private final KasutajaRepository kasutajaRepository;

    @Autowired
    public AuthTeenus(KasutajaRepository kasutajaRepository) {
        this.kasutajaRepository = kasutajaRepository;
        this.encoder = new BCryptPasswordEncoder();
    }

    /**
     * Registreerib uue kasutaja
     * @param dto Kasutaja logimisinfo andmete objekt
     */
    public void registreeriKasutaja(SisseLogimine dto) {
        String hashedParool = encoder.encode(dto.getParool());

        Kasutaja kasutaja = new Kasutaja();

        kasutaja.setParool(hashedParool);
        kasutaja.setEmail(dto.getEmail());

        kasutajaRepository.save(kasutaja);
    }

    /**
     * Kontrollib kasutaja logimisinfo andmete õigsust
     * @param dto Kasutaja logimisinfo andmete objekt
     * @throws LoginFailException Viga tekib, kui kasutajat
     * ei eksisteeri või parool on vale.
     */
    public void logiKasutajaSisse(SisseLogimine dto) throws LoginFailException {
        // Kasutaja andmed andmebaasist
        Kasutaja kasutaja = kasutajaRepository.findByEmail(dto.getEmail());

        // Kui sellise meiliga kasutajat pole,
        // siis ei saa sisse logida
        if (kasutaja == null) {
            throw new LoginFailException("Sisselogimine ebaõnnestus. Sellise meiliaadressiga kasutajat ei ole");
        };

        // Parooli õigsuse kontroll ja tagastus
        if (!encoder.matches(dto.getParool(), kasutaja.getParool())) {
            throw new LoginFailException("Sisselogimine ebaõnnestus. Vale parool");
        }
    }
}
