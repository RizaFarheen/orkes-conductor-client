package io.orkes.conductor.client;

import java.util.List;
import java.util.Map;

import io.orkes.conductor.client.model.TagObject;
import io.orkes.conductor.client.model.integration.Integration;
import io.orkes.conductor.client.model.integration.IntegrationApi;
import io.orkes.conductor.client.model.integration.IntegrationApiUpdate;
import io.orkes.conductor.client.model.integration.IntegrationUpdate;
import io.orkes.conductor.client.model.integration.ai.PromptTemplate;

public interface IntegrationClient {
    /**
     * Client for managing integrations with external systems. Some examples of integrations are:
     * 1. AI/LLM providers (e.g. OpenAI, HuggingFace)
     * 2. Vector DBs (Pinecone, Weaviate etc.)
     * 3. Kafka
     * 4. Relational databases
     *
     * Integrations are configured as integration -> api with 1->N cardinality.
     * APIs are the underlying resources for an integration and depending on the type of integration they represent underlying resources.
     * Examples:
     *     LLM integrations
     *     The integration specifies the name of the integration unique to your environment, api keys and endpoint used.
     *     APIs are the models (e.g. text-davinci-003, or text-embedding-ada-002)
     *
     *     Vector DB integrations,
     *     The integration represents the cluster, specifies the name of the integration unique to your environment, api keys and endpoint used.
     *     APIs are the indexes (e.g. pinecone) or class (e.g. for weaviate)
     *
     *     Kafka
     *     The integration represents the cluster, specifies the name of the integration unique to your environment, api keys and endpoint used.
     *     APIs are the topics that are configured for use within this kafka cluster
     */

    void associatePromptWithIntegration(String aiIntegration, String modelName, String promptName);

    void deleteIntegrationApi(String apiName, String integrationName);

    void deleteIntegration(String integrationName);

    IntegrationApi getIntegrationApi(String apiName, String integrationName);

    List<IntegrationApi> getIntegrationApis(String integrationName);

    Integration getIntegration(String integrationName);

    List<Integration> getIntegrations(String category, Boolean activeOnly);

    List<PromptTemplate> getPromptsWithIntegration(String aiIntegration, String modelName);

    int getTokenUsageForIntegration(String name, String integrationName);

    Map<String, Integer> getTokenUsageForIntegrationProvider(String name);

    void saveIntegrationApi(String integrationName, String apiName, IntegrationApiUpdate apiDetails);

    void saveIntegration(String integrationName, IntegrationUpdate integrationDetails);

    // Tags
    void deleteTagForIntegrationProvider(List<TagObject> tags, String name);
    void saveTagForIntegrationProvider(List<TagObject> tags, String name);
    List<TagObject> getTagsForIntegrationProvider(String name);
}
