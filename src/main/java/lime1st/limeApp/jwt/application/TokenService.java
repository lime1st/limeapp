package lime1st.limeApp.jwt.application;

import io.jsonwebtoken.ExpiredJwtException;
import lime1st.limeApp.jwt.dto.JwtClaims;
import lime1st.limeApp.jwt.dto.JwtToken;
import lime1st.limeApp.member.application.MemberServiceDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class TokenService {

    public refresh() {
        try {
            //  Access Token 만료 확인(refresh 요청이므로 만료된 것이 정상)
            tokenProvider.validateToken(accessToken);
            log.info("만료 안됨: 기존 토큰 리턴");
            // 예외가 발생하지 않으면 accessToken 의 기한이 만료되지 않은 것 -> 그냥 전달
            return ResponseEntity.ok(new JwtToken(accessToken, refreshToken));
        } catch (ExpiredJwtException expired) {
            //  accessToken 만료로 refresh 필요
            log.info("accessToken 만료, refresh 확인: 새로운 토큰 리턴");
            try {
                return ResponseEntity.ok(makeNewToken(memberId, refreshToken));
            } catch (Exception e) {
                // refresh 만료
                return handleException("REFRESH" + e.getMessage());
            }
        } catch (Exception e) {
            return handleException(e.getMessage());
        }
    }

    //  토큰 관련 예외 처리는 컨트롤러에서 별도로 처리
    private ResponseEntity<?> handleException(String msg) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg); // 400
    }

    //  새로운 Access Token, Refresh Token 생성
    private JwtToken makeNewToken(String memberId, String refreshToken) {
        Map<String, Object> claims = tokenProvider.validateToken(refreshToken);

        log.info("refresh token claims: {}", claims);

        //  Refresh Token 에서 mid 값 추출
        if (!memberId.equals(claims.get("memberId").toString())) {
            handleException("Invalid Refresh Token Host");
        }

        //  mid 를 이용해서 사용자 정보를 다시 확인한 후에 새로운 토큰 생성
        MemberServiceDTO dto = memberService.findById(memberId);
        JwtClaims newClaims = new JwtClaims(dto.memberId(), dto.email(), dto.username(), dto.role());

        return tokenProvider.requestToken(newClaims);
    }
}
