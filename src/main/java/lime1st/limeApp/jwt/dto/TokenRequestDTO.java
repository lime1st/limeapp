package lime1st.limeApp.jwt.dto;

public record TokenRequestDTO (
        String email,
        String password
) {
}
