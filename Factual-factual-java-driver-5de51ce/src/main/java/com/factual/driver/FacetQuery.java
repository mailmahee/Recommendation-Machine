package com.factual.driver;

import java.util.List;
import java.util.Map;

/**
 * Represents a top level Factual facet query. Knows how to represent the facet
 * query as URL encoded key value pairs, ready for the query string in a GET
 * request. (See {@link #toUrlQuery()})
 * 
 * @author brandon
 */
public class FacetQuery implements Filterable {

  private boolean includeRowCount;

  /**
   * Constructor.
   * @param fields fields for which facets will be generated
   */
  public FacetQuery(String... fields) {
    select(fields);
  }

  /**
   * Holds all parameters for this FacetQuery.
   */
  private final Parameters queryParams = new Parameters();

  protected Map<String, Object> toUrlParams() {
    Parameters additional = null;
    if (includeRowCount) {
      additional = new Parameters();
      additional.setParam(Constants.INCLUDE_COUNT,true);
    }
    return queryParams.toUrlParams(additional);
  }

  /**
   * Set a parameter and value pair for specifying url parameters, specifically those not yet available as convenience methods.
   * @param key the field name of the parameter to add
   * @param value the field value that will be serialized using value.toString()
   * @return this FacetQuery
   */
  private FacetQuery addParam(String key, Object value) {
    queryParams.setParam(key, value);
    return this;
  }

  /**
   * For each facet value count, the minimum number of results it must have in order to be returned in the response. Must be zero or greater. The default is 1.
   * @param minCount for each facet value count, the minimum number of results it must have in order to be returned in the response. Must be zero or greater. The default is 1.
   * 
   * @return this FacetQuery
   */
  public FacetQuery minCountPerFacetValue(long minCount) {
    addParam(Constants.FACET_MIN_COUNT_PER_FACET_VALUE, minCount);
    return this;
  }

  /**
   * The maximum number of unique facet values that can be returned for a single field. Range is 1-250. The default is 25.
   * @param maxValuesPerFacet the maximum number of unique facet values that can be returned for a single field. Range is 1-250. The default is 25.
   * @return this FacetQuery
   */
  public FacetQuery maxValuesPerFacet(long maxValuesPerFacet) {
    addParam(Constants.FACET_MAX_VALUES_PER_FACET, maxValuesPerFacet);
    return this;
  }

  /**
   * The fields for which facets should be generated. The response will not be ordered identically to this list, nor will it reflect any nested relationships between fields.
   * @param fields the fields for which facets should be generated. The response will not be ordered identically to this list, nor will it reflect any nested relationships between fields.
   * @return this FacetQuery
   */
  private FacetQuery select(String... fields) {
    for (String field : fields) {
      queryParams.addCommaSeparatedParam(Constants.FACET_SELECT, field);
    }
    return this;
  }

  /**
   * Sets a full text search query. Factual will use this value to perform a
   * full text search against various attributes of the underlying table, such
   * as entity name, address, etc.
   * 
   * @param term
   *          the text for which to perform a full text search.
   * @return this FacetQuery
   */
  public FacetQuery search(String term) {
    addParam(Constants.SEARCH, term);
    return this;
  }

  /**
   * Begins construction of a new row filter for this FacetQuery.
   * 
   * @param field
   *            the name of the field on which to filter.
   * @return A partial representation of the new row filter.
   */
  public QueryBuilder<FacetQuery> field(String field) {
    return new QueryBuilder<FacetQuery>(this, field);
  }

  /**
   * Adds a filter so that results can only be (roughly) within the specified
   * geographic circle.
   * 
   * @param circle
   *            The circle within which to bound the results.
   * @return this FacetQuery.
   */
  public FacetQuery within(Circle circle) {
    queryParams.setParam(Constants.FILTER_GEO, circle);
    return this;
  }

  /**
   * Used to nest AND'ed predicates.
   */
  public FacetQuery and(FacetQuery... queries) {
    queryParams.popFilters(Constants.FILTER_AND, queries);
    return this;
  }

  /**
   * Used to nest OR'ed predicates.
   */
  public FacetQuery or(FacetQuery... queries) {
    queryParams.popFilters(Constants.FILTER_OR, queries);
    return this;
  }

  /**
   * Adds <tt>filter</tt> to this FacetQuery.
   */
  @Override
  public void add(Filter filter) {
    queryParams.add(filter);
  }

  /**
   * The response will include a count of the total number of rows in the
   * table that conform to the request based on included filters. This will
   * increase the time required to return a response. The default behavior is
   * to NOT include a row count.
   * 
   * @return this FacetQuery, marked to return total row count when run.
   */
  public FacetQuery includeRowCount() {
    return includeRowCount(true);
  }

  /**
   * When true, the response will include a count of the total number of rows
   * in the table that conform to the request based on included filters.
   * Requesting the row count will increase the time required to return a
   * response. The default behavior is to NOT include a row count.
   * 
   * @param includeRowCount
   *            true if you want the results to include a count of the total
   *            number of rows in the table that conform to the request based
   *            on included filters.
   * @return this FacetQuery.
   */
  public FacetQuery includeRowCount(boolean includeRowCount) {
    this.includeRowCount = includeRowCount;
    return this;
  }

  @Override
  public List<Filter> getFilterList() {
    return queryParams.getFilterList();
  }

  public String toUrlQuery() {
    return UrlUtil.toUrlQuery(toUrlParams());
  }
}
