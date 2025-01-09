package lime1st.limeApp.common.config;

import lime1st.limeApp.common.security.filter.JwtCheckFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class FilterChainConfig {

    private final JwtCheckFilter jwtCheckFilter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.httpBasic(Customizer.withDefaults());

        http.formLogin(AbstractHttpConfigurer::disable);
        http.logout(AbstractHttpConfigurer::disable);

        //  CSRF 토큰은 기본적으로 세션 단위로 관리되는데 API 서버는 상태 유지를 하지 않기 때문에 사용하지 않음
        http.csrf(AbstractHttpConfigurer::disable);

        //  API 서버는 무상태ㅗㄹ 유지하도록 구성하고 세션을 생성하지 않도록 한다.
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.NEVER));

        //        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http.addFilterBefore(jwtCheckFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    //  스프링은 Bean 으로 등록된 필터를 자동으로 등록하므로
    //  JwtCheckFilter 가 Servlet Filter 에 자동 등록하지 않도록 지정
    @Bean
    public FilterRegistrationBean<JwtCheckFilter> registration(JwtCheckFilter filter) {
        FilterRegistrationBean<JwtCheckFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("*")); //  요청 경로(모든 경로)
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")); //  사용 가능 메서드
        config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type")); //  사용 가능 헤더
        config.setAllowCredentials(true);
//        config.applyPermitDefaultValues();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}