package lime1st.limeApp.common.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lime1st.limeApp.common.security.auth.CustomPrincipal;
import lime1st.limeApp.jwt.application.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtCheckFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    //  JwtCheckFilter 가 동작하지 않아야 하는 경로(토큰 발행 관련)를 지정
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().startsWith("/api/v1/token/");
    }

    //  Access Token 검증 문제가 없는 경우 컨트롤러 혹은 다음 필터들이 동작하도록 구성
    //  Access Token 에 문제가 있는 경우 발생하는 Exception 처리는 반드시 필요함
    @Override
    protected void doFilterInternal (
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {


        String headerStr = request.getHeader("Authorization");
//        log.info("headerStr: {}", headerStr);

        //  Access Token 이 없거나 header prefix 가 Bearer 가 아닌 경우
        if (headerStr == null || !headerStr.startsWith("Bearer ")) {
            handleException(response, new Exception("ACCESS TOKEN NOT FOUND"));
            return;
        }

        //  토큰은 Bearer 를 제외한 문자열
        String accessToken = headerStr.substring(7);

        try {
            Map<String, Object> tokenMap = tokenProvider.validateToken(accessToken);

            log.info("tokenMap: {}", tokenMap);

            String memberId = tokenMap.get("memberId").toString();

            //  권한이 여러 개인 경우에는 ,로 구분해서 처리
            String[] roles = tokenMap.get("role").toString().split(",");

            //  토큰 검증 결과를 이용해서 Authentication 객체를 생성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            new CustomPrincipal(memberId),
                            null,   // 토큰 검증(tokenProvider.validateToken)으로 이미 검사가 완료되어 null 로 지정
                            Arrays.stream(roles)
                                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                    .collect(Collectors.toList())
                    );

            //  SecurityContextHolder 에 Authentication 객체를 저장
            //  이후에 SecurityContextHolder 를 이용해서 Authentication 객체를 꺼내서 사용할 수 있다.
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.info("토큰 검증에 문제가 발생");
            handleException(response, e);
        }
    }

    private void handleException (HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().println(e.getMessage());
    }
}
