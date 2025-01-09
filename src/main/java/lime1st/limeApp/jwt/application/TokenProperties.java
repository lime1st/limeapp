package lime1st.limeApp.jwt.application;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
@Getter
@Setter
@ConfigurationProperties("jwt")
public class TokenProperties {

    private SecretKey secretKey;

    /*
    * application.properties jwt
    * */
    private String issuer;

    // 토큰의 만료시간 지정
    private Long accessExpiration;
    private Long refreshExpiration;

    private String secret;

    public SecretKey getSecretKey(){
        if (secretKey == null) {
            secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        }
        return secretKey;
    }
}
