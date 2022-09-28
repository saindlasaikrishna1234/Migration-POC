package com.acms.platform.maintenancenotifications.functional.cucumber.utils;

import com.fasterxml.jackson.core.JsonProcessingException;

public class KmnChecker {

    public static void checkWithRetry(Checker checker) {
        int i = 0;
        while (true) {
            try {
                checker.check();
                break;
            } catch (AssertionError | JsonProcessingException e) {
                if (i++ == 10) {
                    throw new AssertionError(e);
                }
                try {
                    System.out.println("ZZZZZZZZZZZZZZZZZZzzzzzzzzz... wait to retry... " + i);
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    //nothing
                }
            }
        }
    }

    @FunctionalInterface
    public interface Checker {
        void check() throws AssertionError, JsonProcessingException;
    }
}
