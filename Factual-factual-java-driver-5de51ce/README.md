# About

This is the Factual-supported Java driver for [Factual's public API](http://developer.factual.com).

# Installation

## Maven users

The driver is in Maven Central, so you can just add this to your Maven <tt>pom.xml</tt>:

    <dependency>
      <groupId>com.factual</groupId>
      <artifactId>factual-java-driver</artifactId>
      <version>1.5.1</version>
    </dependency>
    
## Non Maven users

You can download the individual driver jar, and view the pom.xml file, here:
[Driver download folder](http://repo1.maven.org/maven2/com/factual/factual-java-driver/1.5.1/)

The pom.xml tells you what dependencies you'll need to plug into your project to get the driver to work (see the <dependencies> section).

# Basic Design

The driver allows you to create an authenticated handle to Factual. With a Factual handle, you can send queries and get results back.

Queries are created using the Query class, which provides a fluent interface to constructing your queries.

Results are returned as the JSON returned by Factual. Optionally, there are JSON parsing conveniences built into the driver.

# Setup

    // Create an authenticated handle to Factual
    Factual factual = new Factual(MY_KEY, MY_SECRET);
    
# Simple Query Example

    // Print 3 random records from Factual's Places table:
    System.out.println(
      factual.fetch("places", new Query().limit(3)));
	
# Full Text Search

    // Print entities that match a full text search for Sushi in Santa Monica:
    System.out.println(
        factual.fetch("places", new Query().search("Sushi Santa Monica")));

# Geo Filters

You can query Factual for entities located within a geographic area. For example:

    // Build a Query that finds entities located within 5000 meters of a latitude, longitude
    new Query().within(new Circle(34.06018, -118.41835, 5000));

# Results sorting

You can have Factual sort your query results for you, on a field by field basis. Simple example:

    // Build a Query to find 10 random entities and sort them by name, ascending:
    new Query().limit(10).sortAsc("name");
    
You can specify more than one sort, and the results will be sorted with the first sort as primary, the second sort or secondary, and so on:

    // Build a Query to find 20 random entities, sorted ascending primarily by region, then by locality, then by name:
    q = new Query()
      .limit(20)
      .sortAsc("region")
      .sortAsc("locality")
      .sortDesc("name");

# Limit and Offset

You can use limit and offset to support basic results paging. For example:

    // Build a Query with offset of 150, limiting the page size to 10:
    new Query().limit(10).offset(150);
	
# Field Selection

By default your queries will return all fields in the table. You can use the only modifier to specify the exact set of fields returned. For example:

    // Build a Query that only gets the name, tel, and category fields:
    new Query().only("name", "tel", "category");
    
# All Top Level Query Parameters

<table>
  <tr>
    <th>Parameter</th>
    <th>Description</th>
    <th>Example</th>
  </tr>
  <tr>
    <td>filters</td>
    <td>Restrict the data returned to conform to specific conditions.</td>
    <td>q.field("name").beginsWith("Starbucks")</td>
  </tr>
  <tr>
    <td>include count</td>
    <td>Include a count of the total number of rows in the dataset that conform to the request based on included filters. Requesting the row count will increase the time required to return a response. The default behavior is to NOT include a row count. When the row count is requested, the Response object will contain a valid total row count via <tt>.getTotalRowCount()</tt>.</td>
    <td><tt>q.includeRowCount()</tt></td>
  </tr>
  <tr>
    <td>geo</td>
    <td>Restrict data to be returned to be within a geographical range based.</td>
    <td>(See the section on Geo Filters)</td>
  </tr>
  <tr>
    <td>limit</td>
    <td>Maximum number of rows to return. Default is 20. The system maximum is 50. For higher limits please contact Factual, however consider requesting a download of the data if your use case is requesting more data in a single query than is required to fulfill a single end-user's request.</td>
    <td><tt>q.limit(10)</tt></td>
  </tr>
  <tr>
    <td>search</td>
    <td>Full text search query string.</td>
    <td>
      Find "sushi":<br><tt>q.search("sushi")</tt><p>
      Find "sushi" or "sashimi":<br><tt>q.search("sushi, sashimi")</tt><p>
      Find "sushi" and "santa" and "monica":<br><tt>q.search("sushi santa monica")</tt>
    </td>
  </tr>
  <tr>
    <td>offset</td>
    <td>Number of rows to skip before returning a page of data. Maximum value is 500 minus any value provided under limit. Default is 0.</td>
    <td><tt>q.offset(150)</tt></td>
  </tr>
  <tr>
    <td>only</td>
    <td>What fields to include in the query results.  Note that the order of fields will not necessarily be preserved in the resulting JSON response due to the nature of JSON hashes.</td>
    <td><tt>q.only("name", "tel", "category")</tt></td>
  </tr>
  <tr>
    <td>sort</td>
    <td>The field (or secondary fields) to sort data on, as well as the direction of sort.  Supports $distance as a sort option if a geo-filter is specified.  Supports $relevance as a sort option if a full text search is specified either using the q parameter or using the $search operator in the filter parameter.  By default, any query with a full text search will be sorted by relevance.  Any query with a geo filter will be sorted by distance from the reference point.  If both a geo filter and full text search are present, the default will be relevance followed by distance.</td>
    <td><tt>q.sortAsc("name").sortDesc("$distance")</tt></td>
  </tr>
</table>  

# Row Filters

The driver supports various row filter logic. Examples:

    // Build a query to find places whose name field starts with "Starbucks"
    new Query().field("name").beginsWith("Starbucks");

    // Build a query to find places with a blank telephone number
    new Query().field("tel").blank();

## Supported row filter logic

<table>
  <tr>
    <th>Predicate</th>
    <th>Description</th>
    <th>Example</th>
  </tr>
  <tr>
    <td>equal</td>
    <td>equal to</td>
    <td><tt>q.field("region").equal("CA")</tt></td>
  </tr>
  <tr>
    <td>notEqual</td>
    <td>not equal to</td>
    <td><tt>q.field("region").notEqual("CA")</tt></td>
  </tr>
  <tr>
    <td>search</td>
    <td>full text search</td>
    <td><tt>q.field("name").search("fried chicken")</tt></td>
  </tr>
  <tr>
    <td>in</td>
    <td>equals any of</td>
    <td><tt>q.field("region").in("MA", "VT", "NH", "RI", "CT")</tt></td>
  </tr>
  <tr>
    <td>notIn</td>
    <td>does not equal any of</td>
    <td><tt>q.field("locality").notIn("Los Angeles")</tt></td>
  </tr>
  <tr>
    <td>beginsWith</td>
    <td>begins with</td>
    <td><tt>q.field("name").beginsWith("b")</tt></td>
  </tr>
  <tr>
    <td>notBeginsWith</td>
    <td>does not begin with</td>
    <td><tt>q.field("name").notBeginsWith("star")</tt></td>
  </tr>
  <tr>
    <td>beginsWithAny</td>
    <td>begins with any of</td>
    <td><tt>q.field("name").beginsWithAny("star", "coffee", "tull")</tt></td>
  </tr>
  <tr>
    <td>notBeginsWithAny</td>
    <td>does not begin with any of</td>
    <td><tt>q.field("name").notBeginsWithAny("star", "coffee", "tull")</tt></td>
  </tr>
  <tr>
    <td>blank</td>
    <td>is blank or null</td>
    <td><tt>q.field("tel").blank()</tt></td>
  </tr>
  <tr>
    <td>notBlank</td>
    <td>is not blank or null</td>
    <td><tt>q.field("tel").notBlank()</tt></td>
  </tr>
  <tr>
    <td>greaterThan</td>
    <td>greater than</td>
    <td><tt>q.field("rating").greaterThan(7.5)</tt></td>
  </tr>
  <tr>
    <td>greaterThanOrEqual</td>
    <td>greater than or equal to</td>
    <td><tt>q.field("rating").greaterThanOrEqual(7.5)</tt></td>
  </tr>
  <tr>
    <td>lessThan</td>
    <td>less than</td>
    <td><tt>q.field("rating").lessThan(7.5)</tt></td>
  </tr>
  <tr>
    <td>lessThanOrEqual</td>
    <td>less than or equal to</td>
    <td><tt>q.field("rating").lessThanOrEqual(7.5)</tt></td>
  </tr>
</table>

## AND

Queries support logical AND'ing your row filters. For example:

    // Build a query to find entities where the name begins with "Coffee" AND the telephone is blank:
    Query q = new Query();
    q.and(
      q.field("name").beginsWith("Coffee"),
      q.field("tel").blank()
    );
    
Note that all row filters set at the top level of the Query are implicitly AND'ed together, so you could also do this:

    new Query()
      .field("name").beginsWith("Coffee")
      .field("tel").blank();

## OR

Queries support logical OR'ing your row filters. For example:

    // Build a query to find entities where the name begins with "Coffee" OR the telephone is blank:
    Query q = new Query();
    q.or(
        q.field("name").beginsWith("Coffee"),
        q.field("tel").blank());
	  
## Combined ANDs and ORs

You can nest AND and OR logic to whatever level of complexity you need. For example:

    // Build a query to find entities where:
    // (name begins with "Starbucks") OR (name begins with "Coffee")
    // OR
    // (name full text search matches on "tea" AND tel is not blank)
    Query q = new Query();
    q.or(
        q.or(
            q.field("name").beginsWith("Starbucks"),
            q.field("name").beginsWith("Coffee")
        ),
        q.and(
            q.field("name").search("tea"),
            q.field("tel").notBlank()
        )
    );


# Crosswalk

The driver fully supports Factual's Crosswalk feature, which lets you "crosswalk" the web and relate entities between Factual's data and that of other web authorities.

(See [the Crosswalk API](http://developer.factual.com/display/docs/Places+API+-+Crosswalk) for more background.)

Crosswalk requests are treated as any other table read, as seen in the example below.  All query-related features apply.

## Simple Crosswalk Example

    // Get all Crosswalk data for a specific Places entity, using its Factual ID:
    ReadResponse resp = factual.fetch("crosswalk", new Query().field("factual_id").equal("97598010-433f-4946-8fd5-4a6dd1639d77"));    
          
# Resolve

The driver fully supports Factual's Resolve feature, which lets you start with incomplete data you may have for an entity, and get potential entity matches back from Factual.

Each result record will include a confidence score (<tt>"similarity"</tt>), and a flag indicating whether Factual decided the entity is the correct resolved match with a high degree of accuracy (<tt>"resolved"</tt>).

For any Resolve query, there will be 0 or 1 entities returned with <tt>"resolved"=true</tt>. If there was a full match, it is guaranteed to be the first record in the JSON response.

(See [the Resolve Blog](http://blog.factual.com/factual-resolve) for more background.)

## Simple Resolve Examples

The <tt>resolves</tt> method gives you all possible matches:

    // Get all entities that are possibly a match
    ReadResponse resp = factual.resolves(new ResolveQuery()
      .add("name", "Buena Vista")
      .add("latitude", 34.06)
      .add("longitude", -118.40));
      
The <tt>resolve</tt> method gives you the one full match if there is one, or null:

    // Get the entity that is a full match, or null:
    Map rec = factual.resolve(new ResolveQuery()
    .add("name", "Buena Vista")
    .add("latitude", 34.06)
    .add("longitude", -118.40));

# Raw Read

Factual may occasionally release a new API which is not immediately supported by the Java driver.  To test queries against these APIs, we recommend using the raw read feature.  The recommendation is to only construct a raw read query if the feature is not yet supported using other convenience methods.

<p>You can perform any GET request using the <tt>factual.get(…)</tt> method. Add parameters to your request by building a map of field and value pairs, and the request will be made using your OAuth token.  The driver will URL-encode the parameter values.

<p>For convenience, use <tt>JsonUtil.toJsonStr(object)</tt> to serialize Java objects to the json format before adding a parameter.  The object can contain maps, collections, primitive types, etc., making it easier to guarantee a valid json string.

## Example Raw Read Queries

Fetch only the name and category fields, including the row count in the response: 
    
    Map<String, Object> params = new HashMap<String, Object>()
    params.put("select", "name,category")
    params.put("include_count", true);
    String respString = factual.get("t/places", params);
    
Fetch 10 items from the "t/places" table in California, New Mexico, or Florida:

    Map<String, Object> params = new HashMap<String, Object>()
    params.put("filters", JsonUtil.toJsonStr(
    		new HashMap() {{  
				put("region", new HashMap() {{
					put("$in", new String[]{"CA", "NM", "FL"});
				}});
			}}
		)
	);
	params.put("limit", 10);
    String respString = factual.fetch("t/places", params);
    
Note that the above examples demonstrate the ability to construct read queries using the raw read feature.  However, in practice, the recommendation is to always use the convenience classes for features which are supported.  In the above cases, a Query object should be used instead.


# Debug Mode

To see a full trace of debug information for a request and response, turn debug mode on.  There are two ways to do so:<p>
Use the <tt>Factual</tt> constructor to enable debug on a new instance:

	Factual factual = new Factual(key, secret, true);

or modify an existing instance to toggle debug mode on and off for individual requests:
	
	factual.debug(true);
	factual.fetch(…);
	factual.debug(false);
	
Debug information will be printed to standard out, with detailed request and response information, including headers.

# Facets

The driver fully supports Factual's Facets feature, which lets you return row counts for Factual tables, grouped by facets of data.  For example, you may want to query all businesses within 1 mile of a location and for a count of those businesses by category.


## Simple Facets Example

    // Returns a count of Starbucks by country
    FacetResponse resp = factual.fetch("global", new FacetQuery("country").search("starbucks"));

Not all fields are configured to return facet counts. To determine what fields you can return facets for, use the schema call.  The faceted attribute of the schema will let you know.

## All Top Level Facets Parameters

<table>
  <tr>
    <th>Parameter</th>
    <th>Description</th>
    <th>Example</th>
  </tr>
  <tr>
    <td>select</td>
    <td>The fields for which facets should be generated. The response will not be ordered identically to this list, nor will it reflect any nested relationships between fields.</td>
    <td><tt>new Facet("region", "locality");</tt></td>
  </tr>
  <tr>
    <td>min count</td>
    <td>For each facet value count, the minimum count it must show in order to be returned in the response. Must be zero or greater. The default is 1.</td>
    <td><tt>f.minCountPerFacetValue(2)</tt></td>
  </tr>
  <tr>
    <td>limit</td>
    <td>The maximum number of unique facet values that can be returned for a single field. Range is 1-250. The default is 20.</td>
    <td><tt>f.maxValuesPerFacet(10)</tt></td>
  </tr>
  <tr>
    <td>filters</td>
    <td>Restrict the data returned to conform to specific conditions.</td>
    <td><tt>f.field("name").beginsWith("Starbucks")</tt></td>
  </tr>
  <tr>
    <td>include count</td>
    <td>Include a count of the total number of rows in the dataset that conform to the request based on included filters. Requesting the row count will increase the time required to return a response. The default behavior is to NOT include a row count. When the row count is requested, the Response object will contain a valid total row count via <tt>.getTotalRowCount()</tt>.</td>
    <td><tt>f.includeRowCount()</tt></td>
  </tr>
  <tr>
    <td>geo</td>
    <td>Restrict data to be returned to be within a geographical range.</td>
    <td>(See the section on Geo Filters)</td>
  </tr>
  <tr>
    <td>search</td>
    <td>Full text search query string.</td>
    <td>
      Find "sushi":<br><tt>f.search("sushi")</tt><p>
      Find "sushi" or "sashimi":<br><tt>f.search("sushi, sashimi")</tt><p>
      Find "sushi" and "santa" and "monica":<br><tt>f.search("sushi santa monica")</tt>
    </td>
  </tr>
</table>  

# Flag

The driver fully supports Factual's Flag feature, which enables flagging problematic rows in Factual tables. Use this feature if you are requesting for an entity to be deleted or merged into a duplicate record.

*Note that the flag feature is new and may not yet be available in the Factual API.  If you experience any errors, please check back at a later time.

## Simple Flag Example

The <tt>flag</tt> method flags a problematic row:

    // Flag a row as inaccurate
	FlagResponse resp = factual.flagInaccurate("global", "0545b03f-9413-44ed-8882-3a9a461848da", new Metadata().user("my_username"));

## All Top Level Flag Parameters

<table>
  <tr>
    <th>Parameter</th>
    <th>Description</th>
    <th>Example</th>
  </tr>
  <tr>
    <td>problem</td>
    <td>One of: duplicate, inaccurate, inappropriate, nonexistent, spam, or other.</td>
    <td><tt>factual.flagDuplicate(table, factualId, metadata)</tt>
	    <p><tt>factual.flagInaccurate(table, factualId, metadata)</tt>	    <p><tt>factual.flagInappropriate(table, factualId, metadata)</tt>
	    <p><tt>factual.flagNonExistent(table, factualId, metadata)</tt>
	    <p><tt>factual.flagSpam(table, factualId, metadata)</tt>
	    <p><tt>factual.flagOther(table, factualId, metadata)</tt>
	    </td>
  </tr>
  <tr>
    <td>user</td>
    <td>An arbitrary token representing the user flagging the data.</td>
    <td><tt>Metadata metadata = new Metadata().user("my_username")</tt></td>
  </tr>
  <tr>
    <td>comment</td>
    <td>Any english text comment that may help explain your corrections.</td>
    <td><tt>metadata.comment("my comment")</tt></td>
  </tr>
  <tr>
    <td>reference</td>
    <td>A reference to a URL, title, person, etc. that is the source of this data.</td>
    <td><tt>metadata.reference("http://...")</tt></td>
  </tr>
</table>  

# Submit

The driver fully supports Factual's Submit feature, which enables you to submit edits to existing rows and/or submit new rows of data in Factual tables. For information on deleting records, see Flag.

*Note that the submit feature is new and may not yet be available in the Factual API.  If you experience any errors, please check back at a later time.

## Simple Submit Examples

The <tt>submit</tt> method is a submission to edit an existing row or add a new row:

	// Field-value mapping for an entity.
	Map<String, Object> values = …;
	Metadata metadata = new Metadata().user("my_username");

	// Submit the addition of a new row
	Submit submit = new Submit(values)
	SubmitResponse resp = factual.submit("global", submit, metadata);
	
    // Submit a field update
	Submit submit = new Submit(values)
    .setValue("longitude", 100);
	SubmitResponse resp = factual.submit("global", "0545b03f-9413-44ed-8882-3a9a461848da",submit, metadata);

	// Submit an update for a field to become blank
	Submit submit = new Submit(values)
    .removeValue("longitude");
	SubmitResponse resp = factual.submit("global", "0545b03f-9413-44ed-8882-3a9a461848da",submit, metadata);

## All Top Level Submit Parameters

<table>
  <tr>
    <th>Parameter</th>
    <th>Description</th>
    <th>Example</th>
  </tr>
  <tr>
    <td>values</td>
    <td>A JSON hash field of names and values to be added to a Factual table</td>
    <td>Update a value:<p><tt>s.setValue("longitude", 100)</tt><p>Make a value blank:<p><tt>s.removeValue("longitude")</tt></td>
  </tr>
  <tr>
    <td>user</td>
    <td>An arbitrary token representing the user submitting the data.</td>
    <td><tt>new Metadata().user("my_username")</tt></td>
  </tr>
  <tr>
    <td>comment</td>
    <td>Any english text comment that may help explain your corrections.</td>
    <td><tt>metadata.comment("my comment")</tt></td>
  </tr>
  <tr>
    <td>reference</td>
    <td>A reference to a URL, title, person, etc. that is the source of this data.</td>
    <td><tt>metadata.reference("http://...")</tt></td>
  </tr>
</table>	

# Multi

The driver fully supports Factual's Multi feature, which enables making multiple requests on the same connection.
Queue responses using <tt>queueFetch</tt>, and send all queued reads using <tt>sendRequests</tt>.  The <tt>sendRequests</tt> method requests all reads queued since the last <tt>sendRequests</tt>.  The responses from the multi request are returned in a list, corresponding to the same order in which they were queued.

## Simple Multi Example

	// Fetch a multi response
	factual.queueFetch("places", new Query().field("region").equal("CA"));
	factual.queueFetch("places", new Query().limit(1)); 
	MultiResponse multi = factual.sendRequests();

# Geopulse

The driver fully supports Factual's <a href="http://developer.factual.com/display/docs/Places+API+-+Geopulse">Geopulse</a> feature, which provides point-based access to geographic attributes: you provide a long/lat coordinate pair, we provide everything we can know about that geography. 

## Simple Geopulse Example

The <tt>geopulse</tt> method fetches results based on the given point:

	ReadResponse resp = factual.geopulse(new Geopulse(new Point(latitude, longitude))
												.only("commercial_density", "commercial_profile"));


## All Top Level Geopulse Parameters

<table>
  <tr>
    <th>Parameter</th>
    <th>Description</th>
    <th>Example</th>
  </tr>
  <tr>
    <td>geo</td>
    <td>A geographic point around which information is retrieved.</td>
    <td><tt>new Point(latitude, longitude)</tt></td>
  </tr>
  <tr>
    <td>select</td>
    <td>What fields to include in the query results. Note that the order of fields will not necessarily be preserved in the resulting JSON response due to the nature of JSON hashes.</td>
    <td><tt>geopulse.only("commercial_density", "commercial_profile")</tt></td>
  </tr>
</table>	


# Reverse Geocoder

The driver fully supports Factual's <a href="http://developer.factual.com/display/docs/Places+API+-+Reverse+Geocoder">Reverse Geocoder</a> feature, which returns the nearest valid address given a longitude and latitude. 

## Simple Reverse Geocoder Example
	
The <tt>reverseGeocode</tt> method fetches results based on the given point:

	ReadResponse resp = factual.reverseGeocode(new Point(latitude, longitude));	

## All Top Level Reverse Geocoder Parameters

<table>
  <tr>
    <th>Parameter</th>
    <th>Description</th>
    <th>Example</th>
  </tr>
  <tr>
    <td>geo</td>
    <td>A valid geographic point for which the closest address is retrieved.</td>
    <td><tt>new Point(latitude, longitude)</tt></td>
  </tr>
</table>

# Monetize

The driver fully supports Factual's Monetize feature, which enables you to find deals for places in Factual's Global Places database.  Use the Query object to specify filters on which to run the monetize request.

## Simple Monetize Example
	
The <tt>monetize</tt> method fetches deals based on a specified query:

    ReadResponse resp = factual.monetize(new Query().field("place_locality").equal("Los Angeles"));

# Exception Handling

If Factual's API indicates an error, a <tt>FactualApiException</tt> unchecked Exception will be thrown. It will contain details about the request you sent and the error that Factual returned.

Here is an example of catching a <tt>FactualApiException</tt> and inspecting it:

    Factual badness = new Factual("BAD_KEY", "BAD_SECRET");
    try{
      badness.read("places", new Query().field("country").equal(true));
    } catch (FactualApiException e) {
      System.out.println("Requested URL: " + e.getRequestUrl());
      System.out.println("Error Status Code: " + e.getResponse().statusCode);
      System.out.println("Error Response Message: " + e.getResponse().statusMessage);
    }
    
    
# More Examples

For more code examples:

* See the standalone demos in <tt>src/test/java/com/factual/demo</tt>
* See the integration tests in <tt>src/test/java/com/factual/FactualTest.java</tt>

# Where to Get Help

If you think you've identified a specific bug in this driver, please file an issue in the github repo. Please be as specific as you can, including:

  * What you did to surface the bug
  * What you expected to happen
  * What actually happened
  * Detailed stack trace and/or line numbers

If you are having any other kind of issue, such as unexpected data or strange behaviour from Factual's API (or you're just not sure WHAT'S going on), please contact us through [GetSatisfaction](http://support.factual.com/factual).
