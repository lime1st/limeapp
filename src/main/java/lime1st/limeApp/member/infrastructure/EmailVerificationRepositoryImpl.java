package lime1st.limeApp.member.infrastructure;

import lime1st.limeApp.member.application.EmailVerificationRepository;
import lime1st.limeApp.member.application.EmailVerificationServiceDTO;
import lime1st.limeApp.member.infrastructure.entity.EmailVerificationEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class EmailVerificationRepositoryImpl implements EmailVerificationRepository {

    private final EmailVerificationEntityRepository repository;

    public EmailVerificationRepositoryImpl(EmailVerificationEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<EmailVerificationServiceDTO> save(EmailVerificationServiceDTO serviceDTO) {
        return repository.save(EmailVerificationEntity.fromService(serviceDTO))
                .map(EmailVerificationEntity::toService);
    }

    @Override
    public Optional<EmailVerificationServiceDTO> findById(String id) {
        return repository.findById(id).map(EmailVerificationEntity::toService);
    }

    @Override
    public Optional<EmailVerificationServiceDTO> findByEmail(String email) {
        return repository.findByEmail(email).map(EmailVerificationEntity::toService);
    }
}
