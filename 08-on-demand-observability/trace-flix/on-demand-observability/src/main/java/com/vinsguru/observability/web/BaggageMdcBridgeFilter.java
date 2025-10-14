package com.vinsguru.observability.web;

import com.vinsguru.observability.DebugRequestKeys;
import io.opentelemetry.api.baggage.Baggage;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class BaggageMdcBridgeFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var requestId = Baggage.current().getEntryValue(DebugRequestKeys.BAGGAGE);
        try(var ignored = MDC.putCloseable(DebugRequestKeys.MDC, requestId)){ // requestId can be null. that's ok!!
            filterChain.doFilter(request, response);
        }
    }

}
