package com.factual.driver;

import java.util.Map;

/**
 * Represents an add or update submission to a Factual row.
 * 
 * @author brandon
 */
public class Submit {

	/**
	 * Holds all parameters for this Submit.
	 */
	private final Parameters queryParams = new Parameters();

	/**
	 * Constructor for submit.
	 */
	public Submit() {
	}

	/**
	 * Constructor for a submit with values initialized as key value pairs in mapping.
	 * 
	 * @param values values this submit is initialized with
	 */
	public Submit(Map<String, Object> values) {
		for (String key : values.keySet())
			setValue(key, values.get(key));
	}

	protected String toUrlQuery() {
		return UrlUtil.toUrlQuery(toUrlParams());
	}

	/**
	 * Set the value for a single field in this submit request.  
	 * Added to a JSON hash of field names and values to be added to a Factual table.
	 * @param field the field name
	 * @param value the value for the specified field
	 * @return this Submit
	 */
	public Submit setValue(String field, Object value) {
		queryParams.setJsonMapParam(Constants.SUBMIT_VALUES, field, value);
		return this;
	}

	/**
	 * Set the value to null for a single field in this submit request.
	 * @param field the field to set as empty, or null.
	 * @return this Submit
	 */
	public Submit removeValue(String field) {
		setValue(field, null);
		return this;
	}

	protected Map<String, Object> toUrlParams() {
		return queryParams.toUrlParams(null);
	}
	
}
