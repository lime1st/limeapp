package lime1st.limeApp.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class BlogControllerAOP {
/**
 * 사용하려면 Target 메서드가 private 설정인지 확인 할 것
 * */
    @Before("execution(* lime1st.limeApp.blog.presentation.BlogController.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        try {

            log.info("Before \n Target: {},\n method: {},\n args: {}",
                    joinPoint.getTarget().toString(),
                    joinPoint.getSignature().getName(),
                    joinPoint.getArgs());
        } catch (Exception e) {
            log.error("Error logging method execution", e);
        }
    }
}
