package lime1st.limeApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

import java.util.Date;

/*
 * ApplicationStartingEvent 는 앱의 실행 시 바로 호출되며 SpringApplication 의 addListener 메소드로 등록한다.
 * */
public class ApplicationStartingEventListener implements ApplicationListener<ApplicationStartingEvent> {

    private static final Logger log = LoggerFactory.getLogger(ApplicationStartingEventListener.class);

    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {
        //  스프링부트 로고 나오기 전에 나온다. 말 그대로 시작 이벤트를 구독
        System.out.println("ApplicationStartingEventListener!! 로그메시지 출력 전, start time: " + new Date(event.getTimestamp()));

        //  log 는 불러오기 전이라 그런가 표시가 안 된다...
        log.info("스타팅 이벤트: {}", new Date(event.getTimestamp()));
    }
}
