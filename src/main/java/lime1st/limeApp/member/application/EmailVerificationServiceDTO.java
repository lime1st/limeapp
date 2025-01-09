package lime1st.limeApp.member.application;

import lime1st.limeApp.member.domain.EmailVerification;

public record EmailVerificationServiceDTO (
        String verificationId,
        String email
){
    public static EmailVerificationServiceDTO fromDomain(EmailVerification emailVerification) {
        return new EmailVerificationServiceDTO(
                emailVerification.verificationId(),
                emailVerification.email()
        );
    }

    public EmailVerification toDomain() {
        return new EmailVerification(this.verificationId, this.email);
    }
}
