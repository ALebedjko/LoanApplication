package io.fourfinanceit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = HomeworkApplication.class)
public class HomeworkApplicationTests {

    @Autowired
    private ApplicationContext appContext;

    @Test
    public void contextLoads() {
       assertNotNull(appContext);
    }

}
