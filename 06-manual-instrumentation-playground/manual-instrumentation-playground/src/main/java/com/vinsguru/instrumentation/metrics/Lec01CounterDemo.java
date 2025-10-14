package com.vinsguru.instrumentation.metrics;

import com.vinsguru.instrumentation.CommonUtil;
import com.vinsguru.instrumentation.OpenTelemetryConfig;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;

public class Lec01CounterDemo {

    private static final Meter meter = OpenTelemetryConfig.meter(Lec01CounterDemo.class);

    public static void main(String[] args) {

        var counter = createProductViewCounter();
        var controller = new ProductController(counter);

        for (int i = 0; i < 10_000; i++) {
            controller.viewProduct();
        }

    }

    // spring bean - thread safe
    private static LongCounter createProductViewCounter(){
        return meter.counterBuilder("app.product.view.count")
                .setDescription("Total number of product view")
                .setUnit("{view}")
                .build();
    }

    // REST controller
    private static class ProductController {

        private final LongCounter counter;

        private ProductController(LongCounter counter) {
            this.counter = counter;
        }

        public void viewProduct(){
            CommonUtil.sleepSeconds(1);
            this.counter.add(1);
        }

    }


}
