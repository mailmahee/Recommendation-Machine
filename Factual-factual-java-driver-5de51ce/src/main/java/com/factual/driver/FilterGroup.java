package com.factual.driver;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;


/**
 * Represents a group of Filters as one Filter.
 * 
 * @author aaron
 */
public class FilterGroup implements Filter {
  private final List<Filter> filters;
  private String op = Constants.FILTER_AND;


  /**
   * Constructor. Defaults logic to AND.
   */
  public FilterGroup(List<Filter> filters) {
    this.filters = filters;
  }

  /**
   * Constructor. Defaults logic to AND.
   */
  public FilterGroup(Filter... filters) {
    this.filters = Lists.newArrayList();
    for(Filter f : filters) {
      this.filters.add(f);
    }
  }

  /**
   * Sets this FilterGroup's logic, e.g., "$or".
   */
  public FilterGroup op(String op) {
    this.op = op;
    return this;
  }

  /**
   * Sets this FilterGroup's logic to be OR.
   */
  public FilterGroup asOR() {
    return op(Constants.FILTER_OR);
  }

  public void add(Filter filter) {
    filters.add(filter);
  }

  /**
   * Produces JSON representation for this FilterGroup
   * <p>
   * For example:
   * <pre>
   * {"$and":[{"first_name":{"$eq":"Bradley"}},{"region":{"$eq":"CA"}},{"locality":{"$eq":"Los Angeles"}}]}
   * </pre>
   */
  @Override
  public String toJsonStr() {
    return JsonUtil.toJsonStr(toJsonObject());
  }

  private List<Object> logicJsonData() {
    List<Object> logics = Lists.newArrayList();
    for(Filter f : filters) {
      logics.add(f.toJsonObject());
    }
    return logics;
  }

  @SuppressWarnings({ "rawtypes", "unchecked", "serial" })
  @Override
  public Object toJsonObject() {
	  return new HashMap() {
	    	{
	    		put(op, logicJsonData());
	    	}
	  };
  }
  
}
