/*
 * Orkes Conductor API Server
 * Orkes Conductor API Server
 *
 * OpenAPI spec version: v2
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package io.orkes.conductor.client.model.integration;

import java.util.List;

import lombok.Data;

@Data
public class IntegrationDef {

    private Category category;
    private String categoryLabel;
    private String description;
    private Boolean enabled;
    private String iconName;
    private String name;
    private List<String> tags;
    private String type;
}
