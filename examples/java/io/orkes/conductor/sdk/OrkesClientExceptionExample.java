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
package io.orkes.conductor.sdk;

import io.orkes.conductor.client.MetadataClient;
import io.orkes.conductor.client.OrkesClientException;
import io.orkes.conductor.client.OrkesClients;

/**
 * Example demonstrating how to catch client exception and extract the underlying status code and
 * message
 */
public class OrkesClientExceptionExample {

    public static void main(String a[]) {

        OrkesClients orkesClients = ApiUtil.getOrkesClient();
        MetadataClient metadataClient = orkesClients.getMetadataClient();
        try {
            metadataClient.getTags();
        } catch (OrkesClientException e) {
            System.out.println("Status " + e.getCode() + " Message " + e.getMessage());
        }
    }
}
