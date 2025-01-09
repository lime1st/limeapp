package lime1st.limeApp.member.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * ServiceDTO 를 여러개 만들 경우를 대비해서 옵셔널을 사용해 두었다.
* */
public interface MemberRepository<T> {

    //  Spring Data JPA 의 Repository 메서드는 보통 void 나 Optional 을 반환한다.
    //  예를 들어, deleteById는 보통 void 로 선언되어 있다.
    //  만약 boolean 을 반환하도록 선언하면, Spring Data JPA 가 이를 올바르게 처리하지 못하고 null 을 반환할 수 있다.
    //  아래와 같이 선언하면 에러가 날 수 있다. 그러므로 삭제 성공여부를 반환하고 싶으면 @Repository 에서 직접 커스터마이징 해야 한다
    //  (MemberJpaRepository 참조).
    int deleteByMemberIdAndUsername(String memberId, String username);

    Optional<T> save(T n);

    Optional<T> findByEmail(String email);

    Optional<T> findByUsername(String username);

    //  Spring Data JPA 페이징 처리에 Pageable 을 기본적으로 지원한다.
    Page<T> findAll(Pageable pageable);

    Optional<T> findByMemberId(String memberId);

    boolean existsByMemberIdAndUsername(String memberId, String username);
}
