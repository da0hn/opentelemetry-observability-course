package com.vinsguru.observability.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.vinsguru.observability.DebugRequestKeys;
import org.slf4j.MDC;
import org.slf4j.Marker;

import java.util.Objects;

// We enable all logging levels intentionally if it is a "Debug-Request"
public class OnDemandLogFilter extends TurboFilter {

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        var requestId = MDC.get(DebugRequestKeys.MDC);
        return Objects.nonNull(requestId) ? FilterReply.ACCEPT : FilterReply.NEUTRAL;
    }

}
