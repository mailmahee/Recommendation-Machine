package com.factual.driver;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;



/**
 * Represents the response from running a fetch request against Factual, such as
 * a geolocation based query for specific places entities.
 *
 * @author aaron
 */
public class ReadResponse extends Response implements Tabular {
  private final String json;
  private List<Map<String, Object>> data = Lists.newArrayList();


  /**
   * Constructor, parses from a JSON response String.
   * 
   * @param json the JSON response String returned by Factual.
   */
  public ReadResponse(String json) {
    this.json = json;
    try{
      JSONObject rootJsonObj = new JSONObject(json);
      Response.withMeta(this, rootJsonObj);
      data = JsonUtil.data(rootJsonObj.getJSONObject(Constants.RESPONSE).getJSONArray(Constants.QUERY_DATA));
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @return The full JSON response from Factual
   */
  @Override
  public String getJson() {
    return json;
  }

  /**
   * @return the first data record or null if no data was returned.
   */
  public Map<String, Object> first() {
    if(data.isEmpty()) {
      return null;
    } else {
      return data.get(0);
    }
  }

  /**
   * An ordered collection of the main data returned by Factual. Represented as
   * Maps, where each Map is a record in the results.
   * 
   * @return the main data returned by Factual.
   */
  @Override
  public List<Map<String, Object>> getData() {
    return data;
  }

  /**
   * @return the size of the result set
   */
  public int size() {
    return data.size();
  }

  /**
   * @return a Collection of all String values found in this Response's data
   *         rows as the <tt>field</tt> attribute.
   */
  public Collection<String> mapStrings(final String field) {
    return Collections2.transform(data, new Function<Map<String, Object>, String>() {
      @Override
      public String apply(Map<String, Object> row) {
        Object val = row.get(field);
        return val != null ? val.toString() : null;
      }});
  }

  //TODO: Bradley: "mapToStrings, mapToDoubles, etc."

}
