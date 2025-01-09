package lime1st.limeApp.jwt.application;

import io.jsonwebtoken.Jwts;
import lime1st.limeApp.jwt.dto.JwtClaims;
import lime1st.limeApp.jwt.dto.JwtToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenProvider {

    private final TokenProperties tokenProperties;

    public JwtToken requestToken(JwtClaims claims) {
        String access = createToken(claims.getDataMap(), tokenProperties.getAccessExpiration());

        //  refreshToken 에는 memberId 정보만 담는다.
        String refresh = createToken(Map.of("memberId", claims.memberId()), tokenProperties.getRefreshExpiration());

        return new JwtToken(access, refresh);
    }

    public String createToken(Map<String, Object> claims, long min) {

        return Jwts.builder().header()
                .add("typ", "JWT")
                .add("alg", "HS256")
                .and()
                .claims(claims)
                .issuer(tokenProperties.getIssuer())   // 발행인
                .issuedAt(Date.from(ZonedDateTime.now().toInstant()))   // 토큰 발행 시간
                .expiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant()))    //  토큰 만료 시간
                .signWith(tokenProperties.getSecretKey())
                .compact();
    }

    public Map<String, Object> validateToken(String token) {
        return Jwts.parser().verifyWith(tokenProperties.getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
    }
}
