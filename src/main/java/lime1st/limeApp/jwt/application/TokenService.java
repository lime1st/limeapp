package lime1st.limeApp.jwt.application;

import io.jsonwebtoken.ExpiredJwtException;
import lime1st.limeApp.common.exception.InvalidException;
import lime1st.limeApp.common.exception.NotFoundException;
import lime1st.limeApp.jwt.dto.JwtClaims;
import lime1st.limeApp.jwt.dto.JwtToken;
import lime1st.limeApp.member.application.MemberService;
import lime1st.limeApp.member.application.MemberServiceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenProvider tokenProvider;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    public JwtToken request(String email, String password) {
        MemberServiceDTO dto = memberService.findByEmail(email);

        JwtClaims claims = new JwtClaims(dto.memberId(), dto.email(), dto.username(), dto.role());

        //  password 검증
        if (!passwordEncoder.matches(password, dto.password())) {
            //  패스워드 검증 후 패스워드 오류 보다 그냥 일반 오류를 리턴하는 것이 보안 상 낫다.
            throw new NotFoundException();
        }

        return tokenProvider.requestToken(claims);
    }

    public JwtToken refresh(String accessToken, String refreshToken, String memberId) {
        try {
            //  Access Token 만료 확인(refresh 요청이므로 만료된 것이 정상)
            tokenProvider.validateToken(accessToken);
            log.info("만료 안됨: 기존 토큰 리턴");
            // 예외가 발생하지 않으면 accessToken 의 기한이 만료되지 않은 것 -> 그냥 전달
            return new JwtToken(accessToken, refreshToken);
        } catch (ExpiredJwtException expired) {
            //  accessToken 만료로 refresh 필요
            log.info("accessToken 만료, refresh 확인: 새로운 토큰 리턴");
            try {
                return makeNewToken(memberId, refreshToken);
            } catch (Exception e) {
                // refresh 만료
                throw new InvalidException("Invalid Token");
            }
        } catch (Exception e) {
            throw new InvalidException("Invalid Token");
        }
    }

    //  새로운 Access Token, Refresh Token 생성
    private JwtToken makeNewToken(String memberId, String refreshToken) {
        Map<String, Object> claims = tokenProvider.validateToken(refreshToken);

        log.info("refresh token claims: {}", claims);

        //  Refresh Token 에서 mid 값 추출
        if (!memberId.equals(claims.get("memberId").toString())) {
            throw new InvalidException("Invalid Token");
        }

        //  mid 를 이용해서 사용자 정보를 다시 확인한 후에 새로운 토큰 생성
        MemberServiceDTO dto = memberService.findById(memberId);
        JwtClaims newClaims = new JwtClaims(dto.memberId(), dto.email(), dto.username(), dto.role());

        return tokenProvider.requestToken(newClaims);
    }
}
