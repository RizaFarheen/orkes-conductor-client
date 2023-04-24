/*
 * Copyright 2022 Orkes, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.orkes.conductor.client.e2e;

import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.run.Workflow;
import io.orkes.conductor.client.ApiClient;
import io.orkes.conductor.client.WorkflowClient;
import io.orkes.conductor.client.http.OrkesWorkflowClient;
import io.orkes.conductor.client.util.ApiUtil;
import io.orkes.conductor.common.model.WorkflowRun;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class SyncWorkflowExecutionTest {

    static ApiClient apiClient;
    static WorkflowClient workflowClient;

    @BeforeAll
    public static void init() {
        apiClient = ApiUtil.getApiClientWithCredentials();
        workflowClient = new OrkesWorkflowClient(apiClient);
    }

    @Test
    @DisplayName("Check sync workflow is execute within 2 seconds")
    public void testSyncWorkflowExecution() {

        String workflowName = "load_test_perf";

        // Trigger two workflows
        StartWorkflowRequest startWorkflowRequest = new StartWorkflowRequest();
        startWorkflowRequest.setName(workflowName);
        startWorkflowRequest.setVersion(1);

        CompletableFuture<WorkflowRun> completableFuture = workflowClient.executeWorkflow(startWorkflowRequest, null);
        try {
            WorkflowRun workflowRun = completableFuture.get(5, TimeUnit.SECONDS);
            System.out.println("WorkflowId is " + workflowRun.getWorkflowId());
            assertEquals(Workflow.WorkflowStatus.COMPLETED, workflowRun.getStatus());
        } catch (Exception e) {
            throw new RuntimeException("Workflow " + workflowName + " did not complete in 5 seconds");
        }
    }

    @Test
    @DisplayName("Check sync workflow end with simple task.")
    public void testSyncWorkflowExecution2() {

        String workflowName = "sync_workflow_end_with_simple_task";

        // Trigger two workflows
        StartWorkflowRequest startWorkflowRequest = new StartWorkflowRequest();
        startWorkflowRequest.setName(workflowName);
        startWorkflowRequest.setVersion(1);

        CompletableFuture<WorkflowRun> completableFuture = workflowClient.executeWorkflow(startWorkflowRequest, "simple_task_rka0w_ref");
        try {
            WorkflowRun workflowRun = completableFuture.get(2, TimeUnit.SECONDS);
            System.out.println("WorkflowId is " + workflowRun.getWorkflowId());
            assertEquals(Workflow.WorkflowStatus.RUNNING, workflowRun.getStatus());
            workflowClient.terminateWorkflow(workflowRun.getWorkflowId(), "Terminated");
        } catch (Exception e) {
            throw new RuntimeException("Sync workflow api did not returned in 2 second");
        }
    }

    @Test
    @DisplayName("Check sync workflow end with set variable task.")
    public void testSyncWorkflowExecution3() {

        String workflowName = "sync_workflow_end_with_set_variable_task";

        // Trigger two workflows
        StartWorkflowRequest startWorkflowRequest = new StartWorkflowRequest();
        startWorkflowRequest.setName(workflowName);
        startWorkflowRequest.setVersion(1);

        CompletableFuture<WorkflowRun> completableFuture = workflowClient.executeWorkflow(startWorkflowRequest, "set_variable_task_1fi09_ref");
        try {
            WorkflowRun workflowRun = completableFuture.get(3, TimeUnit.SECONDS);
            System.out.println("WorkflowId is " + workflowRun.getWorkflowId());
            assertEquals(Workflow.WorkflowStatus.RUNNING, workflowRun.getStatus());
            workflowClient.terminateWorkflow(workflowRun.getWorkflowId(), "Terminated");
        } catch (Exception e) {
            throw new RuntimeException("Sync workflow api did not returned in 3 second");
        }
    }

    @Test
    @DisplayName("Check sync workflow end with set variable task.")
    public void testSyncWorkflowExecution4() {

        String workflowName = "sync_workflow_end_with_jq_task";

        // Trigger two workflows
        StartWorkflowRequest startWorkflowRequest = new StartWorkflowRequest();
        startWorkflowRequest.setName(workflowName);
        startWorkflowRequest.setVersion(1);

        CompletableFuture<WorkflowRun> completableFuture = workflowClient.executeWorkflow(startWorkflowRequest, "json_transform_task_jjowa_ref");
        try {
            WorkflowRun workflowRun = completableFuture.get(3, TimeUnit.SECONDS);
            System.out.println("WorkflowId is " + workflowRun.getWorkflowId());
            assertEquals(Workflow.WorkflowStatus.RUNNING, workflowRun.getStatus());
            workflowClient.terminateWorkflow(workflowRun.getWorkflowId(), "Terminated");
        } catch (Exception e) {
            throw new RuntimeException("Sync workflow api did not returned in 3 second");
        }
    }

    @Test
    @DisplayName("Check sync workflow end with sub workflow task.")
    public void testSyncWorkflowExecution5() {

        String workflowName = "sync_workflow_end_with_subworkflow_task";

        // Trigger two workflows
        StartWorkflowRequest startWorkflowRequest = new StartWorkflowRequest();
        startWorkflowRequest.setName(workflowName);
        startWorkflowRequest.setVersion(1);

        CompletableFuture<WorkflowRun> completableFuture = workflowClient.executeWorkflow(startWorkflowRequest, "http_sync");
        try {
            WorkflowRun workflowRun = completableFuture.get(3, TimeUnit.SECONDS);
            System.out.println("WorkflowId is " + workflowRun.getWorkflowId());
            assertEquals(Workflow.WorkflowStatus.RUNNING, workflowRun.getStatus());
            workflowClient.terminateWorkflow(workflowRun.getWorkflowId(), "Terminated");
        } catch (Exception e) {
            throw new RuntimeException("Sync workflow api did not returned in 3 second");
        }
    }

    @Test
    @DisplayName("Check sync workflow end with failed case")
    public void testSyncWorkflowExecution6() {

        String workflowName = "sync_workflow_failed_case";

        // Trigger two workflows
        StartWorkflowRequest startWorkflowRequest = new StartWorkflowRequest();
        startWorkflowRequest.setName(workflowName);
        startWorkflowRequest.setVersion(1);

        CompletableFuture<WorkflowRun> completableFuture = workflowClient.executeWorkflow(startWorkflowRequest, "get_random_fact");
        try {
            WorkflowRun workflowRun = completableFuture.get(3, TimeUnit.SECONDS);
            System.out.println("WorkflowId is " + workflowRun.getWorkflowId());
            assertEquals(Workflow.WorkflowStatus.RUNNING, workflowRun.getStatus());
            workflowClient.terminateWorkflow(workflowRun.getWorkflowId(), "Terminated");
        } catch (Exception e) {
            throw new RuntimeException("Sync workflow api did not returned in 3 second");
        }
    }

    @Test
    @DisplayName("Check sync workflow end with no poller")
    public void testSyncWorkflowExecution7() {

        String workflowName = "sync_workflow_no_poller";

        // Trigger two workflows
        StartWorkflowRequest startWorkflowRequest = new StartWorkflowRequest();
        startWorkflowRequest.setName(workflowName);
        startWorkflowRequest.setVersion(1);

        CompletableFuture<WorkflowRun> completableFuture = workflowClient.executeWorkflow(startWorkflowRequest, "get_random_fact");
        try {
            WorkflowRun workflowRun = completableFuture.get(3, TimeUnit.SECONDS);
            System.out.println("WorkflowId is " + workflowRun.getWorkflowId());
            assertEquals(Workflow.WorkflowStatus.RUNNING, workflowRun.getStatus());
            workflowClient.terminateWorkflow(workflowRun.getWorkflowId(), "Terminated");
        } catch (Exception e) {
            throw new RuntimeException("Sync workflow api did not returned in 3 second");
        }
    }
}
