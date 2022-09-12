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
package io.orkes.conductor.client;

import java.util.List;

import com.netflix.conductor.common.metadata.workflow.RerunWorkflowRequest;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.model.BulkResponse;
import com.netflix.conductor.common.run.SearchResult;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.common.run.WorkflowSummary;
import com.squareup.okhttp.Call;
import io.orkes.conductor.client.http.ApiCallback;

public interface WorkflowClient {
    String startWorkflow(StartWorkflowRequest startWorkflowRequest);

    Workflow getWorkflow(String workflowId, boolean includeTasks);

    List<Workflow> getWorkflows(
            String name, String correlationId, boolean includeClosed, boolean includeTasks);

    void populateWorkflowOutput(Workflow workflow);

    void deleteWorkflow(String workflowId, boolean archiveWorkflow);

    BulkResponse terminateWorkflows(List<String> workflowIds, String reason);

    List<String> getRunningWorkflow(String workflowName, Integer version);

    List<String> getWorkflowsByTimePeriod(
            String workflowName, int version, Long startTime, Long endTime);

    void runDecider(String workflowId);

    void pauseWorkflow(String workflowId);

    void resumeWorkflow(String workflowId);

    void skipTaskFromWorkflow(String workflowId, String taskReferenceName);

    String rerunWorkflow(String workflowId, RerunWorkflowRequest rerunWorkflowRequest);

    void restart(String workflowId, boolean useLatestDefinitions);

    void retryLastFailedTask(String workflowId);

    void resetCallbacksForInProgressTasks(String workflowId);

    void terminateWorkflow(String workflowId, String reason);

    SearchResult<WorkflowSummary> search(String query);

    SearchResult<Workflow> searchV2(String query);

    SearchResult<WorkflowSummary> search(
            Integer start, Integer size, String sort, String freeText, String query);

    SearchResult<Workflow> searchV2(
            Integer start, Integer size, String sort, String freeText, String query);

    //Bulk operations
    BulkResponse pauseWorkflow(List<String> body);

    Call pauseWorkflowAsync(List<String> body, ApiCallback<BulkResponse> callback);

    BulkResponse restartWorkflow(List<String> body, Boolean useLatestDefinitions);

    Call restartWorkflowAsync(List<String> body, Boolean useLatestDefinitions, ApiCallback<BulkResponse> callback);

    BulkResponse resumeWorkflow(List<String> body);

    Call resumeWorkflowAsync(List<String> body, ApiCallback<BulkResponse> callback);

    BulkResponse retryWorkflow(List<String> body);
    Call retryWorkflowAsync(List<String> body, ApiCallback<BulkResponse> callback);

    BulkResponse terminateWorkflow(List<String> body, String reason);

    Call terminateWorkflowAsync(List<String> body, String reason, ApiCallback<BulkResponse> callback);
}
