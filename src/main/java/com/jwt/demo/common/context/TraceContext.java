package com.jwt.demo.common.context;

public class TraceContext {

    private static final ThreadLocal<String> traceIdHolder = new ThreadLocal<>();

    public static void setTraceId(String traceId) {
        traceIdHolder.set(traceId);
    }

    public static String getTraceId() {
        return traceIdHolder.get();
    }

    public static void clear() {
        traceIdHolder.remove();
    }
}
