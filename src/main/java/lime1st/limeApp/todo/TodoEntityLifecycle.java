package lime1st.limeApp.todo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/*
* SmartLifecycle 인터페이스를 구현해 등록한 컴포넌트(@Component)는 컴포넌트의 시작과 종료 시 콜백함수를 활용할 수 있다.
* */
@Component
@Profile("!test")
public class TodoEntityLifecycle implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(TodoEntityLifecycle.class);

    private final TodoRepository repository;

    private boolean isRunning = false;

    public TodoEntityLifecycle(TodoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void start() {
        log.info("TodoEntityLifecycle: SmartLifecycle Start!");
        isRunning = true;

        List<TodoEntity> list = new ArrayList<>();

        for (int i = 1; i < 101; i++) {
            list.add(new TodoEntity(null, "title " + i, Math.random() > 0.5,
                    Math.random() > 0.5 ? "alice" : "bob"));
        }

        // TodoRepository 에 기본 데이터 등록
//        repository.saveAll(list);
    }

    @Override
    public void stop() {
        // 앱이 종료되면 등록한 기본 데이터를 삭제하기 위해 여기서 삭제
        repository.deleteAll();
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }
}
