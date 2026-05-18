package ee.ut.eventticketing.checkin.security;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public final class JwtTestTokens {

    public static final String ISSUER = "event-ticketing-gateway";
    public static final String SECRET = "event-ticketing-development-secret-32";

    private JwtTestTokens() {
    }

    public static String create(String... roles) throws Exception {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(ISSUER)
                .subject("test-user")
                .claim("roles", List.of(roles))
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plusSeconds(3600)))
                .build();

        SignedJWT signedJwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        signedJwt.sign(new MACSigner(SECRET.getBytes()));
        return signedJwt.serialize();
    }
}
