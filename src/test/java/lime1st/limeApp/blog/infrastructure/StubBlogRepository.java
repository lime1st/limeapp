package lime1st.limeApp.blog.infrastructure;

import lime1st.limeApp.blog.application.BlogRepository;
import lime1st.limeApp.blog.domain.BlogEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.*;

public class StubBlogRepository implements BlogRepository{

    private static final Logger log = LoggerFactory.getLogger(StubBlogRepository.class);

    Map<Long, BlogEntity> blogMap = new HashMap<>();

    public StubBlogRepository() {
        // test data
        blogMap.put(1L, new BlogEntity(1L, "title 1", "content 1", "author1",
                LocalDateTime.of(2020, 12, 12, 11, 11, 11),
                LocalDateTime.of(2020, 12, 12, 11, 11, 11)));
        blogMap.put(2L, new BlogEntity(2L, "title 2", "content 2", "author2",
                LocalDateTime.of(2020, 11, 12, 11, 11, 11),
                LocalDateTime.of(2020, 11, 12, 11, 11, 11)));
        blogMap.put(3L, new BlogEntity(3L, "title 3", "content 3", "author3",
                LocalDateTime.of(2020, 10, 12, 11, 11, 11),
                LocalDateTime.of(2020, 10, 12, 11, 11, 11)));
        blogMap.put(4L, new BlogEntity(4L, "title 4", "content 4", "author4",
                LocalDateTime.of(2020, 8, 12, 11, 11, 11),
                LocalDateTime.of(2020, 8, 12, 11, 11, 11)));
        blogMap.put(5L, new BlogEntity(5L, "title 5", "content 5", "author5",
                LocalDateTime.of(2020, 9, 12, 11, 11, 11),
                LocalDateTime.of(2020, 9, 12, 11, 11, 11)));
    }

    @Override
    public int deleteById(Long id) {
        return blogMap.remove(id) != null ? 1 : 0;
    }

    @Override
    public Optional<BlogEntity> save(BlogEntity blog) {
        if (blog.getTitle().isEmpty()) {
            Optional<BlogEntity> op = Optional.empty();
            return op;
        }
        if (blog.getId() != null) {
            // id 가 있으면 수정이므로 바로 저장(키가 이미 존재하면 밸류를 리턴)
            return Optional.ofNullable(blogMap.put(blog.getId(), blog));
        } else {
            // id 가 없으면 새 데이터 이므로 id 생성 후 저장
            BlogEntity newBlog = blog.withId((long) blogMap.keySet().size() + 1);
            // HashMap 은 새로운 키가 저장되면 null 을 리턴하므로 저장 후 다시 get
            blogMap.put(newBlog.getId(), newBlog);
            return Optional.ofNullable(blogMap.get(newBlog.getId()));
        }
    }

    @Override
    public Optional<BlogEntity> findByIdAndAuthor(Long id, String author) {
        return Optional.ofNullable(blogMap.get(id));
    }

    @Override
    public Page<BlogEntity> findAllByAuthor(PageRequest pageRequest, String author) {
        //  Map -> List
        List<BlogEntity> blogList = new ArrayList<>(blogMap.values().stream()
                .filter(blog->blog.getAuthor().equals(author))
                .toList());

        //  정렬 처리를 위한 Comparator 구현
        if (pageRequest.getSort().isSorted()) {
            Comparator<BlogEntity> comparator = pageRequest.getSort().stream()
                    .map(sort -> {
                        Comparator<BlogEntity> singleComparator;
                        if (sort.getProperty().equalsIgnoreCase("title")) {
                            singleComparator = Comparator.comparing(BlogEntity::getTitle, Comparator.nullsLast(String::compareTo));
                        } else if (sort.getProperty().equalsIgnoreCase("createdAt")) {
                            singleComparator = Comparator.comparing(BlogEntity::getCreatedAt, Comparator.nullsLast(LocalDateTime::compareTo));
                        } else {
                            throw new IllegalArgumentException("Unsupported sort property: " + sort.getProperty());
                        }
                        return sort.isAscending() ? singleComparator : singleComparator.reversed();
                    })
                    .reduce(Comparator::thenComparing)
                    .orElse(Comparator.comparing(BlogEntity::getId)); // 기본 정렬 기준
            blogList = blogList.stream().sorted(comparator).toList();
        }

        // 페이징 처리
        int start = (int) pageRequest.getOffset();
        int end = Math.min(start + pageRequest.getPageSize(), blogList.size());
        List<BlogEntity> pageContent = (start > blogList.size()) ? Collections.emptyList() :
                blogList.subList(start, end);

        return new PageImpl<>(pageContent, pageRequest, blogList.size());
    }

    @Override
    public boolean existsByIdAndAuthor(Long id, String author) {
        return blogMap.get(id).getAuthor().equals(author);
    }
}
