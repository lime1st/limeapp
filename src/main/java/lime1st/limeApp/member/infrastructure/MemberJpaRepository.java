package lime1st.limeApp.member.infrastructure;

import lime1st.limeApp.member.application.MemberRepository;
import lime1st.limeApp.member.infrastructure.entity.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

// 필요한 메서드만 생성하도록 Repository 를 확장했다.
public interface MemberJpaRepository extends Repository<MemberEntity, String>,
        MemberRepository<MemberEntity> {

    // 데이터 변경 쿼리(update, delete 등)에는 @Modifying 을 함께 사용
    @Override
    @Modifying
    @Query("delete from MemberEntity me where me.memberId = :memberId and me.username = :username")
    int deleteByMemberIdAndUsername(@Param("memberId") String memberId,
                                    @Param("username") String username);

    @Override
    @Query("select me from MemberEntity me")
    Page<MemberEntity> findAll(Pageable pageable);
}
