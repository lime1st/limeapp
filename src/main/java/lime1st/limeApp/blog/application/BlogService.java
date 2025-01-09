package lime1st.limeApp.blog.application;

import lime1st.limeApp.blog.application.dto.BlogDTO;
import lime1st.limeApp.blog.domain.BlogEntity;
import lime1st.limeApp.common.exception.DataSaveException;
import lime1st.limeApp.common.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BlogService {

    private static final Logger log = LoggerFactory.getLogger(BlogService.class);

    private final BlogRepository repository;

    public BlogService(BlogRepository repository) {
        this.repository = repository;
    }

    public boolean deleteByIdAndAuthor(Long requestedId, String author) {
        if (repository.existsByIdAndAuthor(requestedId, author)){
            // TODO: 실패에 대한 처리는?
            return repository.deleteById(requestedId) > 0;
        }
        return false;
    }

    public boolean putByIdAndAuthor(Long requestId, BlogDTO putBlog, String author) {
        if (repository.existsByIdAndAuthor(requestId, author)) {
            var updateBlog = putBlog.withId(requestId).toEntity();
            return repository.save(updateBlog).isPresent();
        } else {
            return false;
        }
    }

    public Long create(BlogDTO newBlog) {
        return repository.save(newBlog.toEntity())
                .map(BlogEntity::getId)
                .orElseThrow(() -> new DataSaveException("create"));
    }

    public List<BlogDTO> findAllByAuthor(Pageable pageable, String author) {
        Page<BlogDTO> page = repository.findAllByAuthor(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))
                ), author).map(BlogEntity::toDTO);
        return page.getContent();
    }

    public BlogDTO findByIdAndAuthor(Long requestId, String author) {
        return repository.findByIdAndAuthor(requestId, author)
                .orElseThrow(NotFoundException::new).toDTO();
    }
}
