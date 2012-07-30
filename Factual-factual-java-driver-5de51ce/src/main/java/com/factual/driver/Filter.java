package com.factual.driver;

public interface Filter {

  /**
   * View this object as a json string representation
   * 
   * @return a json string representation of this object
   */
  String toJsonStr();
  
  /**
   * View this as an object representation that can be serialized as json
   * 
   * @return an object representation that can be serialized as json
   */
  Object toJsonObject();
}
