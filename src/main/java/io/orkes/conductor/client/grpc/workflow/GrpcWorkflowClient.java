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
package io.orkes.conductor.client.grpc.workflow;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.Uninterruptibles;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;

import io.orkes.conductor.client.ApiClient;
import io.orkes.conductor.client.grpc.HeaderClientInterceptor;
import io.orkes.conductor.common.model.WorkflowRun;
import io.orkes.conductor.proto.ProtoMappingHelper;
import io.orkes.grpc.service.OrkesWorkflowService;
import io.orkes.grpc.service.WorkflowServiceStreamGrpc;

import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import static io.orkes.conductor.client.grpc.ChannelManager.getChannel;

@Slf4j
public class GrpcWorkflowClient {

    private WorkflowServiceStreamGrpc.WorkflowServiceStreamStub stub;

    private StreamObserver<OrkesWorkflowService.StartWorkflowRequest> requestStream;

    private StartWorkflowResponseStream responseStream;

    private final ProtoMappingHelper protoMappingHelper = ProtoMappingHelper.INSTANCE;

    private final WorkflowExecutionMonitor executionMonitor;

    private final ManagedChannel channel;

    public GrpcWorkflowClient(ApiClient apiClient) {
        this.executionMonitor = new WorkflowExecutionMonitor();
        this.channel = getChannel(apiClient);

        stub =
                WorkflowServiceStreamGrpc.newStub(channel)
                        .withInterceptors(new HeaderClientInterceptor(apiClient));
        this.responseStream = new StartWorkflowResponseStream(executionMonitor);
        requestStream = stub.startWorkflow(responseStream);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(()-> monitorChannel(channel), 1, 1, TimeUnit.SECONDS);
    }

    private void monitorChannel(ManagedChannel channel) {
        ConnectivityState state = channel.getState(false);
        switch (state) {
            case READY:
                if(!responseStream.isReady()) {
                    log.info("Connection State {}", state);
                }
                break;
            case SHUTDOWN:
            case CONNECTING:
            case TRANSIENT_FAILURE:
                log.info("Connection state {}", state);
                reConnect();
                break;
            default:
                log.info("Channel state: {}", state);
        }
    }

    private boolean reConnect() {
        try {
            requestStream = stub.startWorkflow(this.responseStream);
            Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception connectException) {
            log.error("Server not ready {}", connectException.getMessage(), connectException);
            return false;
        }
    }

    public CompletableFuture<WorkflowRun> executeWorkflow(
            StartWorkflowRequest startWorkflowRequest, String waitUntilTask) {
        if (!responseStream.isReady()) {
            reConnect();
            throw new RuntimeException("Server is not yet ready to accept the requests");
        }
        String requestId = UUID.randomUUID().toString();

        OrkesWorkflowService.StartWorkflowRequest.Builder requestBuilder =
                OrkesWorkflowService.StartWorkflowRequest.newBuilder();
        requestBuilder.setRequestId(requestId).setIdempotencyKey(requestId).setMonitor(true);
        if (waitUntilTask != null) {
            requestBuilder.setWaitUntilTask(waitUntilTask);
        }
        requestBuilder.setRequest(protoMappingHelper.toProto(startWorkflowRequest));
        CompletableFuture future = executionMonitor.monitorRequest(requestId);
        synchronized (requestStream) {
            requestStream.onNext(requestBuilder.build());
        }
        return future;
    }

    public void shutdown() {
        channel.shutdown();
    }
}
