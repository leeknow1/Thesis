package com.miras.cclearner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;

@SpringBootApplication
public class CclearnerApplication {

    private static final Date afterOneHour = new Date(System.currentTimeMillis() + 3600000);

    public static void main(String[] args) {
        SpringApplication.run(CclearnerApplication.class, args);
    }

    public static boolean passedOneHour() {
        return new Date().after(afterOneHour);
    }

}
