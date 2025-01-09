package lime1st.limeApp.member.infrastructure;

import lime1st.limeApp.member.application.MemberRepository;
import lime1st.limeApp.member.application.MemberServiceDTO;
import lime1st.limeApp.member.infrastructure.entity.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MemberRepositoryImpl implements MemberRepository<MemberServiceDTO> {

//    기본적인 기능을 사용할 때 JpaRepository 는 구현체가 따로 필요 없으나
//    계층별 DTO 를 사용하고 있으므로 DTO 간 변환이 필요하다....
    private final MemberJpaRepository repository;

    public MemberRepositoryImpl(MemberJpaRepository repository) {
        this.repository = repository;
    }

    //  모든 예외 처리는 application service 레이어에서 처리하기 위해 옵셔널 상태로 리턴한다.

    //  커스텀 메서드,
    //  Spring Data JPA 의 Repository 메서드는 보통 void 나 Optional 을 반환하므로 int 값을 받기 위해
    //  JPQL 을 사용, 나머지 메서드는 메서드시그니처를 사용했다.
    @Override
    public int deleteByMemberIdAndUsername(String memberId, String username) {
        return repository.deleteByMemberIdAndUsername(memberId, username);
    }

//    @Override
    public Optional<MemberServiceDTO> update(MemberServiceDTO serviceDTO) {
        var memberEntity = MemberEntity.fromService(serviceDTO);

//        SpringDataJpa save()는 먼저 엔티티의 기본 키 값이 설정되어 있는지 확인합니다.
//        만약 설정되어 있지 않다면 새로운 엔티티로 간주하고 INSERT 쿼리를 실행합니다.
//        기본 키가 설정되어 있고, 데이터베이스에 해당 기본 키를 가진 엔티티가 존재한다면 해당 엔티티의 상태를 업데이트합니다.
        return repository.save(memberEntity).map(MemberEntity::toService);
    }

    @Override
    public Optional<MemberServiceDTO> save(MemberServiceDTO serviceDTO) {
        var memberEntity = MemberEntity.fromService(serviceDTO);
        return repository.save(memberEntity).map(MemberEntity::toService);
    }

    @Override
    public Page<MemberServiceDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(MemberEntity::toService);
    }

    @Override
    public Optional<MemberServiceDTO> findByUsername(String username) {

        return repository.findByUsername(username).map(MemberEntity::toService);
    }

    @Override
    public Optional<MemberServiceDTO> findByEmail(String email) {
        return repository.findByEmail(email).map(MemberEntity::toService);
    }

    @Override
    public Optional<MemberServiceDTO> findByMemberId(String memberId) {
//        빈 Optional 반환: 데이터를 찾지 못한 경우 빈 Optional 을 반환하면 호출 측에서 이를 처리할 수 있다.
//        데이터가 없을 때 발생하는 상황을 명확하게 표현하고 처리 책임을 상위 레이어로 넘긴다.
//        findById 메서드에서 Optional 을 반환하므로, 데이터를 찾았을 때만 map()을 사용하여 DTO 로 변환.
//	•	  데이터를 찾지 못하면 빈 Optional 이 반환된다.
        return repository.findByMemberId(memberId).map(MemberEntity::toService);
    }

    @Override
    public boolean existsByMemberIdAndUsername(String memberId, String username) {
        return repository.existsByMemberIdAndUsername(memberId, username);
    }
}
