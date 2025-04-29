package com.jwt.demo.common.config;

import com.jwt.demo.adapter.RedisTokenAdapter;
import com.jwt.demo.common.auth.JwtAuthenticationEntryPoint;
import com.jwt.demo.common.auth.JwtTokenProvider;
import com.jwt.demo.common.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisTokenAdapter redisTokenAdapter;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private static final String[] PERMIT_URL_ARRAY = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/v1/hello/**",
            "/v1/auth/login"
    };

    private static final String[] AUTH_URL_ARRAY = {
            "/v1/auth/reissue",
            "/v1/auth/logout"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 안 함(RESTful API와 같이 상태를 유지할 필요가 없는 경우에는 세션을 사용하지 않고 각 요청마다 토큰을 검증하여 인증을 처리하는것이 좋다)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(PERMIT_URL_ARRAY).permitAll() //공개 엔드포인트(모든사람에게 허용)
                                .requestMatchers(AUTH_URL_ARRAY).authenticated() // 인증이 필요한 엔드포인트
                                .anyRequest().authenticated() //그 외 모든 엔드포인트는 인증필요
                )
                .exceptionHandling(handler -> handler.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisTokenAdapter), UsernamePasswordAuthenticationFilter.class); // JWT 인증 필터 추가

        return http.build();
    }
}
