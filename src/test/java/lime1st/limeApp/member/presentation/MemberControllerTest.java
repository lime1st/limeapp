package lime1st.limeApp.member.presentation;

import lime1st.limeApp.member.application.MemberService;
import lime1st.limeApp.member.application.MemberServiceDTO;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    private static final String BASE_URL = "http://localhost/api/v1/members";

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private MemberService service;

    @Test
    @DisplayName("getAllMembers: ")
    @WithMockUser(username = "testUser")
    void getAllMembers_test() throws Exception {
        // given: Mock 데이터를 생성하고 서비스 호출 결과를 미리 정의
        var members = Arrays.array(new MemberServiceDTO("fc7b9203-f569-46d2-8bf0-9e23f2a131bf",
                        null, "testUser", null, false, null,
                        null, null),
                new MemberServiceDTO("fc7b9203-f569-46d2-8bf0-9e23f2a131bg",
                        null, "testUser", null, false, null,
                        null, null),
                new MemberServiceDTO("fc7b9203-f569-46d2-8bf0-9e23f2a131bh",
                        null, "testUser", null, false, null,
                        null, null));
        var pageable = PageRequest.of(0, 3);

        // Mock Service 가 findAll 호출 시, Page 를 반환하도록 설정(findAll() 메서드의 반환값)
        given(service.findAll(pageable)).willReturn(List.of(members));

        // when & then: MockMvc 로 컨트롤러를 호출하고 반환된 데이터 검증
        assertThat(this.mvc.get().uri(BASE_URL)
                .param("page", "0") // 서비스 메서드 호출하는 위의 부분과 값을 일치하지 않으면 에러가 난다.
                .param("size", "3")
                .contentType(MediaType.APPLICATION_JSON)
        ).hasStatusOk()
                .bodyJson()
                .extractingPath("$._embedded.memberResponseDTOes")
                .asArray()
                .hasSize(3);

//        mockMvc.perform(get(BASE_URL)
//                        .param("page", "0")
//                        .param("size", "1")
//                        .param("sort", "name")
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(1)) // 반환된 배열의 크기 확인
//                .andExpect(jsonPath("$[0].username").value("testUser"))
//                .andExpect(jsonPath("$[0].email").value("test@mail.com"));
    }

    @Test
    @DisplayName("getMemberById: Path Variable")
    @WithMockUser(username = "testUser")
    void getTestFindById() throws Exception {
        //  given
        var testUser = new MemberServiceDTO("fc7b9203-f569-46d2-8bf0-9e23f2a131bf",
                 "test@mail.com", "testUser","password", false,
                "USER", null, null);
        given(service.findById("fc7b9203-f569-46d2-8bf0-9e23f2a131bf"))
                .willReturn(testUser);

        //  when & then
        assertThat(this.mvc.get().uri(BASE_URL + "/fc7b9203-f569-46d2-8bf0-9e23f2a131bf")
                .accept(MediaType.APPLICATION_JSON)
        ).hasStatusOk()
                .bodyJson() // json 으로 변환
                .extractingPath("memberId")
                .isEqualTo("fc7b9203-f569-46d2-8bf0-9e23f2a131bf");

        // 줄바꿈이 들어가면 안 된다...
//                        .hasBodyTextEqualTo("""
//                                {"memberId":"fc7b9203-f569-46d2-8bf0-9e23f2a131bf","email":"test@mail.com","username":"testUser","password":"password","enabled":false,"role":"USER","_links":{"self":{"href":"http://localhost/api/v1/members/fc7b9203-f569-46d2-8bf0-9e23f2a131bf"},"members":{"href":"http://localhost/api/v1/members"},"delete":{"href":"http://localhost/api/v1/members/fc7b9203-f569-46d2-8bf0-9e23f2a131bf"},"update":{"href":"http://localhost/api/v1/members"}}}""");

        // 이전 mockMvc 사용방식
//        mockMvc.perform(get(BASE_URL + ))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.username").value("testUser"))
//                .andExpect(jsonPath("$.email").value("test@mail.com"));
    }

}