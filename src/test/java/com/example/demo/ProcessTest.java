package com.example.demo;

import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ProcessInstanceEvent;
import io.camunda.client.api.search.enums.UserTaskState;
import io.camunda.client.api.search.filter.UserTaskFilter;
import io.camunda.client.api.search.response.UserTask;
import io.camunda.process.test.api.CamundaAssert;
import io.camunda.process.test.api.CamundaProcessTestContext;
import io.camunda.process.test.api.CamundaSpringProcessTest;
import io.camunda.process.test.api.assertions.UserTaskSelector;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static io.camunda.process.test.api.assertions.ElementSelectors.byId;
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
        final ProcessInstanceEvent processInstance = client.newCreateInstanceCommand()
                .bpmnProcessId("Process_DoIt")
                .latestVersion()
                .send()
                .join();

        processTestContext.completeUserTask(byElementId("UserTask_DoIt"));
        System.err.println("completed task: 1");

        // -------------------------------

        // 1) Eventually consistency: wait until the previous user task completion is visible
        CamundaAssert.assertThat(processInstance).hasCompletedElement(byId("UserTask_DoIt"), 1);

        // following statement fails
        // 2) Workaround: use a custom selector to filter only created/active user tasks
        processTestContext.completeUserTask(byElementId("UserTask_DoIt").and(
                userTask -> UserTaskState.CREATED.equals(userTask.getState())
        ));
        System.err.println("completed task: 2");

        // -------------------------------

        CamundaAssert.assertThat(processInstance).hasCompletedElement(byId("UserTask_DoIt"), 2);

        // 3) Alternative workaround: extract the custom selector implementation
        processTestContext.completeUserTask(byElementId("UserTask_DoIt").and(created()));

        System.err.println("completed task: 3");

        // -------------------------------

        CamundaAssert.assertThat(processInstance).hasCompletedElement(byId("UserTask_DoIt"), 3);
    }

    private UserTaskSelector created() {
        return new CreatedUserTaskSelector();
    }

    private static final class CreatedUserTaskSelector implements UserTaskSelector {
        @Override
        public boolean test(UserTask userTask) {
            return UserTaskState.CREATED.equals(userTask.getState());
        }

        @Override
        public void applyFilter(UserTaskFilter filter) {
            filter.state(UserTaskState.CREATED);
        }

        @Override
        public String describe() {
            return "is created";
        }
    }
}
