package org.example.oop_projekt.annotatsioonid;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.oop_projekt.DTO.autentimine.EmailDTO;
import org.example.oop_projekt.DTO.autentimine.ParoolDTO;
import org.example.oop_projekt.DTO.autentimine.TokenDTO;
import org.example.oop_projekt.Erindid.Autentimine.AuthException;
import org.example.oop_projekt.Erindid.Autentimine.SobimatuParoolException;
import org.example.oop_projekt.mudel.Kasutaja;
import org.example.oop_projekt.repository.KasutajaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.example.oop_projekt.teenuskiht.autentimine.AuthTeenus;

@Aspect
@Component
public class verifyParoolAspect {

    private final AuthTeenus authTeenus;
    private final KasutajaRepository kasutajaRepository;

    @Autowired
    public verifyParoolAspect(AuthTeenus authTeenus, KasutajaRepository kasutajaRepository) {
        this.authTeenus = authTeenus;
        this.kasutajaRepository = kasutajaRepository;
    }

    @Before("@annotation(verifyParool)")
    public void handleVerifyParool(JoinPoint joinPoint) throws SobimatuParoolException{
        Object[] args = joinPoint.getArgs();

        Kasutaja kasutaja = null;
        ParoolDTO paroolDTO = null;

        // Suudab leida kasutaja nii tokeni kui meiliaadressi põhjal
        // Lisaks peab olema autenditava kasutaja sisestuse saamiseks ParoolDTO
        for (Object arg : args) {
            if (arg instanceof TokenDTO dto) {
                kasutaja = authTeenus.getKasutaja(dto);
            } else if (arg instanceof EmailDTO dto) {
                kasutaja = kasutajaRepository.findByEmail(dto.email());
            }

            if (arg instanceof ParoolDTO dto) {
                paroolDTO = dto;
            }

            if (paroolDTO != null && kasutaja != null) {
                break;
            }
        }


        if (kasutaja == null) {
            throw new AuthException("Ei leidnud kasutajat");
        }
        if (paroolDTO == null) {
            throw new SobimatuParoolException("Ei leidnud parooli");
        }
        if (paroolDTO.parool().isEmpty()) {
            throw new SobimatuParoolException("Parooliväli on tühi");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(paroolDTO.parool(), kasutaja.getParool())) {
            throw new SobimatuParoolException("Parool on vale");
        }

    }
}
