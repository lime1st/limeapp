package lime1st.limeApp.blog.infrastructure;

import lime1st.limeApp.blog.application.BlogRepository;
import lime1st.limeApp.blog.domain.BlogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

/*
* 서비스 레이어에서 리포지토리 레이어를 의존하지 않도록 BlogRepository 를 구현하여 의존성 역전 원칙을 적용했다.
* 실제 구현 테스트 후 JpaRepository 로 대체했다.. 현재는 사용 안 함
* */
public class BlogRepositoryImpl implements BlogRepository {

    private final BlogJpaRepository repository;

    public BlogRepositoryImpl(BlogJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public int deleteById(Long id) {
        return repository.deleteById(id);
    }

    @Override
    public Optional<BlogEntity> save(BlogEntity newBlog) {
        return repository.save(newBlog);
    }

    @Override
    public Optional<BlogEntity> findByIdAndAuthor(Long id, String author) {
        return repository.findByIdAndAuthor(id, author);
    }

    @Override
    public Page<BlogEntity> findAllByAuthor(PageRequest pageRequest, String author) {
        return repository.findAllByAuthor(pageRequest, author);
    }

    @Override
    public boolean existsByIdAndAuthor(Long id, String author) {
        return repository.existsByIdAndAuthor(id, author);
    }
}
