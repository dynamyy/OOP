package org.example.oop_projekt.Kontrollerid.Kasutaja;

import org.example.oop_projekt.DTO.autentimine.TokenVerify;
import org.example.oop_projekt.Erindid.Autentimine.AuthException;
import org.example.oop_projekt.teenuskiht.autentimine.AuthTeenus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "api/tokenVerif")
public class TokenVerifyKontroller {
    private final AuthTeenus authTeenus;

    @Autowired
    public TokenVerifyKontroller(AuthTeenus authTeenus) {
        this.authTeenus = authTeenus;
    }

    @PostMapping
    public ResponseEntity<?> verify(@RequestBody TokenVerify token) {

        try {
            authTeenus.verifyToken(token);
            return ResponseEntity.ok(Map.of("sonum", "Token kehtiv"));
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("sonum", e.getMessage()));
        }

    }

}
