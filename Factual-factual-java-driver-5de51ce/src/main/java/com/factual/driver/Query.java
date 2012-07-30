package com.factual.driver;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

/**
 * Represents a top level Factual query. Knows how to represent the query as URL
 * encoded key value pairs, ready for the query string in a GET request. (See
 * {@link #toUrlQuery()})
 * 
 * @author aaron
 */
public class Query implements Filterable {

  private boolean includeRowCount;

  /**
   * Holds all parameters for this Query.
   */
  protected final Parameters queryParams = new Parameters();

  /**
   * Sets a full text search query. Factual will use this value to perform a
   * full text search against various attributes of the underlying table, such
   * as entity name, address, etc.
   * 
   * @param term
   *          the text for which to perform a full text search.
   * @return this Query
   */
  public Query search(String term) {
    addParam(Constants.SEARCH, term);
    return this;
  }

  /**
   * Sets the maximum amount of records to return from this Query.
   * @param limit the maximum amount of records to return from this Query.
   * @return this Query
   */
  public Query limit(long limit) {
    addParam(Constants.QUERY_LIMIT, (limit > 0 ? limit : null));
    return this;
  }

  /**
   * Sets the fields to select. This is optional; default behaviour is generally
   * to select all fields in the schema.
   * 
   * @param fields
   *          the fields to select.
   * @return this Query
   */
  public Query only(String... fields) {
    for (String field : fields) {
      queryParams.addCommaSeparatedParam(Constants.QUERY_SELECT, field);
    }
    return this;
  }

  /**
   * @return array of select fields set by only(), null if none.
   */
  public String[] getSelectFields() {
    return queryParams.getCommaSeparatedParam(Constants.QUERY_SELECT);
  }

  /**
   * Sets this Query to sort field in ascending order.
   * 
   * @param field
   *          the field to sort in ascending order.
   * @return this Query
   */
  public Query sortAsc(String field) {
    queryParams.addCommaSeparatedParam(Constants.QUERY_SORT, field + ":asc");
    return this;
  }

  /**
   * Sets this Query to sort field in descending order.
   * 
   * @param field
   *          the field to sort in descending order.
   * @return this Query
   */
  public Query sortDesc(String field) {
    queryParams.addCommaSeparatedParam(Constants.QUERY_SORT, field + ":desc");
    return this;
  }

  /**
   * Sets how many records in to start getting results (i.e., the page offset)
   * for this Query.
   * 
   * @param offset
   *          the page offset for this Query.
   * @return this Query
   */
  public Query offset(long offset) {
    addParam(Constants.QUERY_OFFSET, (offset > 0 ? offset : null));
    return this;
  }

  /**
   * The response will include a count of the total number of rows in the table
   * that conform to the request based on included filters. This will increase
   * the time required to return a response. The default behavior is to NOT
   * include a row count.
   * 
   * @return this Query, marked to return total row count when run.
   */
  public Query includeRowCount() {
    return includeRowCount(true);
  }

  /**
   * When true, the response will include a count of the total number of rows in
   * the table that conform to the request based on included filters.
   * Requesting the row count will increase the time required to return a
   * response. The default behavior is to NOT include a row count.
   * 
   * @param includeRowCount
   *          true if you want the results to include a count of the total
   *          number of rows in the table that conform to the request based on
   *          included filters.
   * @return this Query.
   */
  public Query includeRowCount(boolean includeRowCount) {
    this.includeRowCount = includeRowCount;
    return this;
  }

  /**
   * Begins construction of a new row filter.
   * 
   * @param field
   *          the name of the field on which to filter.
   * @return A partial representation of the new row filter.
   * @deprecated use {@link #field(String)}
   */
  @Deprecated
  public QueryBuilder<Query> criteria(String field) {
    return new QueryBuilder<Query>(this, field);
  }

  /**
   * Begins construction of a new row filter for this Query.
   * 
   * @param field
   *          the name of the field on which to filter.
   * @return A partial representation of the new row filter.
   */
  public QueryBuilder<Query> field(String field) {
    return new QueryBuilder<Query>(this, field);
  }

  /**
   * Adds a filter so that results can only be (roughly) within the specified
   * geographic circle.
   * 
   * @param circle The circle within which to bound the results.
   * @return this Query.
   */
  public Query within(Circle circle) {
    queryParams.setParam(Constants.FILTER_GEO, circle);
    return this;
  }

  /**
   * Used to nest AND'ed predicates.
   */
  public Query and(Query... queries) {
    queryParams.popFilters(Constants.FILTER_AND, queries);
    return this;
  }

  /**
   * Used to nest OR'ed predicates.
   */
  public Query or(Query... queries) {
    queryParams.popFilters(Constants.FILTER_OR, queries);
    return this;
  }

  /**
   * Adds <tt>filter</tt> to this Query.
   */
  @Override
  public void add(Filter filter) {
    queryParams.add(filter);
  }

  /**
   * Set a parameter and value pair for specifying url parameters, specifically those not yet available as convenience methods.
   * @param key the field name of the parameter to add
   * @param value the field value that will be serialized using value.toString()
   * @return this Query
   */
  private Query addParam(String key, Object value) {
    queryParams.setParam(key, value);
    return this;
  }

  /**
   * Builds and returns the query string to represent this Query when talking to
   * Factual's API. Provides proper URL encoding and escaping.
   * <p>
   * Example output:
   * <pre>
   * filters=%7B%22%24and%22%3A%5B%7B%22region%22%3A%7B%22%24in%22%3A%5B%22MA%22%2C%22VT%22%2C%22NH%22%5D%7D%7D%2C%7B%22%24or%22%3A%5B%7B%22first_name%22%3A%7B%22%24eq%22%3A%22Chun%22%7D%7D%2C%7B%22last_name%22%3A%7B%22%24eq%22%3A%22Kok%22%7D%7D%5D%7D%5D%7D
   * </pre>
   * <p>
   * (After decoding, the above example would be used by the server as:)
   * <pre>
   * filters={"$and":[{"region":{"$in":["MA","VT","NH"]}},{"$or":[{"first_name":{"$eq":"Chun"}},{"last_name":{"$eq":"Kok"}}]}]}
   * </pre>
   * 
   * @return the query string to represent this Query when talking to Factual's
   *         API.
   */
  protected Map<String, Object> toUrlParams() {
    Parameters additional = null;
    if (includeRowCount) {
      additional = new Parameters();
      additional.setParam(Constants.INCLUDE_COUNT,true);
    }
    return queryParams.toUrlParams(additional);
  }

  @Override
  public String toString() {
    try {
      return URLDecoder.decode(UrlUtil.toUrlQuery(toUrlParams()), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<Filter> getFilterList() {
    return queryParams.getFilterList();
  }

  public String toUrlQuery() {
    return UrlUtil.toUrlQuery(toUrlParams());
  }

}