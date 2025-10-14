package com.vinsguru.instrumentation.metrics;

import com.vinsguru.instrumentation.CommonUtil;
import com.vinsguru.instrumentation.OpenTelemetryConfig;
import io.opentelemetry.api.metrics.LongUpDownCounter;
import io.opentelemetry.api.metrics.Meter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

//Goal: To track the number of tasks actively running in our application
public class Lec04UpDownCounterDemo {

    private static final Meter meter = OpenTelemetryConfig.meter(Lec04UpDownCounterDemo.class);

    public static void main(String[] args) {

        var counter = createActiveTasksCounter();
        var taskExecutor = new TaskExecutor(counter, Executors.newVirtualThreadPerTaskExecutor());

        for (int i = 0; i < 10_000; i++) {
            taskExecutor.execute(() -> CommonUtil.sleepMillis(ThreadLocalRandom.current().nextInt(100, 10_000)));
            CommonUtil.sleepMillis(200);
        }

    }

    // thread safe
    private static LongUpDownCounter createActiveTasksCounter() {
        return meter.upDownCounterBuilder("app.tasks.active.count")
                    .setDescription("Number of tasks currently active")
                    .setUnit("{task}")
                    .build();
    }

    // spring bean
    private static class TaskExecutor {

        private final LongUpDownCounter counter;
        private final ExecutorService executorService;

        private TaskExecutor(LongUpDownCounter counter, ExecutorService executorService) {
            this.counter = counter;
            this.executorService = executorService;
        }

        public void execute(Runnable task){
            this.executorService.submit(this.wrap(task));
        }

        private Runnable wrap(Runnable task){
            return () -> {
                this.counter.add(1);
                task.run();
                this.counter.add(-1);
            };
        }

    }


}
