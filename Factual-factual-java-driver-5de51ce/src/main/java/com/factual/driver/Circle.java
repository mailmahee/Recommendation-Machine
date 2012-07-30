package com.factual.driver;

import java.util.HashMap;


/**
 * Represents a geographic sub query confining results to a circle.
 *
 * @author aaron
 */
public class Circle {
  private final double centerLat;
  private final double centerLong;
  private final int meters;


  /**
   * Constructs a geographic Circle representation.
   * 
   * @param centerLat the latitude of the center of this Circle.
   * @param centerLong the longitude of the center of this Circle.
   * @param meters the radius, in meters, of this Circle.
   */
  public Circle(double centerLat, double centerLong, int meters) {
    this.centerLat = centerLat;
    this.centerLong = centerLong;
    this.meters = meters;
  }

  /**
   * View this circle as a json string representation
   * 
   * @return a json string representation of this Circle
   */
  public String toJsonStr() {
    return JsonUtil.toJsonStr(toJsonObject());
  }

  /**
   * View this Circle as an object representation that can be serialized as json
   * 
   * @return an object representation of this circle that can be serialized as json
   */
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  private Object toJsonObject() {
    return new HashMap() {
      {
        put(Constants.CIRCLE, new HashMap() {
          {
            put(Constants.CENTER, new double[]{centerLat, centerLong});
            put(Constants.METERS, meters);
          }
        });
      }
    };
  }

  @Override
  public String toString() {
    return toJsonStr();
  }

}
