package com.vinsguru.observability;

import com.vinsguru.observability.config.OnDemandObservabilityConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(OnDemandObservabilityConfig.class)
public @interface EnableOnDemandObservability {
}
