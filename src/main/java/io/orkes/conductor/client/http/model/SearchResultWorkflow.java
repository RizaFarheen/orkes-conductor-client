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

import java.util.ArrayList;
import java.util.List;
/**
 * SearchResultWorkflow
 */


public class SearchResultWorkflow {
  @SerializedName("results")
  private List<Workflow> results = null;

  @SerializedName("totalHits")
  private Long totalHits = null;

  public SearchResultWorkflow results(List<Workflow> results) {
    this.results = results;
    return this;
  }

  public SearchResultWorkflow addResultsItem(Workflow resultsItem) {
    if (this.results == null) {
      this.results = new ArrayList<Workflow>();
    }
    this.results.add(resultsItem);
    return this;
  }

   /**
   * Get results
   * @return results
  **/
  @Schema(description = "")
  public List<Workflow> getResults() {
    return results;
  }

  public void setResults(List<Workflow> results) {
    this.results = results;
  }

  public SearchResultWorkflow totalHits(Long totalHits) {
    this.totalHits = totalHits;
    return this;
  }

   /**
   * Get totalHits
   * @return totalHits
  **/
  @Schema(description = "")
  public Long getTotalHits() {
    return totalHits;
  }

  public void setTotalHits(Long totalHits) {
    this.totalHits = totalHits;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SearchResultWorkflow searchResultWorkflow = (SearchResultWorkflow) o;
    return Objects.equals(this.results, searchResultWorkflow.results) &&
        Objects.equals(this.totalHits, searchResultWorkflow.totalHits);
  }

  @Override
  public int hashCode() {
    return Objects.hash(results, totalHits);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SearchResultWorkflow {\n");
    
    sb.append("    results: ").append(toIndentedString(results)).append("\n");
    sb.append("    totalHits: ").append(toIndentedString(totalHits)).append("\n");
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
