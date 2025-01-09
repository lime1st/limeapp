package lime1st.limeApp.member.application;

import java.util.Optional;

public interface EmailVerificationRepository {

    Optional<EmailVerificationServiceDTO> save(EmailVerificationServiceDTO dto);

    Optional<EmailVerificationServiceDTO> findById(String id);

    Optional<EmailVerificationServiceDTO> findByEmail(String email);

}
