package lime1st.limeApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * CommandLineRunner 를 사용하여 애플리케이션 초기화를 위해 여러 작업을 수행해야 할 때 사용할 수 있다.
 * 메인 클래스에서 직접 구현해도 되지만 구분하는게 사용하기 편하다.
 * 빈으로 등록해야 한다. 여기서는 @Component 를 이용해 빈으로 등록했다.
 * 구현체가 여러개 필요할 경우 @Order 애너테이션으로 순서를 지정할 수 있다.
 * 콘솔에서 언제 출력되는지 확인해 보자, 현재는 ReadyEvent 전에 출력됨
 * ApplicationRunner 도 있다.
 */
@Component
@Order(1)
public class CustomCommandLineRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CustomCommandLineRunner.class);

    @Override
    public void run(String... args) throws Exception {
        log.info("CommandLineRunner!! 1번 {}", LocalDateTime.now());

    }
}
