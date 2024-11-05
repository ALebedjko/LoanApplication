package io.fourfinanceit.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.fourfinanceit.BootApplication;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
        classes = BootApplication.class)
@WebAppConfiguration
public abstract class AbstractControllerTest {

    MockMvc mvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    String objectToJson(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.writeValueAsString(obj);
    }

    <T> T mapFromJson(String json, TypeReference type)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.readValue(json, type);
    }
}
