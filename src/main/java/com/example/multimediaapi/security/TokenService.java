package com.example.multimediaapi.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.multimediaapi.model.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    private String secret = "356b1fc8-b865-4f81-8d55-c4dc017cbb2e";

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("multimediaAPI")
                    .withSubject(user.getEmail())
                    .withExpiresAt(this.generateExpirationDate())
                    .sign(algorithm);

            return token;
        }catch (JWTCreationException jce){
            throw new RuntimeException("ERROR WHILE GENERATING TOKEN", jce);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("multimediaAPI")
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (JWTVerificationException jce) {
            throw new RuntimeException("ERROR WHILE VALIDATING TOKEN", jce);
        }
    }

    private Instant generateExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("+01:00"));
    }

}
