package org.example.oop_projekt.Kontrollerid;

import org.example.oop_projekt.DTO.TokenVerify;
import org.example.oop_projekt.Erindid.TokenKehtetuException;
import org.example.oop_projekt.teenuskiht.autentimine.AuthTeenus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
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
        } catch (TokenKehtetuException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("sonum", e.getMessage()));
        }

    }

}
