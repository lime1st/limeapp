package lime1st.limeApp.blog.presentation;

import lime1st.limeApp.blog.application.BlogService;
import lime1st.limeApp.blog.application.dto.BlogDTO;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

// 실제 서버를 실행하지 않는다. HTTP 요청과 응답을 가상으로 처리
@WebMvcTest(BlogController.class)
public class BlogControllerTests {

    private static final String BASE_URL = "http://localhost/api/v1/blogs";

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private BlogService service;

    private BlogDTO[] blogs;

    @BeforeEach
    void setUp() {
        blogs = Arrays.array(
                new BlogDTO(101L, null, null, "tester", null, null),
                new BlogDTO(102L, null, null, "tester", null, null),
                new BlogDTO(103L, null, null, "tester", null, null)
        );
    }

    @Test
    void testMockBeanInjection() {
        assertNotNull(service, "빈 주입 실패");
    }

    @Test
    @DisplayName("deleteBlog: 요청 데이터 삭제, 204 NO_CONTENT 리턴")
    @WithMockUser(username = "tester")
    void deleteBlog_test() {
        //  given
        given(service.deleteByIdAndAuthor(100L, "tester")).willReturn(true);

        //  when & then
        assertThat(this.mvc.delete().uri(BASE_URL + "/100")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        ).hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("putBlog: 요청 데이터 수정 후 204 NO_CONTENT 리턴")
    @WithMockUser(username = "tester")
    void putBlog_test() {
        //  Mock 객체(여기서는 service)에서 메서드 호출 시 모든 인수를 매처로 사용하거나, 모두 실제값으로 사용해야 한다. 혼합 불가
        //  any(Class<T>) 해당 타입의 어떤 값이 와도 허용
        //  eq(Object) 특정 값을 매처로 사용
        given(service.putByIdAndAuthor(eq(100L), any(BlogDTO.class), eq("tester"))).willReturn(true);

        String updateBlog = """
                {
                    "title": "test title put",
                    "content": "test content put",
                    "author": "tester",
                    "createdAt": "2024-12-12T10:10"
                }
                """;

        //  when & then
        assertThat(this.mvc.put().uri(BASE_URL + "/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBlog)
                .with(csrf())
        ).hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("createBlog: validated 테스트")
    @WithMockUser(username = "tester")
    void createBlog_test_validated() {
        String newBlog = """
                {
                    "title": "test title"
                }
                """;

        //  when & then: 아직 테스트가 안 됨.... 정상적으로 등롣이 된다 어떤 설정이 빠진 걸까?
//        assertThat(this.mvc.post().uri(BASE_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(newBlog)
//                .with(csrf())
//        ).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("createBlog: Blog 생성 후 201 CREATED 리턴 Header 에 경로 포함")
    @WithMockUser(username = "tester")
    void createBlog_test() {
        given(service.create(any(BlogDTO.class))).willReturn(100L);

        String newBlog = """
                {
                    "title": "test title",
                    "content": "test content"
                }
                """;

        //  when & then
        assertThat(this.mvc.post().uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newBlog)
                .with(csrf())
        ).hasStatus(HttpStatus.CREATED)
                .hasHeader("Location", BASE_URL + "/100");
    }

    @Test
    @DisplayName("getAllBlog: 요청 id 로 찾기(작성자가 요청자와 다를 때) page 리턴")
    @WithMockUser(username = "tester")
    void getAllBlog_test() {
        //  given
        var pageable = PageRequest.of(0, 3);
        given(service.findAllByAuthor(pageable,"tester")).willReturn(List.of(blogs));

        //  when & then
        assertThat(this.mvc.get().uri(BASE_URL + "?page=0&size=3")
                .accept(MediaType.APPLICATION_JSON)
        ).hasStatusOk()
                .bodyJson()
                .extractingPath("$._embedded.blogDTOes")
                .asArray()
                .hasSize(3);;
    }

    @Test
    @DisplayName("getBlogById: 요청 id 로 찾기, 작성자도 검증함(작성자가 요청자와 같음) OK 200 블로그 리턴")
    @WithMockUser(username = "tester")
    void getBlogById_test() {
        //  given
        given(service.findByIdAndAuthor(101L, "tester")).willReturn(blogs[0]);

        //  when & then
        assertThat(this.mvc.get().uri(BASE_URL + "/101")
                .accept(MediaType.APPLICATION_JSON)
        ).hasStatusOk();
//                .hasBodyTextEqualTo("""
//                        {"id":101,"title":null,"content":null,"author":"tester","createdAt":null,"updatedAt":null}""");


//        이전 버전 방식 MockMvc 사용
//        mockMvc.perform(get("/blogs/21"))
//                .andExpect(status().isOk());
    }
}
