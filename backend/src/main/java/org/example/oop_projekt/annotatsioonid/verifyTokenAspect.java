package org.example.oop_projekt.annotatsioonid;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.oop_projekt.DTO.TokenDTO;
import org.example.oop_projekt.Erindid.Autentimine.TokenKehtetuException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;


@Aspect
@Component
public class verifyTokenAspect {
    @Value("${jwt.secret}")
    private String secret;

    @Before("@annotation(verifyToken)")
    public void handleVerifyToken(JoinPoint joinPoint) throws TokenKehtetuException{
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg instanceof TokenDTO dto) {
                String token = dto.token();
                try {
                    Jws<Claims> claimJws = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes())).build().parseSignedClaims(token);
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

                return;
            }
        }

        throw new TokenKehtetuException("Token on puudu.");
    }
}
