package lime1st.limeApp.blog.application;

import lime1st.limeApp.blog.domain.BlogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public interface BlogRepository {

    //  Spring Data JPA 의 Repository 메서드는 보통 void 나 Optional 을 반환한다.
    //  예를 들어, deleteById는 보통 void 로 선언되어 있다.
    //  만약 boolean 을 반환하도록 선언하면, Spring Data JPA 가 이를 올바르게 처리하지 못하고 null 을 반환할 수 있다.
    //  아래와 같이 선언하면 에러가 날 수 있다. 그러므로 삭제 성공여부를 반환하고 싶으면 @Repository 에서 직접 커스터마이징 해야 한다(했다).
    int deleteById(Long id);

    Optional<BlogEntity> save(BlogEntity newBlog);

    Optional<BlogEntity> findByIdAndAuthor(Long id, String author);

    Page<BlogEntity> findAllByAuthor(PageRequest pageRequest, String author);

    boolean existsByIdAndAuthor(Long id, String author);

}
