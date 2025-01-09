package lime1st.limeApp.blog.infrastructure;

import lime1st.limeApp.blog.domain.BlogEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.fail;

/**
* 테스트 데이터 test/resources/data.sql
* */
@DataJpaTest
@ActiveProfiles("test")
class BlogRepositoryImplTest {

    private static final Logger log = LoggerFactory.getLogger(BlogRepositoryImplTest.class);

    @Autowired
//    private BlogRepositoryImpl repository;
    private BlogJpaRepository repository;

    @Test
    void test() {
    }

    @Test
    @DisplayName("deleteById: 데이터 삭제")
    void deleteById_test() {
        //  when
        repository.deleteById(11L);

        //  then: 값을 삭제 후 있는 지 확인
        assertThat(repository.existsByIdAndAuthor(11L, "alice")).isFalse();
    }

    @Test
    @DisplayName("save: id 에 값이 있으면 데이터 수정")
    void sava_test_for_update() {
        //  given
        BlogEntity findBlog = repository.findByIdAndAuthor(11L, "alice").get();


        //  when
        BlogEntity updateBlog = new BlogEntity(findBlog.getId(), "update", "update", "alice",
                findBlog.getCreatedAt(), null);
        repository.save(updateBlog);

        //  then
        repository.findByIdAndAuthor(11L, "alice").ifPresent(
                entity -> assertThat(entity.getTitle()).isNotEqualTo("title1")
        );
    }

    @Test
    @DisplayName("save: id가 null 이면 새로운 데이터 추가")
    void save_test() {
        //  given
        BlogEntity newBlog = new BlogEntity(null, "new", "new", "andy",
                null, null);

        //  when & then
        repository.save(newBlog).ifPresent(
                blog-> assertThat(blog.getTitle()).isEqualTo("new")
        );
    }

    @Test
    @DisplayName("findByAuthor: 작성자의 모든 글 찾기(페이징 처리)")
    void findAllByAuthor_test() {
        PageRequest pageRequest = PageRequest.of(0, 5,
                Sort.by(Sort.Direction.DESC, "title"));
        Page<BlogEntity> page = repository.findAllByAuthor(pageRequest, "alice");

        assertThat(page.getNumberOfElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("findByIdAndAuthor: id, 작성자로 찾기")
    void findByIdAndAuthor_test() {
        repository.findByIdAndAuthor(11L, "alice").ifPresent(
                blog -> assertThat(blog.getTitle()).isEqualTo("title1")
        );

        /*
        * Optional.get()은 값이 없을 경우 NoSuchElementException 을 던지므로, 테스트 코드에서는 사용을 피하고
        * isPresent 또는 isEmpty
        * Optional 자체가 null 이 아니며, 내부 값이 비어 있을 뿐
        * */
        // 없는 아이디
        repository.findByIdAndAuthor(1L, "alice")
                .ifPresentOrElse(
                        value -> fail("Expected no value, but found one"), // 값이 있으면 테스트 실패
                        () -> assertThat(true).isTrue() // 값이 없으면 테스트 통과
                );
    }

    @Test
    @DisplayName("existByIdAndAuthor: 아이디, 작성자로 데이터 확인 true/false")
    void existByIdAndAuthor_test() {
        assertThat(repository.existsByIdAndAuthor(15L, "bob")).isTrue();
        assertThat(repository.existsByIdAndAuthor(15L, "alice")).isFalse();
        assertThat(repository.existsByIdAndAuthor(1L, "")).isFalse();
    }
}