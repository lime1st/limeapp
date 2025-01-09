package lime1st.limeApp.member.domain;

public record EmailVerification(
        String verificationId,
        String email
) {
    public EmailVerification withId(String id) {
        return new EmailVerification(id, email());
    }
}
