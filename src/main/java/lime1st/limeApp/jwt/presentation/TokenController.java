package lime1st.limeApp.jwt.presentation;

import jakarta.validation.constraints.NotBlank;
import lime1st.limeApp.jwt.application.TokenService;
import lime1st.limeApp.jwt.dto.JwtToken;
import lime1st.limeApp.jwt.dto.TokenRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/token")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;


    @PostMapping("/access")
    public ResponseEntity<?> requestToken(@RequestBody TokenRequestDTO tokenRequestDTO) {
        log.info("tokenRequest: {}", tokenRequestDTO);

        JwtToken token = tokenService.request(tokenRequestDTO.email(), tokenRequestDTO.password());

        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @NotBlank @RequestHeader("Authorization") String accessTokenStr,
            @NotBlank @RequestParam("refreshToken") String refreshToken,
            @NotBlank @RequestParam("memberId") String memberId
    ) {

        String accessToken = accessTokenStr.substring(7);

        return ResponseEntity.ok(tokenService.refresh(accessToken, refreshToken, memberId));
    }
}
