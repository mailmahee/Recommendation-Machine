package com.factual.driver;

import org.json.JSONException;
import org.json.JSONObject;

public class RawReadResponse extends Response {
  
	private final String json;

  /**
   * Constructor, parses from a JSON response String.
   * 
   * @param json the JSON response String returned by Factual.
   */
  public RawReadResponse(String json) {
    this.json = json;
    try{
      JSONObject rootJsonObj = new JSONObject(json);
      Response.withMeta(this, rootJsonObj);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getJson() {
	return json;
  }

}
