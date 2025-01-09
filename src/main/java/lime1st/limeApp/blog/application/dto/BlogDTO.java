package lime1st.limeApp.blog.application.dto;

import lime1st.limeApp.blog.domain.BlogEntity;

import java.time.LocalDateTime;

public record BlogDTO(
        Long id,
        String title,
        String content,
        String author,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public BlogDTO withId(Long id) {
        return new BlogDTO(
                id,
                this.title,
                this.content,
                this.author,
                this.createdAt,
                this.updatedAt
        );
    }

    public BlogEntity toEntity() {
        return new BlogEntity(
                this.id,
                this.title,
                this.content,
                this.author,
                this.createdAt,
                this.updatedAt
        );
    }
}
