package com.factual.driver;

/**
 * Represents a Crosswalk record from Factual.
 * 
 * @deprecated No longer in use due to deprecated CrosswalkQuery and
 *             CrosswalkResponse.
 * @author aaron
 */
@Deprecated
public class Crosswalk {
  private String factualId;
  private String url;
  private String namespace;
  private String namespaceId;

  public String getFactualId() {
    return factualId;
  }

  public void setFactualId(String factualId) {
    this.factualId = factualId;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public String getNamespaceId() {
    return namespaceId;
  }

  public void setNamespaceId(String namespaceId) {
    this.namespaceId = namespaceId;
  }

  @Override
  public String toString() {
    return "[Crosswalk: " + "factualId=" + factualId + ", url=" + url
        + ", namespace=" + namespace + ", namespaceId=" + namespaceId + "]";
  }

}
