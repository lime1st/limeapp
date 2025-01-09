package lime1st.limeApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.web.FilterChainProxy;

@SpringBootApplication
public class LimeAppApplication {

	private static final Logger log = LoggerFactory.getLogger(LimeAppApplication.class);

	public static void main(String[] args) {

		SpringApplication springApplication = new SpringApplication(LimeAppApplication.class);

		//	이벤트 리스너 등록
		springApplication.addListeners(
				new ApplicationStartingEventListener(),
				new ApplicationReadyEventListener(),
				new ApplicationShutdownListener()
		);

		springApplication.run(args);
		log.info("Lime App Start!!");
	}

//	@Bean
	public ApplicationRunner printFilterChains(FilterChainProxy filterChainProxy) {
		return args -> {
			filterChainProxy.getFilterChains().forEach(chain ->{
				log.info("Filter chain: {}", chain);
				chain.getFilters().forEach(filter -> {
					log.info("Filter in chain: {}", filter.getClass().getName());
				});
			});
		};
	}
}
