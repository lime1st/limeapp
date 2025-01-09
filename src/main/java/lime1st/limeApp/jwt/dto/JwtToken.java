package lime1st.limeApp.jwt.dto;

public record JwtToken(
        String accessToken,
        String refreshToken
) {
}
