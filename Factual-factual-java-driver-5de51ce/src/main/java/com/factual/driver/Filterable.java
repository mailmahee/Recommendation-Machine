package com.factual.driver;

import java.util.List;

/**
 * Interface for filterable queries
 * @author brandon
 */
public interface Filterable {
	/**
	 * Get the list of filters on a query that supports filtering
	 * @return a list of filters
	 */
	List<Filter> getFilterList();
	
	void add(Filter filter);
}