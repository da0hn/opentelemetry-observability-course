package com.vinsguru.instrumentation.logs;

import com.vinsguru.instrumentation.CommonUtil;
import com.vinsguru.instrumentation.OpenTelemetryConfig;
import org.slf4j.MDC;

public class Lec02TurboFilterDemo {

    private static final PaymentService paymentService = new PaymentService();

    public static void main(String[] args) {

        OpenTelemetryConfig.setupLoggingAppender();

        for (int i = 1; i <= 10 ; i++) {
            processRequest(i);
        }

        CommonUtil.sleepSeconds(1);

    }

    private static void processRequest(int userId){
        try(var ignored = MDC.putCloseable("userId", String.valueOf(userId))){
            paymentService.processPayment(userId * 10);
        }
    }

}
