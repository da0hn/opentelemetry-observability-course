package com.vinsguru.instrumentation.metrics;

import com.vinsguru.instrumentation.CommonUtil;
import com.vinsguru.instrumentation.OpenTelemetryConfig;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.metrics.ObservableLongGauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Lec03GaugeDemo {

    private static final Logger log = LoggerFactory.getLogger(Lec03GaugeDemo.class);
    private static final Meter meter = OpenTelemetryConfig.meter(Lec03GaugeDemo.class);

    public static void main(String[] args) {

        try(var gauge = createJvmMemoryUsedGauge()){
            simulateMemoryUsage();
        }

    }

    // This method is only for demonstrating the JVM memory gauge
    private static void simulateMemoryUsage(){
        var memory = new ArrayList<byte[]>();
        for (int i = 1; i <= 10_000 ; i++) {
            // allocate ~10MB each iteration
            memory.add(new byte[1024 * 1024 * 10]);
            log.info("allocated {} MB", memory.size() * 10);

            CommonUtil.sleepSeconds(1);

            // Clear memory every minute to see the gauge drop
            if(i % 60 == 0){
                log.info("clearing list / memory");
                memory.clear();
                //Note: System.gc() is only for demonstrating the gauge. Do not use in real apps.
                System.gc(); // may not do immediately
            }

        }

    }

    // observable gauge that automatically reports JVM memory utilization
    private static ObservableLongGauge createJvmMemoryUsedGauge() {
        return meter.gaugeBuilder("jvm.memory.used")
                    .ofLongs()
                    .setDescription("The amount of JVM memory currently used")
                    .setUnit("By")
                    .buildWithCallback(o -> {
                        log.info("executing callback for jvm.memory.used");
                        var usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                        o.record(usedMemory);
                    });
    }

}
