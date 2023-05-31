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
package io.orkes.conductor.client.api;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.sdk.workflow.def.tasks.SimpleTask;
import org.junit.jupiter.api.Test;

import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.sdk.workflow.def.ConductorWorkflow;
import com.netflix.conductor.sdk.workflow.def.tasks.Http;
import com.netflix.conductor.sdk.workflow.executor.WorkflowExecutor;

import io.orkes.conductor.client.WorkflowClient;
import io.orkes.conductor.client.util.Commons;

import com.google.common.util.concurrent.Uninterruptibles;

import static org.junit.jupiter.api.Assertions.*;

public class WorkflowClientTests extends ClientTest {
    private final WorkflowClient workflowClient;

    private final WorkflowExecutor workflowExecutor;

    public WorkflowClientTests() {
        this.workflowClient = super.orkesClients.getWorkflowClient();
        this.workflowExecutor = new WorkflowExecutor(
                super.orkesClients.getTaskClient(),
                super.orkesClients.getWorkflowClient(),
                super.orkesClients.getMetadataClient(),
                10);
    }

    @Test
    public void testSearchByCorrelationIds() {
        List<String> correlationIds = new ArrayList<>();
        Set<String> workflowNames = new HashSet<>();
        Map<String, Set<String>> correlationIdToWorkflows = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            String correlationId = UUID.randomUUID().toString();
            correlationIds.add(correlationId);
            for (int j = 0; j < 5; j++) {
                ConductorWorkflow<Object> workflow = new ConductorWorkflow<>(workflowExecutor);
                workflow.add(new Http("http").url("https://orkes-api-tester.orkesconductor.com/get"));
                workflow.setName("workflow_" + j);
                workflowNames.add(workflow.getName());
                StartWorkflowRequest request = new StartWorkflowRequest();
                request.setName(workflow.getName());
                request.setWorkflowDef(workflow.toWorkflowDef());
                request.setCorrelationId(correlationId);
                String id = workflowClient.startWorkflow(request);
                Set<String> ids = correlationIdToWorkflows.getOrDefault(correlationId, new HashSet<>());
                ids.add(id);
                correlationIdToWorkflows.put(correlationId, ids);
            }
        }
        //Let's give couple of seconds for indexing to complete
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
        Map<String, List<Workflow>> result = workflowClient.getWorkflowsByNamesAndCorrelationIds(correlationIds, workflowNames.stream().collect(Collectors.toList()), true, false);
        assertNotNull(result);
        assertEquals(correlationIds.size(), result.size());
        for (String correlationId : correlationIds) {
            assertEquals(5, result.get(correlationId).size());
            Set<String> ids = result.get(correlationId).stream().map(wf -> wf.getWorkflowId()).collect(Collectors.toSet());
            assertEquals(correlationIdToWorkflows.get(correlationId), ids);
        }
    }

    @Test
    public void testUpdateVariables() {
        ConductorWorkflow<Object> workflow = new ConductorWorkflow<>(workflowExecutor);
        workflow.add(new SimpleTask("simple_task","simple_task_ref"));
        workflow.setTimeoutPolicy(WorkflowDef.TimeoutPolicy.TIME_OUT_WF);
        workflow.setTimeoutSeconds(60);
        workflow.setName("update_variable_test");
        workflow.setVersion(1);
        workflow.registerWorkflow(true, true);

        StartWorkflowRequest request = new StartWorkflowRequest();
        request.setName(workflow.getName());
        request.setVersion(workflow.getVersion());
        request.setInput(Map.of());
        String workflowId = workflowClient.startWorkflow(request);
        assertNotNull(workflowId);

        Workflow execution = workflowClient.getWorkflow(workflowId, false);
        assertNotNull(execution);
        assertTrue(execution.getVariables().isEmpty());

        Map<String, Object> variables = Map.of("k1", "v1", "k2", 42, "k3", Arrays.asList(3, 4, 5));
        execution = workflowClient.updateVariables(workflowId, variables);
        assertNotNull(execution);
        assertFalse(execution.getVariables().isEmpty());
        assertEquals(variables.get("k1"), execution.getVariables().get("k1"));
        assertEquals(variables.get("k2").toString(), execution.getVariables().get("k2").toString());
        assertEquals(variables.get("k3").toString(), execution.getVariables().get("k3").toString());

        Map<String, Object> map = new HashMap<>();
        map.put("k1", null);
        map.put("v1", "xyz");
        execution = workflowClient.updateVariables(workflowId, map);
        assertNotNull(execution);
        assertFalse(execution.getVariables().isEmpty());
        assertEquals(null, execution.getVariables().get("k1"));
        assertEquals(variables.get("k2").toString(), execution.getVariables().get("k2").toString());
        assertEquals(variables.get("k3").toString(), execution.getVariables().get("k3").toString());
        assertEquals("xyz", execution.getVariables().get("v1").toString());

    }

    StartWorkflowRequest getStartWorkflowRequest() {
        StartWorkflowRequest startWorkflowRequest = new StartWorkflowRequest();
        startWorkflowRequest.setName(Commons.WORKFLOW_NAME);
        startWorkflowRequest.setVersion(1);
        startWorkflowRequest.setInput(new HashMap<>());
        return startWorkflowRequest;
    }
}
