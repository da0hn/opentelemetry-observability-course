package com.vinsguru.instrumentation.logs;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.MDC;
import org.slf4j.Marker;

// TurboFilter can enable DEBUG based on certain conditions.
// Just For Demo: We enable DEBUG logging if MDC contains userId = "5".
public class TurboDebugFilter extends TurboFilter {

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        var userId = MDC.get("userId");
        if("5".equals(userId) && level.isGreaterOrEqual(Level.DEBUG)){
            return FilterReply.ACCEPT;
        }
        return FilterReply.NEUTRAL;
    }

}
