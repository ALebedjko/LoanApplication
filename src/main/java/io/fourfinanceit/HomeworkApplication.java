package io.fourfinanceit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan(
        basePackageClasses = {HomeworkApplication.class, org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters.class}
)
@SpringBootApplication
public class HomeworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomeworkApplication.class, args);
    }
}
