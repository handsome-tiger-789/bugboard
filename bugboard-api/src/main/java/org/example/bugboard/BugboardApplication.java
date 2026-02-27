package org.example.bugboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BugboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(BugboardApplication.class, args);
    }

}
