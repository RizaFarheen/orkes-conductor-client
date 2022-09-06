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

package io.orkes.conductor.client.http.model;

import java.util.Objects;

import com.google.gson.annotations.SerializedName;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * CreateOrUpdateApplicationRequest
 */


public class CreateOrUpdateApplicationRequest {
  @SerializedName("name")
  private String name = null;

  public CreateOrUpdateApplicationRequest name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Application&#x27;s name e.g.: Payment Processors
   * @return name
  **/
  @Schema(required = true, description = "Application's name e.g.: Payment Processors")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateOrUpdateApplicationRequest createOrUpdateApplicationRequest = (CreateOrUpdateApplicationRequest) o;
    return Objects.equals(this.name, createOrUpdateApplicationRequest.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateOrUpdateApplicationRequest {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
