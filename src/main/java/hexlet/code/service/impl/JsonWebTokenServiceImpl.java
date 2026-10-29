package hexlet.code.service.impl;

import hexlet.code.service.JsonWebTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class JsonWebTokenServiceImpl implements JsonWebTokenService {
    private final JwtEncoder encoder;

    @Override
    public String generateToken(String username) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer("self").issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS)).subject(username).build();
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
