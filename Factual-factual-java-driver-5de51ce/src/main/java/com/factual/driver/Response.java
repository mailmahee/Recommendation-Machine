package com.factual.driver;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Represents the basic concept of a response from Factual.
 *
 * @author aaron
 */
public abstract class Response {
  public static final int UNDEFINED = -1;
  private String version;
  private String status;
  private int totalRowCount = UNDEFINED;
  private int includedRows;


  /**
   * The status returned by the Factual API server, e.g. "ok".
   * 
   * @return status returned by the Factual API server.
   */
  public String getStatus() {
    return status;
  }

  /**
   * The version tag returned by the Factual API server, e.g. "3".
   * 
   * @return the version tag returned by the Factual API server.
   */
  public String getVersion() {
    return version;
  }

  /**
   * @return total underlying row count, or {@link #UNDEFINED} if unknown.
   */
  public int getTotalRowCount() {
    return totalRowCount;
  }

  /**
   * @return amount of result rows returned in this response.
   */
  public int getIncludedRowCount() {
    return includedRows;
  }

  /**
   * @return true if Factual's response did not include any results records for
   *         the query, false otherwise.
   */
  public boolean isEmpty() {
    return includedRows == 0;
  }

  /**
   * Parses response metadata from <tt>rootJsonObj</tt> and adds it to <tt>response</tt>
   * 
   * @param resp the response object to which to add metadata.
   * @param rootJsonObj the top-level JSON response Object built from a Factual response.
   */
  public static void withMeta(Response resp, JSONObject rootJsonObj) {
    try {
      resp.version = rootJsonObj.getString(Constants.VERSION);
      resp.status = rootJsonObj.getString(Constants.STATUS);
      if(rootJsonObj.has(Constants.RESPONSE)) {
    	  JSONObject respJson = rootJsonObj.getJSONObject(Constants.RESPONSE);
          if(respJson.has(Constants.TOTAL_ROW_COUNT)) {
             resp.totalRowCount = respJson.getInt(Constants.TOTAL_ROW_COUNT);
          }
          if(respJson.has(Constants.INCLUDED_ROWS)) {
             resp.includedRows = respJson.getInt(Constants.INCLUDED_ROWS);
          }
      }
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String toString() {
    return getJson();
  }

  /**
   * Subclasses of Response must provide access to the original JSON
   * representation of Factual's response.
   * 
   * @return the original JSON representation of Factual's response.
   */
  public abstract String getJson();

}
