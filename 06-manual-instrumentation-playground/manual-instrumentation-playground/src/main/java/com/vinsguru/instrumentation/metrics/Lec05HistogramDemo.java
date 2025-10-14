package com.vinsguru.instrumentation.metrics;

import com.vinsguru.instrumentation.CommonUtil;
import com.vinsguru.instrumentation.OpenTelemetryConfig;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.LongUpDownCounter;
import io.opentelemetry.api.metrics.Meter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.LongStream;

// Goal: To track how long tasks take to complete and see their distribution using a histogram
public class Lec05HistogramDemo {

    private static final Meter meter = OpenTelemetryConfig.meter(Lec05HistogramDemo.class);

    public static void main(String[] args) {

        var histogram = createHistogram();
        var taskExecutor = new TaskExecutor(histogram, Executors.newVirtualThreadPerTaskExecutor());

        for (int i = 0; i < 10_000; i++) {
            taskExecutor.execute(() -> CommonUtil.sleepMillis(ThreadLocalRandom.current().nextInt(100, 10_000)));
            CommonUtil.sleepMillis(200);
        }

    }

    // thread safe
    private static LongHistogram createHistogram() {
        var buckets = LongStream.iterate(1000, i -> i + 1000)
                                .limit(10)
                                .boxed()
                                .toList();
        return meter.histogramBuilder("app.tasks.execution.duration")
                    .ofLongs()
                    .setDescription("Time taken for tasks to complete execution")
                    .setUnit("ms")
                    .setExplicitBucketBoundariesAdvice(buckets)
                    .build();
    }

    // spring bean
    private static class TaskExecutor {

        private final LongHistogram histogram;
        private final ExecutorService executorService;

        private TaskExecutor(LongHistogram histogram, ExecutorService executorService) {
            this.histogram = histogram;
            this.executorService = executorService;
        }

        public void execute(Runnable task) {
            this.executorService.submit(this.wrap(task));
        }

        private Runnable wrap(Runnable task) {
            return () -> {
                var before = System.currentTimeMillis();
                task.run();
                var after = System.currentTimeMillis();
                this.histogram.record(after - before);
            };
        }

    }


}
