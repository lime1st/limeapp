package lime1st.limeApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.util.Date;
/*
 * ContextClosedEvent 는 앱의 종료 시 호출되며 SpringApplication 의 addListener 메소드로 등록한다.
 * */
public class ApplicationShutdownListener implements ApplicationListener<ContextClosedEvent> {

    private static final Logger log = LoggerFactory.getLogger(ApplicationShutdownListener.class);

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        //  앱 종료
        log.info("ApplicationShutdownListener 종료!{}", new Date(event.getTimestamp()));
    }
}
