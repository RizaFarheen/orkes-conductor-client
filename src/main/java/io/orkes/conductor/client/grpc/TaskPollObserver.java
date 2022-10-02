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
package io.orkes.conductor.client.grpc;

import java.util.concurrent.ThreadPoolExecutor;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.netflix.conductor.grpc.ProtoMapper;
import com.netflix.conductor.grpc.TaskServiceGrpc;
import com.netflix.conductor.grpc.TaskServicePb;
import com.netflix.conductor.proto.TaskPb;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskPollObserver implements StreamObserver<TaskPb.Task> {

    private final ProtoMapper protoMapper = ProtoMapper.INSTANCE;

    private final Worker worker;

    private final ThreadPoolExecutor executor;

    private final TaskServiceGrpc.TaskServiceBlockingStub stub;

    public TaskPollObserver(
            Worker worker,
            ThreadPoolExecutor executor,
            TaskServiceGrpc.TaskServiceBlockingStub stub) {
        this.worker = worker;
        this.executor = executor;
        this.stub = stub;
    }

    @Override
    public void onNext(TaskPb.Task task) {
        executor.execute(
                () -> {
                    try {
                        TaskResult result = worker.execute(protoMapper.fromProto(task));
                        updateTask(result);
                    } catch (Exception e) {
                        log.error("Error executing task: {}", e.getMessage(), e);
                    }
                });
    }

    @Override
    public void onError(Throwable t) {
        log.error(t.getMessage(), t);
    }

    @Override
    public void onCompleted() {}

    public void updateTask(TaskResult taskResult) {
        stub.updateTask(
                TaskServicePb.UpdateTaskRequest.newBuilder()
                        .setResult(protoMapper.toProto(taskResult))
                        .build());
    }
}