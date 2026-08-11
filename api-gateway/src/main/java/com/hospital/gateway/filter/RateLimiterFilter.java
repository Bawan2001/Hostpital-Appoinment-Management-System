package com.hospital.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimiterFilter implements GlobalFilter, Ordered {

    private static final int MAX_REQUESTS_PER_MINUTE = 100;
    private static final long ONE_MINUTE_IN_MILLIS = 60_000L;

    private final Map<String, UserRateState> rateMap = new ConcurrentHashMap<>();

    private static class UserRateState {
        long windowStartTime;
        AtomicInteger requestCount;

        UserRateState(long windowStartTime) {
            this.windowStartTime = windowStartTime;
            this.requestCount = new AtomicInteger(1);
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String clientIp = getClientIp(request);

        long currentTime = System.currentTimeMillis();

        UserRateState rateState = rateMap.compute(clientIp, (ip, state) -> {
            if (state == null || (currentTime - state.windowStartTime) > ONE_MINUTE_IN_MILLIS) {
                return new UserRateState(currentTime);
            } else {
                state.requestCount.incrementAndGet();
                return state;
            }
        });

        int currentRequests = rateState.requestCount.get();
        int remaining = Math.max(0, MAX_REQUESTS_PER_MINUTE - currentRequests);

        exchange.getResponse().getHeaders().add("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS_PER_MINUTE));
        exchange.getResponse().getHeaders().add("X-RateLimit-Remaining", String.valueOf(remaining));

        if (currentRequests > MAX_REQUESTS_PER_MINUTE) {
            return onRateLimitExceeded(exchange, clientIp);
        }

        return chain.filter(exchange);
    }

    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        if (request.getRemoteAddress() != null && request.getRemoteAddress().getAddress() != null) {
            return request.getRemoteAddress().getAddress().getHostAddress();
        }
        return "unknown-client";
    }

    private Mono<Void> onRateLimitExceeded(ServerWebExchange exchange, String clientIp) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String jsonResponse = String.format(
                "{\"timestamp\":\"%s\",\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Rate limit exceeded for IP: %s. Maximum %d requests per minute permitted.\",\"path\":\"%s\"}",
                Instant.now().toString(),
                clientIp,
                MAX_REQUESTS_PER_MINUTE,
                exchange.getRequest().getURI().getPath()
        );

        DataBuffer buffer = response.bufferFactory().wrap(jsonResponse.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -90; // Runs right after JwtAuthenticationFilter
    }
}
