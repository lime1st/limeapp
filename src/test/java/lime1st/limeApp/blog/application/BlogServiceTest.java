package lime1st.limeApp.blog.application;

import lime1st.limeApp.blog.application.dto.BlogDTO;
import lime1st.limeApp.blog.infrastructure.StubBlogRepository;
import lime1st.limeApp.common.exception.DataSaveException;
import lime1st.limeApp.common.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BlogServiceTest {

    private static final Logger log = LoggerFactory.getLogger(BlogServiceTest.class);
    private BlogService service;

    @BeforeEach
    void setUp() {
        BlogRepository repository = new StubBlogRepository();
        service = new BlogService(repository);
    }

    @Test
    @DisplayName("deleteBlog: 요청 id로 사용자와 작성자가 같은 데이터 삭제")
    void deleteBlog_ByIdAndAuthor_test() {
        //  given stub repository 에 기본 데이터가 있음
        //  when
        service.deleteByIdAndAuthor(1L, "author1");

        //  then 아래의 findByIdTest_not_exist 와 다른 방법으로 검증
        assertThatThrownBy(() -> service.findByIdAndAuthor(1L, "author1"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Not Found");
    }

    @Test
    @DisplayName("findAllByAuthor: 사용자와 작성자가 같은 모든 데이터 검색")
    void findAllByAuthor_test() {
        //  given
        BlogDTO newBlog = new BlogDTO(null, "title 2", "content", "author1", null, null);
        service.create(newBlog);
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "title"));
        //  when
        List<BlogDTO> listAuthor1 = service.findAllByAuthor(pageable,"author1");
        List<BlogDTO> listAuthor5 = service.findAllByAuthor(pageable,"author5");
        //  then
        assertThat(listAuthor1).hasSize(2);
        assertThat(listAuthor1.stream().findFirst().get().title()).isEqualTo("title 2");
        assertThat(listAuthor5).hasSize(1);
    }

    @Test
    @DisplayName("findById: 존재 하지 않는 id 검색 시 exception")
    void findByIdAndAuthor_test_not_exist() {
        // when & then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            service.findByIdAndAuthor(10L, "author1");
        });
        // AssertJ로 예외 메시지 확인
        assertThat(exception.getMessage()).isEqualTo("Not Found");
    }

    @Test
    @DisplayName("create: 실패 Exception")
    void create_fail_test() {
        //  given
        BlogDTO newBlog = new BlogDTO(null, "", "content", "author1",
                null, null);

        //  when & then
        DataSaveException exception = assertThrows(DataSaveException.class, () -> {
            service.create(newBlog);
        });
        // AssertJ로 예외 메시지 확인
        assertThat(exception.getMessage()).isEqualTo("Data create Error");

    }

    @Test
    @DisplayName("create: 생성 후 Id 리턴")
    void create_test() {
        //  given
        BlogDTO newBlog = new BlogDTO(null, "title", "content", "tester",
                null, null);

        //  when
        Long id = service.create(newBlog);
        BlogDTO savedBlog = newBlog.withId(id);

        //  then: stubRepository 는 JpaAudit 기능이 작동하지 않으므로 저장된 데이터도 createdAt, updatedAt 값이 null 이다.
        assertThat(service.findByIdAndAuthor(id, "tester")).isEqualTo(savedBlog);
    }

    @Test
    @DisplayName("findBlogAndAuthor: id와 작성자로 찾기")
    void findBlogByIdAndAuthor_test() {
        //  when
        BlogDTO findBlog = service.findByIdAndAuthor(5L, "author5");

        //  then
        assertThat(findBlog.id()).isEqualTo(5);
    }

}