package org.example.oop_projekt.teenuskiht;

import org.example.oop_projekt.DTO.SisseLogimine;
import org.example.oop_projekt.Erindid.LoginFailException;
import org.example.oop_projekt.Erindid.RegistreerimineFailedException;
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
     * @throws RegistreerimineFailedException
     */
    public void registreeriKasutaja(SisseLogimine dto) throws RegistreerimineFailedException {
        // Sisselogimisandmete olemasolu kontroll
        if (dto.getEmail().isEmpty() || dto.getParool().isEmpty()) {
            throw new RegistreerimineFailedException("Kõik väljad peavad olema täidetud");
        }

        if (kasutajaRepository.findByEmail(dto.getEmail()) != null) {
            throw new RegistreerimineFailedException("Selle meiliaadressiga kasutaja on juba olemas");
        }

        /*
        sobivad kõik eesti tähestiku tähed.
         - Peab olema 1 suurtäht ja 1 väiketäht
         - Peab olema 1 number
         - Peab olema vähemalt 8 tähemärki pikk
         */
        String pwRegex = "^(?=.*[a-zäöüõšž])(?=.*[A-ZÄÖÜÕŠŽ])(?=.*\\d)[\\p{L}\\d\\p{P}\\p{S}]{8,}$";

        if (!dto.getParool().matches(pwRegex)) {
            throw new RegistreerimineFailedException("Parool ei vasta nõuetele");
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!dto.getEmail().matches(emailRegex)) {
            throw new RegistreerimineFailedException("Meiliaadress ei vasta nõuetele");
        }

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
}
