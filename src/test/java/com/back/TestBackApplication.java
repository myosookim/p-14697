package com.back;

import org.springframework.boot.SpringApplication;
import org.testcontainers.utility.TestcontainersConfiguration;

public class TestBackApplication {

    public static void main(String[] args) {
        SpringApplication.from(BackApplication::main).with(TestcontainersConfiguration.class).run(args);
    }
}


