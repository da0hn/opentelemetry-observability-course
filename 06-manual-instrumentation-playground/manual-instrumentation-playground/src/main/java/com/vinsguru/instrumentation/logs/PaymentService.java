package com.vinsguru.instrumentation.logs;

import com.vinsguru.instrumentation.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    public void processPayment(int amount){
        log.debug("Entering processPayment method. amount received: {}", amount);
        log.info("Payment started");
        CommonUtil.sleepMillis(50);
        log.info("Payment processed. amount: {}", amount);
    }

}
