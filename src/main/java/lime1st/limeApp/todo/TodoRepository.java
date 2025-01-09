package lime1st.limeApp.todo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "todos")
public interface TodoRepository extends CrudRepository<TodoEntity, Long> {
}
