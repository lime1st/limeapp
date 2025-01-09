package lime1st.limeApp.member.infrastructure;

import lime1st.limeApp.member.application.MemberServiceDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * 테스트 데이터 test/resources/data.sql
 * */
@DataJpaTest
@ComponentScan("lime1st.limeApp.member.infrastructure")
//@ActiveProfiles("test") //  spring.profiles.active 를 지정할 때 사용
@TestPropertySource(locations = "classpath:application-test.properties")    // 프로파일을 직접 지정할 때 사용
class MemberRepositoryImplTest {

    private static final Logger log = LoggerFactory.getLogger(MemberRepositoryImplTest.class);

    @Autowired
    MemberRepositoryImpl repository;

    @Test
    void test() {

    }

    @Test
    @DisplayName("deleteByMemberIdAndUsername: ")
    void deleteByMemberIdAndUsername_test() {
        //  when
        repository.deleteByMemberIdAndUsername("99", "alice");

        //  then
        assertThat(repository.existsByMemberIdAndUsername("99", "alice")).isFalse();
    }

    @Test
    @DisplayName("update:")
    void update_test() {

    }

    @Test
    @DisplayName("save: 저장 데이터 확인")
    void save_test() {
        //  given
        MemberServiceDTO newMember = new MemberServiceDTO("1000", "jerry@mail.com", "jerry",
                "password", true, "USER", null, null);

        //  when & then
        repository.save(newMember).ifPresent(
                savedMember -> assertThat(savedMember.memberId()).isEqualTo("1000")
        );

    }

    @Test
    @DisplayName("findAll: 페이징 처리도 확인")
    void findAll_test() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<MemberServiceDTO> page = repository.findAll(pageable);

        assertThat(page.getNumberOfElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("findBy, memberId, username, email: 데이터 확인")
    void findBy_test() {
        repository.findByMemberId("99").ifPresent(
                member -> assertThat(member.username()).isEqualTo("alice")
        );

        repository.findByEmail("bob@mail.com").ifPresent(
                member -> assertThat(member.username()).isEqualTo("bob")
        );

        repository.findByUsername("john").ifPresent(
                member -> assertThat(member.email()).isEqualTo("john@mail.com")
        );

        repository.findByMemberId("1000").ifPresentOrElse(
                value -> fail("값이 없어야 한다."),
                ()->assertThat(true).isTrue()
        );

        repository.findByEmail("mail").ifPresentOrElse(
                value -> fail("값이 없어야 한다."),
                ()->assertThat(true).isTrue()
        );

        repository.findByUsername("jerry").ifPresentOrElse(
                value -> fail("값이 없어야 한다."),
                ()->assertThat(true).isTrue()
        );
    }

    @Test
    @DisplayName("existsByMemberIdAndUsername: member_id, username 데이터 확인 true/false")
    void existsByMemberIdAndUsername_test() {
        assertThat(repository.existsByMemberIdAndUsername("99", "alice")).isTrue();
        assertThat(repository.existsByMemberIdAndUsername("99", "bob")).isFalse();
        assertThat(repository.existsByMemberIdAndUsername("1", "")).isFalse();
    }
}