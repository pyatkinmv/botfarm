package ru.pyatkinmv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BotfarmApplication {
    public static void main(String[] args) {
        SpringApplication.run(BotfarmApplication.class, args);
    }

    // TODO: in box logging
    //  scheduling
    //  event generating
    //  messaging
    //  avoid 6 request per sec bound
    //  cache or custom count

}
