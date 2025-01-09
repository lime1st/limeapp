package lime1st.limeApp.todo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.util.Objects;

/**
 * spring-data-rest 활용
 * 에러 처리를 위한 Controller 가 필요하다
* */

@Entity
@Table(name = "la_todo")
public class TodoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long todoId;

    @NotEmpty
    private String title;

    private boolean done;

    @NotEmpty
    private String userId;

    public TodoEntity() {
    }

    public TodoEntity(Long todoId, String title, boolean done, String userId) {
        this.todoId = todoId;
        this.title = title;
        this.done = done;
        this.userId = userId;
    }

    public Long getTodoId() {
        return todoId;
    }

    public String getTitle() {
        return title;
    }

    public boolean isDone() {
        return done;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TodoEntity todo = (TodoEntity) o;
        return Objects.equals(getTodoId(), todo.getTodoId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getTodoId());
    }

    @Override
    public String toString() {
        return "Todo{" +
                "todoId=" + todoId +
                ", title='" + title + '\'' +
                ", done=" + done +
                ", userId='" + userId + '\'' +
                '}';
    }
}
