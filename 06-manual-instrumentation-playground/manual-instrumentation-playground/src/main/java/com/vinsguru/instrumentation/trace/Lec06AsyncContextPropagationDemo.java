package com.vinsguru.instrumentation.trace;

import com.vinsguru.instrumentation.CommonUtil;
import io.opentelemetry.context.Context;

public class Lec06AsyncContextPropagationDemo {

    public static void main(String[] args) {

        var demo = new Lec06AsyncContextPropagationDemo();
        //POST /orders
        demo.processOrder();

        CommonUtil.sleepSeconds(2);

    }

    private void processOrder() {
        TraceUtil.trace("processOrder", span -> {
            var t1 = Thread.ofVirtual().start(Context.current().wrap(this::processPayment));
            var t2 = Thread.ofVirtual().start(Context.current().wrap(this::deductInventory));
            var t3 = Thread.ofVirtual().start(Context.current().wrap(this::sendNotification));
            this.awaitCompletion(t1, t2, t3);
            span.setAttribute("order.id", 124);
            span.setAttribute("order.amount", 1500);
        });
    }

    private void processPayment() {
        TraceUtil.trace("processPayment", span -> {
            CommonUtil.sleepMillis(150);
            span.setAttribute("payment.method", "CREDIT_CARD");
        });
    }

    private void deductInventory() {
        TraceUtil.trace("deductInventory", span -> {
            CommonUtil.sleepMillis(125);
        });
    }

    private void sendNotification() {
        TraceUtil.trace("sendNotification", span -> {
            CommonUtil.sleepMillis(100);
        });
    }

    private void awaitCompletion(Thread... threads){
        for(var thread: threads){
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
