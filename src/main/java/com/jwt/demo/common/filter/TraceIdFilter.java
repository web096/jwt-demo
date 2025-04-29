package com.jwt.demo.common.filter;


import com.jwt.demo.common.context.TraceContext;
import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class TraceIdFilter implements Filter {

    private static final String TRACE_ID_KEY = "traceId";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            // UUID로 traceId 생성
            String traceId = UUID.randomUUID().toString();
            // TraceContext에 저장
            TraceContext.setTraceId(traceId);
            MDC.put(TRACE_ID_KEY, traceId); // MDC에도 저장

            // 다음 필터나 서블릿으로 요청 전달
            filterChain.doFilter(servletRequest, servletResponse);

        } finally {
            // 요청이 끝나면 ThreadLocal 제거
            TraceContext.clear();
            MDC.remove(TRACE_ID_KEY); // MDC 정리
        }
    }
}
