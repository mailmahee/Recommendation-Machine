package com.factual.driver;

import java.util.Map;

/**
 * 
 * Represents a Geopulse query against Factual
 * 
 * @author brandon
 *
 */
public class Geopulse {

  /**
   * Holds all parameters for this Geopulse.
   */
  protected final Parameters queryParams = new Parameters();

  /**
   * Represents a Geopulse query against Factual
   * 
   * @param point The geographic point at which this geopulse request is executed.
   */
  public Geopulse(Point point) {
	queryParams.setParam(Constants.FILTER_GEO, point);
  }
  
  protected Map<String, Object> toUrlParams() {
	return queryParams.toUrlParams(null);
  }
  
  protected String toUrlQuery() {
	return UrlUtil.toUrlQuery(toUrlParams());
  }
	
  /**
   * Sets the fields to select. This is optional; default behaviour is generally
   * to select all fields in the schema.
   * 
   * @param fields
   *          the fields to select.
   * @return this Geopulse
   */
  public Geopulse only(String... fields) {
	for (String field : fields) {
		queryParams.addCommaSeparatedParam(Constants.QUERY_SELECT, field);
	}
    return this;
  }
}
