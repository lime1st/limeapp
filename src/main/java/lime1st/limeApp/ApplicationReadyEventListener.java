package lime1st.limeApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.util.Date;
/*
* ApplicationReadyEvent 는 앱의 실행 준비가 완료되면 호출되며 SpringApplication 의 addListener 메소드로 등록한다.
* */
public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(ApplicationReadyEventListener.class);

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        //  앱이 완전 준비가 끝난 후 호출 된다. 맨 마지막에 텍스트가 나온다.
        log.info("ApplicationReadyEventListener 준비 완료!{}", new Date(event.getTimestamp()));
    }
}
