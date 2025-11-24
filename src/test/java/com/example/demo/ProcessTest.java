package com.example.demo;

import io.camunda.client.CamundaClient;
import io.camunda.process.test.api.CamundaProcessTestContext;
import io.camunda.process.test.api.CamundaSpringProcessTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static io.camunda.process.test.api.assertions.UserTaskSelectors.byElementId;

@SpringBootTest
@CamundaSpringProcessTest
public class ProcessTest {

    @Autowired
    CamundaClient client;

    @Autowired
    CamundaProcessTestContext processTestContext;

    @Test
    void test() {
        client.newCreateInstanceCommand()
                .bpmnProcessId("Process_DoIt")
                .latestVersion()
                .send()
                .join();

        processTestContext.completeUserTask(byElementId("UserTask_DoIt"));
        System.err.println("completed task: 1");

        // following statement fails
        processTestContext.completeUserTask(byElementId("UserTask_DoIt"));
    }
}
