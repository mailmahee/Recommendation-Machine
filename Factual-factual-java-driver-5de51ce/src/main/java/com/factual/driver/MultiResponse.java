package com.factual.driver;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import com.factual.driver.Factual.RequestImpl;
import com.google.common.collect.Lists;

/**
 * Represents a Factual Multi response.
 * 
 * @author brandon
 *
 */
public class MultiResponse extends Response {
	private String json = null;
	private List<Response> data = Lists.newArrayList();

	private Map<String, RequestImpl> requestMapping = null;
	
	/**
	 * 
	 * @param requestMapping
	 */
	public MultiResponse(Map<String, RequestImpl> requestMapping) {
		this.requestMapping = requestMapping;
	}

	/**
	 * Parses from a json response string
	 * @param json json response string to parse from
	 */
	public void setJson(String json) {
		this.json = json;
		try {
			JSONObject rootJsonObj = new JSONObject(json);
			parseResponse(rootJsonObj);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void parseResponse(JSONObject jo) throws JSONException {
	   data.clear();
	   for (Entry<String, RequestImpl> entry : requestMapping.entrySet()) {
		   String responseJson = jo.getJSONObject(entry.getKey()).toString();
		   RequestImpl query = entry.getValue();	
		   Response resp = query.getResponse(responseJson);
		   if (resp != null)
			   data.add(resp);
	   }
	}

    /**
     * A collection of the responses returned by Factual for a multi query.
     * 
     * @return the multi query data returned by Factual.
     */	
	public List<Response> getData() {
		return data;
	}

	@Override
	public String getJson() {
		return json;
	}
}
