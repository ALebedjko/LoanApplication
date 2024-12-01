package io.fourfinanceit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BootApplication.class)
public class HomeworkApplicationTests {

    @Autowired
    private ApplicationContext appContext;

    @Test
    public void contextLoads() {
        assertNotNull(appContext, "Application context should not be null");
    }
}