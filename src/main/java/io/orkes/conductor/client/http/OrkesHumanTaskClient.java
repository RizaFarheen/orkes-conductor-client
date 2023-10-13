/*
 * Copyright 2023 Orkes, Inc.
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
package io.orkes.conductor.client.http;

import io.orkes.conductor.client.ApiClient;
import io.orkes.conductor.client.HumanTaskClient;
import io.orkes.conductor.client.http.api.HumanTaskApi;
import io.orkes.conductor.client.model.HTScrollableSearchResultHumanTaskEntry;
import io.orkes.conductor.client.model.HumanTaskSearchRequest;

public class OrkesHumanTaskClient extends OrkesClient implements HumanTaskClient {


    private HumanTaskApi humanTaskApi;

    public OrkesHumanTaskClient(ApiClient apiClient) {
        super(apiClient);
        this.humanTaskApi = new HumanTaskApi(apiClient);
    }


    @Override
    public HTScrollableSearchResultHumanTaskEntry searchV2(HumanTaskSearchRequest humanTaskSearchRequest)
            throws ApiException {
        return humanTaskApi.searchV2(humanTaskSearchRequest);
    }
}
