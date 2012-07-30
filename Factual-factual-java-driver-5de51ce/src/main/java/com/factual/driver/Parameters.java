package com.factual.driver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

/**
 * Internal.  Holds a mapping between parameters and their values for serialization to a URL-encoded string.
 * @author brandon
 *
 */
public class Parameters {

	/**
	 * Holds key-value parameter pairs
	 */
	private Map<Object, Object> params = Maps.newHashMap();

	public Parameters() {
	}
	
	public Parameters(Map<Object, Object> params) {
		this.params = params;
	}
	
	protected Parameters copy() {
		return new Parameters(Maps.newHashMap(params));
	}

	protected Object getParam(String key) {
		return params.get(key);
	}

	protected boolean containsParam(String key) {
		return params.containsKey(key);
	}

	/**
	 * Set a parameter where the value is a data structure that will be serialized in json format
	 * @param key
	 * @param value
	 */
	protected void setJsonParam(String key, Object value) {
		params.put(key, new JsonData<Object>(value));
	}

	/**
	 * Set a parameter where the value will be serialized using value.toString()
	 * @param key
	 * @param value
	 */
	protected void setParam(String key, Object value) {
		params.put(key, new SimpleData(value));
	}
	
	protected Map<String, Object> toUrlParams() {
		return toUrlParams(null);
	}
	
	protected Map<String, Object> toUrlParams(Parameters additionalParams) {
		Map<String, Object> paramMap = Maps.newHashMap();
		for (Entry<Object, Object> entry : params.entrySet()) {
			paramMap.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
		}
		if (additionalParams != null)
			paramMap.putAll(additionalParams.toUrlParams(null));	
		return paramMap;
	}

	/**
	 * Convenience method for adding comma separated parameters to a field.
	 * Example: name,region,address
	 */
	protected void addCommaSeparatedParam(String key, Object value) {
		if (!params.containsKey(key) || !(params.get(key) instanceof CommaSeparatedData))
			params.put(key, new CommaSeparatedData());
		((CommaSeparatedData) params.get(key)).add(value);
	}

	protected String[] getCommaSeparatedParam(String key) {
		if (!containsParam(key) || !(getParam(key) instanceof CommaSeparatedData))
			return null;
	    return ((CommaSeparatedData) getParam(key)).toArray(new String[]{});
	}

	protected void setJsonMapParam(String key, String field, Object value) {
		if (!containsParam(key) || !(getParam(key) instanceof JsonData))
			setJsonParam(key, Maps.newHashMap());
		((JsonData<Map>) getParam(key)).getValue().put(field, value);
	}
	
    private List<Filter> getFilterList(Filterable q) {
        return q.getFilterList();
    }
	  
	protected List<Filter> getFilterList() {
	    Object filter = getParam(Constants.FILTERS);
	    if (filter instanceof SimpleData &&
	    	((SimpleData) filter).getData() instanceof FilterList)
	    	return (List<Filter>) ((SimpleData) filter).getData();
	    return null;
	}
	  
	/**
	 * Pops the newest Filter from each of <tt>queries</tt>,
	 * grouping each popped Filter into one new FilterGroup.
	 * Adds that new FilterGroup as the newest Filter in this
	 * Query.
	 * <p>
	 * The FilterGroup's logic will be determined by <tt>op</tt>.
	 */
	protected void popFilters(String op, Filterable... queries) {
		FilterGroup group = new FilterGroup().op(op);
		for(Filterable q : queries) {
			List<Filter> list = getFilterList(q);
			if (list != null)
				if (!list.isEmpty())
					group.add(list.remove(list.size()-1));
	    }
	    add(group);
	}

	/**
	 * Adds <tt>filter</tt> to this Query.
	 */
	protected void add(Filter filter) {
		if (!containsParam(Constants.FILTERS)
				|| !(getParam(Constants.FILTERS) instanceof SimpleData)
				|| !(((SimpleData) getParam(Constants.FILTERS)).getData() instanceof FilterList))
			setParam(Constants.FILTERS, new FilterList());
		getFilterList().add(filter);
	}

	/**
	 * Holds a filter list parameter value representation. Implicit top-level AND.
	 */
	private static class FilterList extends ArrayList<Filter> {
		public FilterList() {
		}

		@Override
		public String toString() {
			if (isEmpty()) {
				return null;
			} else if (size() == 1) {
				return get(0).toJsonStr();
			} else {
				return new FilterGroup(this).toJsonStr();
			}
		}

		public Object toJsonObject() {
			if (isEmpty()) {
				return null;
			} else if (size() == 1) {
				return get(0).toJsonObject();
			} else {
				return new FilterGroup(this).toJsonObject();
			}
		}
	}
	
	/**
	 * Holds a comma-separated parameter value representation
	 */
	private static class CommaSeparatedData extends HashSet<Object> {
		public CommaSeparatedData() {
		}

		@Override
		public String toString() {
			return Joiner.on(",").skipNulls().join(this);
		}

		public Object toJsonObject() {
			return toString();
		}
	}

	/**
	 * Holds a parameter value representation which is serializable to json
	 * @param <T>
	 */
	private static class JsonData<T> {
		private T value = null;

		public JsonData(T value) {
			this.value = value;
		}

		public T getValue() {
			return value;
		}

		@Override
		public String toString() {
			return JsonUtil.toJsonStr(value);
		}

		public Object toJsonObject() {
			return value;
		}
	}

	/**
	 * Holds a parameter value which directly uses data.toString(), performing no transformation.
	 */
	private static class SimpleData {
		private Object data = null;

		public SimpleData(Object data) {
			this.data = data;
		}

		@Override
		public String toString() {
			return String.valueOf(data);
		}

		public Object toJsonObject() {
			return toString();
		}

		public Object getData() {
			return data;
		}
	}

}
