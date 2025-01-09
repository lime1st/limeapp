package lime1st.limeApp.member.infrastructure;

import lime1st.limeApp.member.infrastructure.entity.EmailVerificationEntity;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface EmailVerificationEntityRepository extends Repository<EmailVerificationEntity, String> {

    Optional<EmailVerificationEntity> save(EmailVerificationEntity entity);

    Optional<EmailVerificationEntity> findByEmail(String email);

    Optional<EmailVerificationEntity> findById(String id);
}
