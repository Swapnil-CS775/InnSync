package com.innsync.order.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    /**
     * This is a MOCK implementation for local development.
     * It simulates sending an OTP by printing it clearly to the console.
     * Later, we can replace the logic inside this method with a real MSG91 API call
     * without changing any other part of our application.
     */
    public void sendOtp(String phoneNumber, String otp) {
        // We don't need an MSG91 account for this to work.
        logger.info("==================================================");
        logger.info("           SIMULATING OTP SEND                    ");
        logger.info("           To Phone: {}", phoneNumber);
        logger.info("           OTP Code: {}", otp);
        logger.info("==================================================");
    }
}