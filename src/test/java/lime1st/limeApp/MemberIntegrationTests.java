package lime1st.limeApp;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import lime1st.limeApp.member.presentation.dto.MemberCreateDTO;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

// TestRestTemplate(실제 애플리케이션 서버를 싱행하여, HTTP 요청을 실제로 보내고 받으므로) 은 전체 애플리케이션을 실행해야 한다.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MemberIntegrationTests {

    private static final Logger log = LoggerFactory.getLogger(MemberIntegrationTests.class);

    @Autowired
    TestRestTemplate restTemplate;

    //  TODO: members 자신의 데이터만 조회할 수 있음
//    @Test
//    @DisplayName("자신의 데이터만 조회")
//    void getTestEmail() {
//        ResponseEntity<String> response = restTemplate
//                .withBasicAuth("alice", "password")
//                .getForEntity("/api/v1/members/100", String.class); //  100: bob's id
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }

    @Test
    @DisplayName("getMember: Role 기반 접근 제어 USER 만 접근")
    void getMember_reject_role_test() {
        //  현재 SecurityConfig 의 테스트 유저정보는 alice/password/USER, bob/password/GUEST
        //  when: Guest 접근
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("tester", "password")
                .getForEntity("/api/v1/members/99", String.class);
        //  then: FORBIDDEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        //  when: USER 접근
        response = restTemplate
                .withBasicAuth("alice", "password")
                .getForEntity("/api/v1/members/99", String.class);
        //  then: OK
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("getMember: 잘못된 인증 정보 제공 시 401 Unauthorized")
    void getMember_bad_credentials_test() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("bob", "1234")
                .getForEntity("/api/v1/members/99", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        response = restTemplate
                .withBasicAuth("alice", "1234")
                .getForEntity("/api/v1/members/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("getAllMembers: no parameters and default values")
    void getAllMembers_no_parameter_test() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("alice", "password")
                .getForEntity("/api/v1/members", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int memberCount = documentContext.read("$..memberResponseDTOes.length()");
        assertThat(memberCount).isEqualTo(3);
    }

    @Test
    @DisplayName("getAllMembers: page&size&sort 페이징 테스트")
    void getAllMembers_page_size_sort_test() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("alice", "password")
                .getForEntity("/api/v1/members?page=0&size=1&sort=username,desc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        log.info("response: {}", response.getBody());

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int memberCount = documentContext.read("$..memberResponseDTOes.length()");
        assertThat(memberCount).isEqualTo(1);
    }

    @Test
    @DisplayName("post 테스트")
    @DirtiesContext
    void postTest() {
        //	given: name 이 같아야 조회할 수 있다.
        //  리포지토리는 @Entity 여야 한다. Service 에서 DTO 를 사용하고 있다...
        MemberCreateDTO newMember = new MemberCreateDTO("amy@mail.com", "alice", "1234");
        ResponseEntity<Void> createResponse = restTemplate
                .withBasicAuth("alice", "password")
                .postForEntity("/api/v1/members", newMember, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED); // 201

        // CREATED 는 Location Header 를 반환해야 한다.
        URI locationOfNewMember = createResponse.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("alice", "password")
                .getForEntity(locationOfNewMember, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 생성한 값 검증
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        String memberId = documentContext.read("$.memberId");
        String name = documentContext.read("$.username");

        log.info("post: {}", getResponse.getBody());

        assertThat(memberId).isNotNull();
        assertThat(name).isEqualTo("alice");

    }

    @Test
    @DisplayName("getMember: pathVariable 없는 데이터 not_found")
    void getMember_pathVariable_test() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("alice", "password")
                .getForEntity("/api/v1/members/1000", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    @Test
    @DisplayName("getAllMembers: 모든 멤버 불러오기")
    void getAllMembers_test() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("alice", "password")
                .getForEntity("/api/v1/members", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 결과 값 검증
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int memberCount = documentContext.read("$..memberResponseDTOes.length()");
        assertThat(memberCount).isEqualTo(3);

        //  net.minidev.json.JSONArray; 다른 JSONArray 는 참조타입 List 로 변환해야 containsExactlyInAnyOrder 를 사용 가능
        JSONArray ids = documentContext.read("$..memberId");
        assertThat(ids).containsExactlyInAnyOrder("99", "100", "101");

        JSONArray names = documentContext.read("$..username");
        assertThat(names).containsExactlyInAnyOrder("alice", "bob", "john");
    }

    @Test
    @DisplayName("getMember: responseDTO")
    void getMember_test() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("alice", "password")
                .getForEntity("/api/v1/members/99", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        String id = documentContext.read("$.memberId");
        assertThat(id).isEqualTo("99");
        String  email = documentContext.read("$.email");
        assertThat(email).isEqualTo("alice@mail.com");
        String name = documentContext.read("$.username");
        assertThat(name).isEqualTo("alice");
        String password = documentContext.read("$.password");
        assertThat(password).isEqualTo("password");
        String createdAt = documentContext.read("$.createdAt");
        assertThat(createdAt).isNotNull();
    }

}