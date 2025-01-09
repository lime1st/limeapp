package lime1st.limeApp.member.application;

import lime1st.limeApp.member.domain.EmailVerification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmailVerificationService {

    private final EmailVerificationRepository repository;

    public EmailVerificationService(EmailVerificationRepository repository) {
        this.repository = repository;
    }

    //    email 로 emailVerification 을 생성해서 verificationId 를 받는다.
    //    id 는 emailVerification 이 생성되어 저장될 때 UUID 가 자동으로 생성된다.
    public String getVerificationIdByEmail(String email) {
        return repository.findByEmail(email)
                .map(EmailVerificationServiceDTO::verificationId)
                .orElse(getVerification(email));
    }

    public String getEmailForVerificationId(String verificationId) {
        return repository.findById(verificationId)
                .map(EmailVerificationServiceDTO::email)
                .orElse(null);
    }

    private String getVerification(String email) {
        var emailVerification = new EmailVerification(null, email);
        return repository
                .save(EmailVerificationServiceDTO.fromDomain(emailVerification))
                .map(EmailVerificationServiceDTO::verificationId)
                .orElse(null);
    }
}
