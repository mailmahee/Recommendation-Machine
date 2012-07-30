package com.factual.driver;

import java.util.HashMap;

public class FieldFilter implements Filter {
  private final String fieldName;
  private final String op;
  private final Object arg;


  public FieldFilter(String op, String fieldName, Object arg) {
    this.op = op;
    this.fieldName = fieldName;
    this.arg = arg;
  }

  /**
   * Produces JSON representation of the represented filter logic.
   * <p>
   * For example:
   * <pre>
   * {"first": {"$eq":"Jack"}}
   * {"first": {"$in":["a", "b", "c"]}}
   * </pre>
   */
  @Override
  public String toJsonStr() {
    return JsonUtil.toJsonStr(toJsonObject());
  }

  /**
   * View this FieldFilter as an object representation that can be serialized as json
   * 
   * @return an object representation of this field filter that can be serialized as json
   */
  @SuppressWarnings({ "rawtypes", "unchecked", "serial" })
  @Override
  public Object toJsonObject() {
	  return new HashMap() {
	    	{
	    		put(fieldName, new HashMap() {
	    			{
	    				put(op, arg);
	    			}
	    		});
	    	}
	  };
  }

}
