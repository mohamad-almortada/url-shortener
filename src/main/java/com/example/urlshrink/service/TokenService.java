package com.example.urlshrink.service;

import com.example.urlshrink.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;


    public String generateToken(User user) {
        Instant now = Instant.now();
        String scope = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(user.getUsername())
                .claim("scope", scope)
                .claim("email", user.getEmail())
                .claim("expiration", Date.from(now.plus(1, ChronoUnit.HOURS)))
                .build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet))
                .getTokenValue();
    }

    private Map<String, Object> retrieveClaimsFrom(String token) {
        return this.jwtDecoder.decode(token).getClaims();
    }

    public <T> T extractClaim(String token, Function<Map<String, Object>, T> claimsResolver) {
        final Map<String, Object> claims = retrieveClaimsFrom(token);
        return claimsResolver.apply(claims);
    }
    public String retrieveEmail(String token) {
        return extractClaim(token, claims -> (String) claims.get("email"));
    }

    public Date retrieveExpiration(String token) {
        Long expirationEpoch = extractClaim(token, claims -> (Long) claims.get("expiration"));
        return expirationEpoch != null ? new Date(expirationEpoch * 1000) : null;
    }

    public boolean isTokenValid(String token, User user) {
        Date expirationDate = retrieveExpiration(token);
        return expirationDate != null && !isTokenExpired(expirationDate);
    }

    private boolean isTokenExpired(Date date) {
        return date.before(new Date());
    }
}
