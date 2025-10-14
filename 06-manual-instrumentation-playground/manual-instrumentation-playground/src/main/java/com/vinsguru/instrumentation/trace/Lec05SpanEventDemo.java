package com.vinsguru.instrumentation.trace;

import com.vinsguru.instrumentation.CommonUtil;

public class Lec05SpanEventDemo {

    public static void main(String[] args) {

        var demo = new Lec05SpanEventDemo();
        //POST /orders
        demo.processOrder();
        
        CommonUtil.sleepSeconds(2);

    }

    private void processOrder() {
        TraceUtil.trace("processOrder", span -> {
            processPayment();
            deductInventory();
            sendNotification();

            span.setAttribute("order.id", 124);
            span.setAttribute("order.amount", 1500);
        });
    }

    private void processPayment() {
        TraceUtil.trace("processPayment", span -> {
            CommonUtil.sleepMillis(150);
            span.addEvent("payment failed...retrying");
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

}
