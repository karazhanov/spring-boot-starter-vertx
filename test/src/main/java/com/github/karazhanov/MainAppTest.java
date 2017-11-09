package com.github.karazhanov;

import com.github.karazhanov.configuration.EnableVertX;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author karazhanov on 06.11.17.
 */
@SpringBootApplication
@EnableVertX
public class MainAppTest {

    public static void main(String[] args) {
        SpringApplication.run(MainAppTest.class, args);
    }
}
