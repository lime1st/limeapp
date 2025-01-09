package lime1st.limeApp;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lime1st.limeApp.blog.domain.BlogEntity;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 기본 데이터는 test/resources/data.sql 에 있음
 * */
@SpringBootTest(classes = LimeAppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
//@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class BlogIntegrationTests {

    private static final Logger log = LoggerFactory.getLogger(BlogIntegrationTests.class);

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @DisplayName("deleteBlog: 작성자가 아니면 삭제 안 됨 204")
    void deleteBlog_test_fail_by_not_author() {
        ResponseEntity<Void> deleteResponse = restTemplate
                .withBasicAuth("alice", "password")
                .exchange("/api/v1/blogs/14", HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        // 삭제 안 됨 확인
        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("bob", "password")
                .getForEntity("/api/v1/blogs/14", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("deleteBlog: 블로그가 없으면 404")
    void deleteBlog_test_not_exist() {
        ResponseEntity<Void> deleteResponse = restTemplate
                .withBasicAuth("alice", "password")
                .exchange("/api/v1/blogs/99999", HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("deleteBlog: 블로그가 있으면 삭제 204")
    @DirtiesContext
    void deleteBlog_test() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("alice", "password")
                .exchange("/api/v1/blogs/11", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("alice", "password")
                .getForEntity("/api/v1/blogs/11", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("putBlog: 작성자가 아니면 찾을 수 없어서 수정 안 됨 404")
    void pubBlog_test_fail_by_not_author() {
        BlogEntity bobsBlogEntity = new BlogEntity(null, "put put", "put content", null);
        HttpEntity<BlogEntity> request = new HttpEntity<>(bobsBlogEntity);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("alice", "password")
                .exchange("/api/v1/blogs/14", HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("pubBlog: 블로그가 없으면 404")
    void pubBlog_test_not_exist() {
        BlogEntity unknownBlogEntity = new BlogEntity(null, "unknown", "no content", "alice");
        HttpEntity<BlogEntity> request = new HttpEntity<>(unknownBlogEntity);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("alice", "password")
                .exchange("/api/v1/blogs/99999", HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("pubBlog: 블로그가 있으면 업데이트 하고 204")
    @DirtiesContext
    void pubBlog_test() {
        BlogEntity blogEntityUpdate = new BlogEntity(null, "test put", "test content put", "alice");
        HttpEntity<BlogEntity> request = new HttpEntity<>(blogEntityUpdate);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("alice", "password")
                .exchange("/api/v1/blogs/11", HttpMethod.PUT, request, Void.class);  //    not putForEntity()
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // 업데이트 내용 확인
        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("alice", "password")
                .getForEntity("/api/v1/blogs/11", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        String title = documentContext.read("$.title");
        assertThat(id).isEqualTo(11);
        assertThat(title).isEqualTo("test put");
//        log.info("createdAt: right here!!! {}", documentContext.read("$.createdAt").toString());
//        log.info("updatedAt: right here!!! {}", documentContext.read("$.updatedAt").toString());
    }

    @Test
    @DisplayName("getBlogById: 작성자가 아닌 글 요청 시 404")
    void getBlogById_test_not_author() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("alice", "password")
                .getForEntity("/api/v1/blogs/14", String.class); // bob 's article
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("getBlogById: 인증은 되었지만 인가가 안 된(권한이 없는) 요청 시 403")
    void getBlogById_test_not_authorize() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("tester", "password")
                .getForEntity("/api/v1/blogs/11", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("getBlogById: 잘못된 사용자의 요청 시 401")
    void getBlogById_test_bad_credentials() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("BAD-USER", "password")
                .getForEntity("/api/v1/blogs/15", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        response = restTemplate
                .withBasicAuth("alice", "BAD-PASSWORD")
                .getForEntity("/api/v1/blogs/15", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("getAllBlog: 사용자와 작성자가 같은 글 정렬된 Page 로 반환")
    void getAllBlog_test_page_of_sort() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("alice", "password")
                .getForEntity("/api/v1/blogs?page=0&size=1&sort=author,desc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray read = documentContext.read("$._embedded.blogDTOes");
        assertThat(read.size()).isEqualTo(1);

        String author = documentContext.read("$._embedded.blogDTOes[0].author");
        assertThat(author).isEqualTo("alice");
    }

    @Test
    @DisplayName("getAllBlog: 사용자와 작성자가 같은 글 Page 로 반환")
    void getAllBlog_test_page() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("alice", "password")
                .getForEntity("/api/v1/blogs?page=0&size=1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$._embedded.blogDTOes");
        assertThat(page.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("findAllBlog: 사용자와 작성자가 같은 모든 글 반환")
    void getAllBlog_test() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("alice", "password")
                .getForEntity("/api/v1/blogs", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int cashCardCount = documentContext.read("$._embedded.blogDTOes.length()");
        assertThat(cashCardCount).isEqualTo(3);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(11, 12, 13);

        JSONArray authors = documentContext.read("$..author");
        assertThat(authors).containsExactlyInAnyOrder("alice", "alice", "alice");
    }

    @Test
    @DisplayName("createBlog: 새로운 데이터 생성 후 201, id를 포함한 요청 위치 반환")
    @DirtiesContext
    void createBlog_test() {
        BlogEntity newBlogEntity = new BlogEntity(null, "test title", "test content", null);
        ResponseEntity<Void> createResponse = restTemplate
                .withBasicAuth("alice", "password")
                .postForEntity("/api/v1/blogs", newBlogEntity, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewBlog = createResponse.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("alice", "password")
                .getForEntity(locationOfNewBlog, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("getBlogById: 없는 id 요청 시 404, 에러 메시지")
    void getBlogById_test_not_exist() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("alice", "password")
                .getForEntity("/api/v1/blogs/1000", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Not Found");
    }

    @Test
    @DisplayName("getBlogById: 저장되어 있는 데이터가 있으면 블로그 반환")
    void getBlogById_test() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("alice", "password")
                .getForEntity("/api/v1/blogs/13", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(13);

        String title = documentContext.read("$.title");
        assertThat(title).isEqualTo("title3");
    }
}
