package org.example.oop_projekt.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenVerify {
    private String token;

    public TokenVerify(String token) {
        this.token = token;
    }
}
