# Writing Workers

A Workflow task represents a unit of business logic that achieves a specific goal, such as checking inventory, initiating payment transfer, etc. A worker implements a task in the workflow. 

## Content

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [Implementing Workers](#implementing-workers)
  - [Managing Workers in Application](#managing-workers-in-application)
- [Design Principles for Workers](#design-principles-for-workers)
- [System Task Workers](#system-task-workers)
  - [Wait Task](#wait-task)
    - [Using Code to Create Wait Task](#using-code-to-create-wait-task)
    - [JSON Configuration](#json-configuration)
  - [HTTP Task](#http-task)
    - [Using Code to Create HTTP Task](#using-code-to-create-http-task)
    - [JSON Configuration](#json-configuration-1)
  - [Javascript Executor Task](#javascript-executor-task)
    - [Using Code to Create Inline Task](#using-code-to-create-inline-task)
    - [JSON Configuration](#json-configuration-2)
  - [JSON Processing using JQ](#json-processing-using-jq)
    - [Using Code to Create JSON JQ Transform Task](#using-code-to-create-json-jq-transform-task)
    - [JSON Configuration](#json-configuration-3)
- [Worker vs. Microservice/HTTP Endpoints](#worker-vs-microservicehttp-endpoints)
- [Deploying Workers in Production](#deploying-workers-in-production)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Implementing Workers

The workers can be implemented by writing a simple Java function and annotating the function with the `@WorkerTask`. Conductor workers are services (similar to microservices) that follow the [Single Responsibility Principle](https://en.wikipedia.org/wiki/Single_responsibility_principle).

Workers can be hosted along with the workflow or run in a distributed environment where a single workflow uses workers deployed and running in different machines/VMs/containers. Keeping all the workers in the same application or running them as a distributed application is a design and architectural choice. Conductor is well suited for both kinds of scenarios.

You can create or convert any existing Java function to a distributed worker by adding `@WorkerTask` annotation to it. Here is a simple worker that takes name as input and returns greetings:

```java
package io.orkes.helloworld;

import com.netflix.conductor.sdk.workflow.task.InputParam;
import com.netflix.conductor.sdk.workflow.task.WorkerTask;

public class ConductorWorkers {

    @WorkerTask("greet")
    public String greet(@InputParam("name") String name) {
        return "Hello " + name;
    }

}
```

### Managing Workers in Application

Workers use a polling mechanism (with a long poll) to check for any available tasks from the server periodically. The startup and shutdown of workers are handled by the  `conductor.client.automator.TaskRunnerConfigurer` class.

```java
WorkflowExecutor executor = new WorkflowExecutor("http://server/api/");
/*List of packages  (comma separated) to scan for annotated workers.  
  Please note the worker method MUST be public, and the class in which they are defined
  MUST have a no-args constructor*/       
executor.initWorkers("com.company.package1,com.company.package2");
```

## Design Principles for Workers

Each worker embodies the design pattern and follows certain basic principles:

1. Workers are stateless and do not implement a workflow-specific logic.
2. Each worker executes a particular task and produces well-defined output given specific inputs.
3. Workers are meant to be idempotent (Should handle cases where the partially executed task, due to timeouts, etc, gets rescheduled).
4. Workers do not implement the logic to handle retries, etc., that the Conductor server takes care of.

## System Task Workers

A system task worker is a pre-built, general-purpose worker in your Conductor server distribution.

System tasks automate repeated tasks such as calling an HTTP endpoint, executing lightweight ECMA-compliant javascript code, publishing to an event broker, etc.

### Wait Task

> [!tip]
> Wait is a powerful way to have your system wait for a specific trigger, such as an external event, a particular date/time, or duration, such as 2 hours, without having to manage threads, background processes, or jobs.

#### Using Code to Create Wait Task

```java
import com.netflix.conductor.sdk.workflow.def.tasks.Wait;
/* Wait for a specific duration */
Wait waitTask = new Wait("wait_for_2_sec",Duration.ofMillis(1000));
/* Wait using Datetime */
ZonedDateTime zone = ZonedDateTime.parse("2020-10-05T08:20:10+05:30[Asia/Kolkata]");
Wait waitTask = new Wait("wait_till_2days",zone);
workflow.add(waitTask);//workflow is an object of ConductorWorkflow<WorkflowInput>
```

#### JSON Configuration

```json
{
  "name": "wait",
  "taskReferenceName": "wait_till_jan_end",
  "type": "WAIT",
  "inputParameters": {
    "until": "2024-01-31 00:00 UTC"
  }
}
```

### HTTP Task

Make a request to an HTTP(S) endpoint. The task allows for GET, PUT, POST, DELETE, HEAD, and PATCH requests.

#### Using Code to Create HTTP Task

```java
import com.netflix.conductor.sdk.workflow.def.tasks.Http;
Http httptask = new Http("mytask");
httptask.url("http://worldtimeapi.org/api/timezone/Asia/Kolkata");
workflow.add(httptask);//workflow is an object of ConductorWorkflow<WorkflowInput>
```

#### JSON Configuration

```json
{
  "name": "http_task",
  "taskReferenceName": "http_task_ref",
  "type" : "HTTP",
  "uri": "https://orkes-api-tester.orkesconductor.com/api",
  "method": "GET"
}
```

### Javascript Executor Task

Execute ECMA-compliant JavaScript code. It is useful when writing a script for data mapping, calculations, etc.

#### Using Code to Create Inline Task

```java
import com.netflix.conductor.sdk.workflow.def.tasks.Javascript;
Javascript jstask = new Javascript("hello_script",
                  """function greetings(name) {
                     return {
                        "text": "hello " + name
                            }
                      }
                    greetings("Orkes");""");
workflow.add(jstask);
```

#### JSON Configuration

```json
{
  "name": "inline_task",
  "taskReferenceName": "inline_task_ref",
  "type": "INLINE",
  "inputParameters": {
    "expression": " function greetings() {\n return {\n     \"text\": \"hello \" + $.name\n        }\n    }\n    greetings();",
    "evaluatorType": "graaljs",
    "name": "${workflow.input.name}"
  }
}
```

### JSON Processing using JQ

[Jq](https://jqlang.github.io/jq/) is like sed for JSON data - you can slice, filter, map, and transform structured data with the same ease that sed, awk, grep, and friends let you play with text.

#### Using Code to Create JSON JQ Transform Task

```java
import com.netflix.conductor.sdk.workflow.def.tasks.JQ;

JQ jqtask = new JQ("jq_task", "{ key3: (.key1.value1 + .key2.value2) }");
workflow.add(jqtask);
```

#### JSON Configuration

```json
{
  "name": "json_transform_task",
  "taskReferenceName": "json_transform_task_ref",
  "type": "JSON_JQ_TRANSFORM",
  "inputParameters": {
    "key1": "k1",        
    "key2": "k2",
    "queryExpression": "{ key3: (.key1.value1 + .key2.value2) }",
  }
}
```

## Worker vs. Microservice/HTTP Endpoints

> [!tip] 
> Workers are a lightweight alternative to exposing an HTTP endpoint and orchestrating using HTTP tasks. They are recommended if you do not need to expose the service over HTTP or gRPC endpoints.

There are several advantages to this approach:

1. **No need for an API management layer**: Given there are no exposed endpoints and workers are self-load-balancing.
2. **Reduced infrastructure footprint**: No need for an API gateway/load balancer.
3. Workers initiate all communication using polling, avoiding the need to open any incoming TCP ports.
4. Workers **self-regulate** when busy; they only poll as much as they can handle. Backpressure handling is done out of the box.
5. Workers can be scaled up / down quickly based on the demand by increasing the number of processes.

## Deploying Workers in Production

Conductor workers can run in the cloud-native environment or on-prem and can easily be deployed like any other Java application. Workers can run a containerized environment, VMs, or bare metal like you would deploy your other Java applications.
