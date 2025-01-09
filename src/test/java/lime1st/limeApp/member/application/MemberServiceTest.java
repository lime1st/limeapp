package lime1st.limeApp.member.application;

import lime1st.limeApp.common.exception.DuplicationException;
import lime1st.limeApp.common.exception.NotFoundException;
import lime1st.limeApp.member.infrastructure.StubMemberRepositoryFILE;
import lime1st.limeApp.member.infrastructure.StubMemberRepositoryMEM;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberServiceTest {

    private static final Logger log = LoggerFactory.getLogger(MemberServiceTest.class);

    private final MemberService service;

    public MemberServiceTest() throws IOException {
        MemberRepository<MemberServiceDTO> repository = new StubMemberRepositoryMEM();
//        MemberRepository<MemberServiceDTO> repository = new StubMemberRepositoryFILE();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // 테스트에 사용할 리포지터리 선택
        this.service = new MemberService(repository, passwordEncoder);
    }

    @Test
    @DisplayName("delete: ")
    void delete_test() {
        //  when
        service.delete("fc7b9203-f569-46d2-8bf0-9e23f2a131bh", "john");

        //  then: 삭제 결과 확인
        assertThat(service.findById("fc7b9203-f569-46d2-8bf0-9e23f2a131bh")).isNull();
    }

    @Test
    @DisplayName("update: ")
    void update_test() {

    }

    @Test
    @DisplayName("join: id 생성, passwordEncode 확인")
    void join_test() {
        //  given
        MemberServiceDTO petty = new MemberServiceDTO(null, "petty@mail.com", "petty",
                "1234", false, null, null, null);
        //  when
        MemberServiceDTO savedMember = service.join(petty);

        //  then
        assertThat(savedMember.memberId()).isNotNull();
        assertThat(savedMember.password()).isNotEqualTo("1234");
    }

    @Test
    @DisplayName("join: email 은 중복 불가")
    void join_email_duplicate_test() {
        //  given
        MemberServiceDTO john = new MemberServiceDTO("", "john@mail.com", null, null,
                false, null, null, null);

        //  when
        DuplicationException exception = assertThrows(DuplicationException.class, () -> {
            service.join(john);
        });

        //  then
        assertThat(exception.getMessage()).isEqualTo("Duplicate");
    }

    @Test
    @DisplayName("findAll: ")
    void findAll_test() {
        //  when
        Pageable pageable = PageRequest.of(0, 1);
        List<MemberServiceDTO> list = service.findAll(pageable);

        //  then
        assertThat(list).hasSize(1);
    }

    @Test
    @DisplayName("findByEmail: 없으면 NotFoundException")
    void findByEmail_test() {
        //  when
        MemberServiceDTO alice = service.findByEmail("alice@mail.com");
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            service.findByEmail("mail.com");
        });

        //  then
        assertThat(alice.email()).isEqualTo("alice@mail.com");
        // AssertJ로 예외 메시지 확인
        assertThat(exception.getMessage()).isEqualTo("Not Found");
    }

    @Test
    @DisplayName("findById: 검색 시 username 이 다르거나 없으면 null")
    void findById_invalid_username_test() {
        //  when
        MemberServiceDTO alice = service.findById("fc7b9203-f569-46d2-8bf0-9e23f2a131bf");
        MemberServiceDTO noId = service.findById("noId");

        //  then
        assertThat(alice).isNull();
        assertThat(noId).isNull();
    }

    @Test
    @DisplayName("findById: id 로 검색 시 username 확인(로그인 이름)")
    void findById_test() {
        //  when
        MemberServiceDTO alice = service.findById("fc7b9203-f569-46d2-8bf0-9e23f2a131bf");

        //  then
        assertThat(alice.email()).isEqualTo("alice@mail.com");
    }
}