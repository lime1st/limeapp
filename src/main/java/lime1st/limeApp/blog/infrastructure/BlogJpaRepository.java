package lime1st.limeApp.blog.infrastructure;

import lime1st.limeApp.blog.application.BlogRepository;
import lime1st.limeApp.blog.domain.BlogEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

interface BlogJpaRepository extends Repository<BlogEntity, Long>, BlogRepository {

    @Override
    @Modifying
    @Query("delete from BlogEntity be where be.id = :id")
    int deleteById(@Param("id") Long id);
}
