package com.factual.driver;

import java.util.HashMap;

/**
 * 
 * Represents a geographical point.
 * 
 * @author brandon
 *
 */
public class Point {
	  private final double latitude;
	  private final double longitude;

	  /**
	   * Constructs a geographic Point representation.
	   * 
	   * @param latitude the latitude of the point.
	   * @param longitude the longitude of the point.
	   */
	  public Point(double latitude, double longitude) {
	    this.latitude = latitude;
	    this.longitude = longitude;
	  }

	  /**
	   * View this point as a json string representation
	   * 
	   * @return a json string representation of this Point
	   */
	  public String toJsonStr() {
	    return JsonUtil.toJsonStr(toJsonObject());
	  }

	  /**
	   * View this Point as an object representation that can be serialized as json
	   * 
	   * @return an object representation of this point that can be serialized as json
	   */
	  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
	  private Object toJsonObject() {
		  return new HashMap() {
			{
				put(Constants.POINT, new double[]{latitude, longitude});
			}
		  };
	  }
	  
	  @Override
	  public String toString() {
		  return toJsonStr();
	  }
}
