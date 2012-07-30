package com.factual.driver;

import java.util.Map;


public class ResolveQuery {
	
	/**
	 * Holds all parameters for this ResolveQuery.
	 */
	private final Parameters queryParams = new Parameters();

	public ResolveQuery add(String key, Object val) {
		queryParams.setJsonMapParam(Constants.RESOLVE_VALUES, key, val);
		return this;
	}
	
    protected Map<String, Object> toUrlParams() {
		return queryParams.toUrlParams();
	}
    
    protected String toUrlQuery() {
    	return UrlUtil.toUrlQuery(toUrlParams());
    }
}
