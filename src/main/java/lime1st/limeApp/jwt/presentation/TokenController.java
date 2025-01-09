package lime1st.limeApp.jwt.presentation;

import io.jsonwebtoken.ExpiredJwtException;
import lime1st.limeApp.common.exception.NotFoundException;
import lime1st.limeApp.jwt.application.TokenProvider;
import lime1st.limeApp.jwt.dto.JwtClaims;
import lime1st.limeApp.jwt.dto.JwtToken;
import lime1st.limeApp.jwt.dto.TokenRequestDTO;
import lime1st.limeApp.member.application.MemberService;
import lime1st.limeApp.member.application.MemberServiceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/token")
@RequiredArgsConstructor
public class TokenController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/access")
    public ResponseEntity<?> requestToken(@RequestBody TokenRequestDTO tokenRequestDTO) {
        log.info("tokenRequest: {}", tokenRequestDTO);

        // email 로 찾고 없으면?
        MemberServiceDTO dto = memberService.findByEmail(tokenRequestDTO.email());
        JwtClaims claims = new JwtClaims(dto.memberId(), dto.email(), dto.username(), dto.role());
        //  password 검증
        if (!passwordEncoder.matches(tokenRequestDTO.password(), dto.password())) {
            //  패스워드 검증 후 패스워드 오류 보다 그냥 일반 오류를 리턴하는 것이 보안 상 낫다.
            throw new NotFoundException();
        }
        JwtToken token = tokenProvider.requestToken(claims);

        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @RequestHeader("Authorization") String accessTokenStr,
            @RequestParam("refreshToken") String refreshToken,
            @RequestParam("memberId") String memberId
    ) {
        //  전달 받은 값 검증
        if (accessTokenStr == null || !accessTokenStr.startsWith("Bearer ")) {
            return handleException("No Access Token");
        }
        if (refreshToken == null) {
            return handleException("No Refresh Token");
        }
        if (memberId == null) {
            return handleException("No MemberID");
        }

        String accessToken = accessTokenStr.substring(7);


    }
}
