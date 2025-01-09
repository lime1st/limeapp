package lime1st.limeApp.member.infrastructure.entity;

import jakarta.persistence.*;
import lime1st.limeApp.member.application.EmailVerificationServiceDTO;

@Entity
@Table(name = "la_email_verification")
public class EmailVerificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String verificationId;

    private String email;

    public EmailVerificationEntity() {
    }

    public EmailVerificationEntity(String verificationId, String email) {
        this.verificationId = verificationId;
        this.email = email;
    }

    public static EmailVerificationEntity fromService(EmailVerificationServiceDTO dto) {
        return new EmailVerificationEntity(dto.verificationId(), dto.email());
    }

    public EmailVerificationServiceDTO toService() {
        return new EmailVerificationServiceDTO(this.verificationId, this.email);
    }

    public String getVerificationId() {
        return verificationId;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmailVerificationEntity that = (EmailVerificationEntity) o;
        return getVerificationId().equals(that.getVerificationId());
    }

    @Override
    public int hashCode() {
        return getVerificationId().hashCode();
    }

    @Override
    public String toString() {
        return "EmailVerificationEntity{" +
                "verificationId='" + verificationId + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
