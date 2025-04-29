package com.jwt.demo.common.filter;

import com.jwt.demo.adapter.RedisTokenAdapter;
import com.jwt.demo.common.auth.TokenUser;
import com.jwt.demo.enums.ErrorCode;
import com.jwt.demo.common.auth.JwtTokenProvider;
import com.jwt.demo.common.exception.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisTokenAdapter redisTokenAdapter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String bearerToken = request.getHeader("Authorization");
            String token = jwtTokenProvider.getToken(bearerToken);

            if (token == null) {
                throw new JwtException(ErrorCode.JWT_UNAUTHORIZED);
            }

            boolean isReissueOrLogout = isReissueOrLogoutRequest(request);

            if (isReissueOrLogout) {
                // reissue 또는 logout 요청: 만료된 AccessToken 허용
                TokenUser tokenUser = jwtTokenProvider.getTokenPayload(token);

                if (tokenUser == null || tokenUser.getUserIdx() == null) {
                    throw new JwtException(ErrorCode.JWT_UNAUTHORIZED);
                }

                setAuthentication(tokenUser);

            } else {

                if (jwtTokenProvider.validateToken(token)) {

                    TokenUser tokenUser = jwtTokenProvider.getUserFrom(token);

                    // Redis access token 유효성 검증
                    if (!redisTokenAdapter.isAccessTokenValid(tokenUser.getUserIdx(), tokenUser.getJti())) {
                        throw new JwtException(ErrorCode.JWT_UNAUTHORIZED);
                    }

                    setAuthentication(tokenUser);
                }

            }

        } catch (Exception e) {
            log.warn("JwtAuthenticationFilter > doFilterInternal > JwtException : {} => {}", e.getMessage(), request.getRequestURI());
            request.setAttribute("exception",e);
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(TokenUser tokenUser) {
        Set<GrantedAuthority> roles = new HashSet<>();
        roles.add(new SimpleGrantedAuthority("ROLE_" + tokenUser.getRole()));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(tokenUser, null, roles);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private boolean isReissueOrLogoutRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/v1/auth/reissue") || path.startsWith("/v1/auth/logout");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String[] excludePath = {"/v1/hello/**", "/v1/auth/**"};
        String path = request.getRequestURI();

        return Arrays.asList(excludePath).contains(path);
    }
}
