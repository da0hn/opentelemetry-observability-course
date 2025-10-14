package com.vinsguru.instrumentation.metrics;

import com.vinsguru.instrumentation.CommonUtil;
import com.vinsguru.instrumentation.OpenTelemetryConfig;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;

import java.util.concurrent.ThreadLocalRandom;

public class Lec02CounterWithAttributesDemo {

    private static final Meter meter = OpenTelemetryConfig.meter(Lec02CounterWithAttributesDemo.class);

    public static void main(String[] args) {

        var counter = createProductViewCounter();
        var productViewRecorder = new ProductViewRecorder(counter);
        var controller = new ProductController(productViewRecorder);

        for (int i = 0; i < 10_000; i++) {
            controller.viewProduct(ThreadLocalRandom.current().nextInt(1, 4));
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

        private final ProductViewRecorder productViewRecorder;

        private ProductController(ProductViewRecorder productViewRecorder) {
            this.productViewRecorder = productViewRecorder;
        }

        // GET /products/{productId}
        public void viewProduct(long productId){
            CommonUtil.sleepSeconds(1);
            this.productViewRecorder.recordView(productId);
        }

    }

    // spring bean
    private static class ProductViewRecorder {

        private static final AttributeKey<Long> PRODUCT_ID_KEY = AttributeKey.longKey("product.id");
        private final LongCounter counter;

        private ProductViewRecorder(LongCounter counter) {
            this.counter = counter;
        }

        public void recordView(long productId){
            this.counter.add(1, Attributes.of(
                    PRODUCT_ID_KEY, productId
            ));
        }

    }

}
