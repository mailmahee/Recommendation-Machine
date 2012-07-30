package com.factual.driver;

import com.google.api.client.http.HttpResponse;

/**
 * Represents an Exception that happened while communicating with Factual.
 * Includes information about the request that triggered the problem.
 * 
 * @author aaron
 */
public class FactualApiException extends RuntimeException {
  private String requestUrl;
  private String requestMethod;
  private HttpResponse response;


  public FactualApiException(Exception e) {
    super(e);
  }

  public FactualApiException(String msg) {
    super(msg);
  }

  public FactualApiException requestUrl(String url) {
    this.requestUrl = url;
    return this;
  }

  public FactualApiException requestMethod(String method) {
    this.requestMethod = method;
    return this;
  }

  public FactualApiException response(HttpResponse response) {
    this.response = response;
    return this;
  }

  /**
   * @return the URL used to make the offending request to Factual.
   */
  public String getRequestUrl() {
    return requestUrl;
  }

  /**
   * @return the HTTP request method used to make the offending request to Factual.
   */
  public String getRequestMethod() {
    return requestMethod;
  }

  /**
   * @return the full HttpResponse object, representing the problematic response
   *         from Factual.
   */
  public HttpResponse getResponse() {
    return response;
  }
}
