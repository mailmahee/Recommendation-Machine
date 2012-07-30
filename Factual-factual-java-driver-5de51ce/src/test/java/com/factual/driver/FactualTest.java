package com.factual.driver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

/**
 * Integration tests for the Factual Java driver. Expects your key and secret to
 * be in:
 * 
 * <pre>
 * src/test/resources/key.txt
 * src/test/resources/secret.txt
 * </pre>
 * 
 * @author aaron
 */
public class FactualTest {
  private static Factual factual;

  final double latitude = 34.06018;
  final double longitude = -118.41835;
  final int meters = 5000;

  @Before
  public void setup() {
    String key = read("key.txt");
    String secret = read("secret.txt");
    factual = new Factual(key, secret);
  }

  @Test
  public void testSchema() {
    SchemaResponse schema = factual.schema("restaurants-us");
    assertTrue(schema.getTitle().toLowerCase().contains("restaurant"));
    assertTrue(schema.isGeoEnabled());
    assertTrue(schema.isSearchEnabled());

    assertFalse(schema.getColumnSchemas().isEmpty());

    ColumnSchema nameSchema = schema.getColumnSchema("name");
    assertEquals("name", nameSchema.name);
    assertEquals("string", nameSchema.datatype);
  }

  /**
   * Find rows in the global places database in the United States
   */
  @Test
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public void testCoreExample1() {
    ReadResponse resp = factual.fetch("places", new Query().field("country")
        .equal("US"));

    assertOk(resp);
    assertAll(resp, "country", "US");

    Map<String, Object> params = Maps.newHashMap();
    params.put("filters", JsonUtil.toJsonStr(new HashMap() {
      {
        put("country", new HashMap() {
          {
            put("$eq", "US");
          }
        });
      }
    }));
    String respRaw = factual.get("t/places", params);
    assertEquals(resp.getJson(), respRaw);
  }

  /**
   * Find rows in the restaurant database whose name begins with "Star" and
   * return both the data and a total count of the matched rows.
   */
  @Test
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public void testCoreExample2() {
    ReadResponse resp = factual.fetch("places", new Query().field("name")
        .beginsWith("Star").includeRowCount());

    assertOk(resp);
    assertStartsWith(resp, "name", "Star");

    Map<String, Object> params = Maps.newHashMap();
    params.put("filters", JsonUtil.toJsonStr(new HashMap() {
      {
        put("name", new HashMap() {
          {
            put("$bw", "Star");
          }
        });
      }
    }));
    params.put("include_count", true);
    String respRaw = factual.get("t/places", params);
    assertEquals(resp.getJson(), respRaw);
  }

  /**
   * Do a full-text search of the restaurant database for rows that match the
   * terms "Fried Chicken, Los Angeles"
   */
  @Test
  public void testCoreExample3() {
    ReadResponse resp = factual.fetch("places",
        new Query().search("Fried Chicken, Los Angeles"));

    assertOk(resp);

    Map<String, Object> params = Maps.newHashMap();
    params.put("q", "Fried Chicken, Los Angeles");
    String respRaw = factual.get("t/places", params);
    assertEquals(resp.getJson(), respRaw);

  }

  /**
   * To support paging in your app, return rows 20-25 of the full-text search
   * result from Example 3
   */
  @Test
  public void testCoreExample4() {
    ReadResponse resp = factual.fetch("places",
        new Query().search("Fried Chicken, Los Angeles").offset(20).limit(5));

    assertOk(resp);
    assertEquals(5, resp.getData().size());

    Map<String, Object> params = Maps.newHashMap();
    params.put("q", "Fried Chicken, Los Angeles");
    params.put("offset", 20);
    params.put("limit", 5);
    String respRaw = factual.get("t/places", params);
    assertEquals(resp.getJson(), respRaw);

  }

  /**
   * Return rows from the global places database with a name equal to "Stand"
   * within 5000 meters of the specified lat/lng
   */
  @Test
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public void testCoreExample5() {
    ReadResponse resp = factual.fetch("places", new Query().field("name")
        .equal("Stand").within(new Circle(latitude, longitude, meters)));
    assertNotEmpty(resp);
    assertOk(resp);

    Map<String, Object> params = Maps.newHashMap();
    params.put("geo", JsonUtil.toJsonStr(new HashMap() {
      {
        put("$circle", new HashMap() {
          {
            put("$center",
                new String[] { Double.toString(latitude),
                    Double.toString(longitude) });
            put("$meters", meters);
          }
        });
      }
    }));
    params.put("filters", JsonUtil.toJsonStr(new HashMap() {
      {
        put("name", new HashMap() {
          {
            put("$eq", "Stand");
          }
        });
      }
    }));

    String respRaw = factual.get("t/places", params);
    assertEquals(resp.getJson(), respRaw);

  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public void testSort_byDistance() {

    ReadResponse resp = factual.fetch(
        "places",
        new Query().within(new Circle(latitude, longitude, meters)).sortAsc(
            "$distance"));

    assertNotEmpty(resp);
    assertOk(resp);
    assertAscendingDoubles(resp, "$distance");

    Map<String, Object> params = Maps.newHashMap();
    params.put("geo", JsonUtil.toJsonStr(new HashMap() {
      {
        put("$circle", new HashMap() {
          {
            put("$center",
                new String[] { Double.toString(latitude),
                    Double.toString(longitude) });
            put("$meters", meters);
          }
        });
      }
    }));
    params.put("sort", "$distance:asc");

    String respRaw = factual.get("t/places", params);
    assertEquals(resp.getJson(), respRaw);

  }

  /**
   * {"$and":[{"name":{"$bw":"McDonald's"},"category":{"$bw":"Food & Beverage"}}
   * ]}
   */
  @Test
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public void testRowFilters_2beginsWith() {
    ReadResponse resp = factual.fetch("places",
        new Query().field("name").beginsWith("McDonald's").field("category")
            .beginsWith("Food & Beverage"));

    assertOk(resp);
    assertStartsWith(resp, "name", "McDonald");
    assertStartsWith(resp, "category", "Food & Beverage");

    Map<String, Object> params = Maps.newHashMap();
    params.put("filters", JsonUtil.toJsonStr(new HashMap() {
      {
        put("$and", new Map[] { new HashMap() {
          {
            put("name", new HashMap() {
              {
                put("$bw", "McDonald's");
              }
            });
            put("category", new HashMap() {
              {
                put("$bw", "Food & Beverage");
              }
            });
          }
        } });
      }
    }));

    String respRaw = factual.get("t/places", params);
    assertEquals(resp.getJson(), respRaw);

  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public void testIn() {
    Query q = new Query().field("region").in("CA", "NM", "FL");
    ReadResponse resp = factual.fetch("places", q);

    assertOk(resp);
    assertNotEmpty(resp);
    assertIn(resp, "region", "CA", "NM", "FL");

    q = new Query().field("region").in(new ArrayList() {
      {
        add("CA");
        add("NM");
        add("FL");
      }
    });
    resp = factual.fetch("places", q);
    assertOk(resp);
    assertNotEmpty(resp);
    assertIn(resp, "region", "CA", "NM", "FL");

    Map<String, Object> params = Maps.newHashMap();
    params.put("filters", JsonUtil.toJsonStr(new HashMap() {
      {
        put("region", new HashMap() {
          {
            put("$in", new String[] { "CA", "NM", "FL" });
          }
        });
      }
    }));

    String respRaw = factual.get("t/places", params);
    assertEquals(resp.getJson(), respRaw);

  }

  /**
   * Tests a top-level AND with a nested OR and an $in:
   * 
   * <pre>
   * {$and:[
   *   {region:{$in:["MA","VT","NH"]}},
   *   {$or:[
   *     {name:{$bw:"Star"}},
   *     {name:{$bw:"Coffee"}}]}]}
   * </pre>
   * 
   * @throws UnsupportedEncodingException
   */
  @Test
  public void testComplicated() {
    Query q = new Query();
    q.field("region").in("MA", "VT", "NH");
    q.or(q.field("name").beginsWith("Coffee"),
        q.field("name").beginsWith("Star"));

    ReadResponse resp = factual.fetch("places", q);

    assertOk(resp);
    assertNotEmpty(resp);
    assertIn(resp, "region", "MA", "VT", "NH");

    // assert name starts with (coffee || star)
    for (String name : resp.mapStrings("name")) {
      assertTrue(name.toLowerCase().startsWith("coffee")
          || name.toLowerCase().startsWith("star"));
    }
  }

  private void assertIn(ReadResponse resp, String field, String... elems) {
    for (String val : resp.mapStrings(field)) {
      for (String elem : elems) {
        if (elem.equals(val)) {
          return;
        }
      }
      fail(val + " was not in " + Joiner.on(", ").join(elems));
    }
  }

  @Test
  public void testSimpleTel() {
    ReadResponse resp = factual.fetch("places", new Query().field("tel")
        .beginsWith("(212)"));

    assertStartsWith(resp, "tel", "(212)");

    assertOk(resp);
  }

  /**
   * Search for places with names that have the terms "Fried Chicken"
   */
  @Test
  public void testFullTextSearch_on_a_field() {
    ReadResponse resp = factual.fetch("places", new Query().field("name")
        .search("Fried Chicken"));

    for (String name : resp.mapStrings("name")) {
      assertTrue(name.toLowerCase().contains("frie")
          || name.toLowerCase().contains("fry")
          || name.toLowerCase().contains("chicken"));
    }
  }

  @Test
  public void testCrosswalk_ex1() {
    ReadResponse resp = factual.fetch(
        "crosswalk",
        new Query().field("factual_id").equal(
            "97598010-433f-4946-8fd5-4a6dd1639d77"));
    assertOk(resp);
    List<Map<String, Object>> crosswalks = resp.getData();
    assertFalse(crosswalks.isEmpty());
    assertFactualId(crosswalks, "97598010-433f-4946-8fd5-4a6dd1639d77");
  }

  @Test
  public void testCrosswalk_ex2() {
    ReadResponse resp = factual.fetch(
        "crosswalk",
        new Query().field("factual_id")
            .equal("97598010-433f-4946-8fd5-4a6dd1639d77").field("namespace")
            .equal("loopt"));
    List<Map<String, Object>> crosswalks = resp.getData();
    assertOk(resp);
    assertEquals(1, crosswalks.size());
    assertFactualId(crosswalks, "97598010-433f-4946-8fd5-4a6dd1639d77");
    assertNamespace(crosswalks, "loopt");
  }

  @Test
  public void testCrosswalk_ex3() {
    ReadResponse resp = factual.fetch("crosswalk",
        new Query().field("namespace").equal("foursquare")
            .field("namespace_id").equal("4ae4df6df964a520019f21e3"));
    List<Map<String, Object>> crosswalks = resp.getData();
    assertOk(resp);
    assertFalse(crosswalks.isEmpty());
  }

  @Test
  public void testCrosswalk_limit() {
    ReadResponse resp = factual.fetch(
        "crosswalk",
        new Query().field("factual_id")
            .equal("97598010-433f-4946-8fd5-4a6dd1639d77").limit(1));
    List<Map<String, Object>> crosswalks = resp.getData();

    assertOk(resp);
    assertEquals(1, crosswalks.size());
  }

  @Test
  public void testResolve_ex1() {
    ReadResponse resp = factual.fetch(
        "places",
        new ResolveQuery().add("name", "McDonalds")
            .add("address", "10451 Santa Monica Blvd").add("region", "CA")
            .add("postcode", "90025"));
    assertOk(resp);
    assertNotEmpty(resp);
  }

  @Test
  public void testApiException_BadAuth() {
    Factual badness = new Factual("badkey", "badsecret");
    try {
      badness.fetch("places", new Query().field("region").equal("CA"));
      fail("Expected to catch a FactualApiException");
    } catch (FactualApiException e) {
      assertEquals(401, e.getResponse().getStatusCode());
      assertEquals("Unauthorized", e.getResponse().getStatusMessage());
      assertTrue(e.getRequestUrl().startsWith(
          "http://api.v3.factual.com/t/places"));
    }
  }

  @Test
  public void testApiException_BadSelectField() {
    try {
      Query select = new Query().field("country").equal("US").only("hours");
      factual.fetch("places", select);
      fail("Expected to catch a FactualApiException");
    } catch (FactualApiException e) {
      assertEquals(400, e.getResponse().getStatusCode());
      assertTrue(e.getRequestUrl().startsWith(
          "http://api.v3.factual.com/t/places"));
      // verify the message includes useful info from the API error
      assertTrue(e.getMessage().contains("select"));
      assertTrue(e.getMessage().contains("unknown field"));
    }
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public void testSelect() {
    Query select = new Query().field("country").equal("US")
        .only("address", "country");
    assertEquals("[address, country]",
        Arrays.toString(select.getSelectFields()));

    ReadResponse resp = factual.fetch("places", select);
    assertOk(resp);
    assertAll(resp, "country", "US");

    Map<String, Object> params = Maps.newHashMap();
    params.put("filters", JsonUtil.toJsonStr(new HashMap() {
      {
        put("country", new HashMap() {
          {
            put("$eq", "US");
          }
        });
      }
    }));
    params.put("select", "address,country");
    String respRaw = factual.get("t/places", params);
    assertEquals(resp.getJson(), respRaw);

  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public void testCustomRead1() {

    Map<String, Object> params = Maps.newHashMap();
    params.put("filters", JsonUtil.toJsonStr(new HashMap() {
      {
        put("region", new HashMap() {
          {
            put("$in", new String[] { "CA", "NM", "FL" });
          }
        });
      }
    }));
    params.put("select", "address,country");
    String respString = factual.get("t/places", params);

    assertTrue(respString != null && respString.length() > 0);
  }

  @Test
  public void testCustomRead2() {
    Map<String, Object> params = Maps.newHashMap();
    params.put("select", "name,category");
    params.put("include_count", true);

    String respString = factual.get("t/places", params);
    assertTrue(respString != null && respString.length() > 0);
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public void testRawGet() {
    String respRaw = factual.get(
        "t/places",
        "filters="
            + UrlUtil.urlEncode("{\"name\" : {\"$eq\" : \"Starbucks\"} }"));
    try {
      JSONObject rootJsonObj = new JSONObject(respRaw);
      assertEquals("ok", rootJsonObj.get("status"));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  /**
   * And should not be used for geo queries. However, neither should it throw an
   * exception.
   */
  @Test
  public void testInvalidAnd() {
    Query q = new Query();
    q.and(q.field("category").beginsWith("Food"),
        q.within(new Circle(latitude, longitude, meters)));

    ReadResponse resp = factual.fetch("places", q);
    assertOk(resp);
  }

  @Test
  public void testFacet() {
    FacetQuery facet = new FacetQuery("region", "locality").search("Starbucks")
        .maxValuesPerFacet(20).minCountPerFacetValue(100).includeRowCount();

    FacetResponse resp = factual.fetch("places", facet);
    assertOk(resp);
    assertTrue(resp.getData().size() > 0);
  }

  @Test
  public void testFacetFilter() {
    FacetQuery facet = new FacetQuery("locality").field("region").in("MA",
        "VT", "NH");
    facet.and(facet.or(facet.field("name").beginsWith("Coffee"),
        facet.field("name").beginsWith("Star")), facet.field("locality")
        .beginsWith("w"));
    FacetResponse resp = factual.fetch("places", facet);
    assertOk(resp);
    assertTrue(resp.getData().size() > 0);
  }

  @Test
  public void testFacetGeo() {
    FacetQuery facet = new FacetQuery("category").within(new Circle(latitude,
        longitude, meters));
    FacetResponse resp = factual.fetch("places", facet);
    assertOk(resp);
    assertTrue(resp.getData().size() > 0);
  }

  @Test
  @Ignore("Until API supports it")
  public void testSubmitAdd() {
    Submit submit = new Submit().setValue("longitude", 100);
    SubmitResponse resp = factual.submit("2EH4Pz", submit,
        new Metadata().user("test_driver_user"));
    assertOk(resp);
    assertTrue(resp.isNewEntity());
  }

  @Test
  @Ignore("Until API supports it")
  public void testSubmitEdit() {
    Submit submit = new Submit().setValue("longitude", 100);
    SubmitResponse resp = factual.submit("2EH4Pz",
        "0545b03f-9413-44ed-8882-3a9a461848da", submit,
        new Metadata().user("test_driver_user"));
    assertOk(resp);
    assertFalse(resp.isNewEntity());
  }

  @Test
  @Ignore("Until API supports it")
  public void testSubmitDelete() {
    Submit submit = new Submit().removeValue("longitude");
    SubmitResponse resp = factual.submit("2EH4Pz",
        "0545b03f-9413-44ed-8882-3a9a461848da", submit,
        new Metadata().user("test_driver_user"));
    assertOk(resp);
    assertFalse(resp.isNewEntity());
  }

  @Test
  public void testSubmitError() {
    Submit submit = new Submit().removeValue("longitude");
    FactualApiException exception = null;
    try {
      SubmitResponse resp = factual.submit("2EH4Pz", "randomwrongid", submit,
          new Metadata().user("test_driver_user"));
    } catch (FactualApiException e) {
      exception = e;
    }
    assertTrue(exception != null);
  }

  @Test
  @Ignore("Until API supports it")
  public void testFlagDuplicate() {
    FlagResponse resp = factual.flagDuplicate("2EH4Pz",
        "0545b03f-9413-44ed-8882-3a9a461848da",
        new Metadata().user("test_driver_user"));
    assertOk(resp);
  }

  @Test
  @Ignore("Until API supports it")
  public void testFlagInaccurate() {
    FlagResponse resp = factual.flagInaccurate("2EH4Pz",
        "0545b03f-9413-44ed-8882-3a9a461848da",
        new Metadata().user("test_driver_user"));
    assertOk(resp);
  }

  @Test
  @Ignore("Until API supports it")
  public void testFlagInappropriate() {
    FlagResponse resp = factual.flagInappropriate("2EH4Pz",
        "0545b03f-9413-44ed-8882-3a9a461848da",
        new Metadata().user("test_driver_user"));
    assertOk(resp);
  }

  @Test
  @Ignore("Until API supports it")
  public void testFlagNonExistent() {
    FlagResponse resp = factual.flagNonExistent("2EH4Pz",
        "0545b03f-9413-44ed-8882-3a9a461848da",
        new Metadata().user("test_driver_user"));
    assertOk(resp);
  }

  @Test
  @Ignore("Until API supports it")
  public void testFlagSpam() {
    FlagResponse resp = factual.flagSpam("2EH4Pz",
        "0545b03f-9413-44ed-8882-3a9a461848da",
        new Metadata().user("test_driver_user"));
    assertOk(resp);
  }

  @Test
  @Ignore("Until API supports it")
  public void testFlagOther() {
    FlagResponse resp = factual.flagOther("2EH4Pz",
        "0545b03f-9413-44ed-8882-3a9a461848da",
        new Metadata().user("test_driver_user"));
    assertOk(resp);
  }

  @Test
  public void testGeopulse() {
    ReadResponse resp = factual.geopulse(new Geopulse(new Point(latitude,
        longitude)).only("commercial_density", "commercial_profile"));
    Map<String, Object> pulse = resp.getData().get(0);
    assertTrue(pulse.containsKey("commercial_profile"));
    assertTrue(pulse.containsKey("commercial_density"));
    assertOk(resp);
  }

  @Test
  public void testGeocode() {
    ReadResponse resp = factual.reverseGeocode(new Point(latitude, longitude));
    assertTrue(resp.getData().size() == 1);
    assertOk(resp);
  }

  @Test
  public void testWorldGeographies() {
    Query query = new Query().and(
        new Query().field("name").equal("philadelphia"),
        new Query().field("country").equal("us"), new Query()
            .field("placetype").equal("locality"));
    ReadResponse resp = factual.fetch("world-geographies", query);
    assertTrue(resp.getData().size() == 14);
    assertOk(resp);
  }

  @Test
  public void testMulti() {
    factual.queueFetch("places", new Query().field("region").equal("CA"));
    factual.queueFetch("places", new Query().limit(1));
    MultiResponse multi = factual.sendRequests();
    List<Response> data = multi.getData();
    assertTrue(data.size() == 2);
    for (int i = 0; i < 2; i++) {
      Response resp = data.get(i);
      if (i == 0) {
        assertTrue(resp.getIncludedRowCount() == 20);
        assertOk(resp);
      } else if (i == 1) {
        assertTrue(resp.getIncludedRowCount() == 1);
        assertOk(resp);
      }
    }
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public void testMultiRawRead() {
    Map<String, Object> params = Maps.newHashMap();
    params.put("filters", JsonUtil.toJsonStr(new HashMap() {
      {
        put("country", new HashMap() {
          {
            put("$eq", "US");
          }
        });
      }
    }));
    factual.queueFetch("places", new FacetQuery("region", "locality"));
    factual.queueFetch("t/places", params);
    MultiResponse multi = factual.sendRequests();
    List<Response> data = multi.getData();
    assertTrue(data.size() == 2);
    for (int i = 0; i < 2; i++) {
      Response resp = data.get(i);
      if (i == 0) {
        assertTrue(resp instanceof FacetResponse);
      } else {
        assertTrue(resp instanceof RawReadResponse);
        assertTrue(resp.getIncludedRowCount() == 20);
      }
      assertOk(resp);
    }
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public void testRawReadMulti() {
    final Map<String, Object> queryParams1 = Maps.newHashMap();
    queryParams1.put("filters", JsonUtil.toJsonStr(new HashMap() {
      {
        put("country", new HashMap() {
          {
            put("$eq", "US");
          }
        });
      }
    }));
    Map<String, Object> params = Maps.newHashMap();
    params.put("queries", JsonUtil.toJsonStr(new HashMap() {
      {
        put("q1", UrlUtil.toUrl("/t/places", new HashMap() {
          {
            put("limit", 1);
          }
        }));
      }
    }));
    String respRaw = factual.get("multi", params);
    assertTrue(respRaw != null);
  }

  @Test
  public void testMultiComplex() {
    factual.queueFetch("places", new FacetQuery("region", "locality"));
    factual.queueFetch("places", new Query().limit(1));
    factual.queueFetch(
        "places",
        new ResolveQuery().add("name", "McDonalds")
            .add("address", "10451 Santa Monica Blvd").add("region", "CA")
            .add("postcode", "90025"));
    MultiResponse multi = factual.sendRequests();
    List<Response> data = multi.getData();
    assertTrue(data.size() == 3);
    for (int i = 0; i < 3; i++) {
      Response resp = data.get(i);
      if (i == 0) {
        assertTrue(resp instanceof FacetResponse);
        assertOk(resp);
      } else if (i == 1) {
        assertTrue(resp instanceof ReadResponse);
        assertOk(resp);
        assertTrue(resp.getIncludedRowCount() == 1);
      } else if (i == 2) {
        assertTrue(resp instanceof ReadResponse);
        assertOk(resp);
      }
    }
  }

  @Test
  public void testMultiCrosswalk() {
    factual.queueFetch(
        "crosswalk",
        new Query().field("factual_id")
            .equal("97598010-433f-4946-8fd5-4a6dd1639d77").limit(1));
    MultiResponse multi = factual.sendRequests();
    for (Response resp : multi.getData()) {
      assertOk(resp);
    }
  }

  @Test
  public void testMultiGeopulseWithNearestAddress() {
    factual.queueFetch(new Geocode(new Point(latitude, longitude)));
    factual.queueFetch(new Geopulse(new Point(latitude, longitude)));
    MultiResponse multi = factual.sendRequests();
    assertTrue(multi.getData().size() == 2);
    for (Response resp : multi.getData()) {
      assertOk(resp);
    }
  }

  @Test
  public void testMultiGeopulseWithNearestPlace() {
    factual.queueFetch("global",
        new Query().within(new Circle(latitude, longitude, meters)));
    factual.queueFetch(new Geopulse(new Point(latitude, longitude)));
    MultiResponse multi = factual.sendRequests();
    assertTrue(multi.getData().size() == 2);
    for (Response resp : multi.getData()) {
      assertOk(resp);
    }
  }

  @Test
  public void testMonetize() {
    ReadResponse resp = factual.monetize(new Query().field("place_locality")
        .equal("Los Angeles"));
    assertOk(resp);
  }

  @Test
  public void testMonetizeByBusiness() {
    ReadResponse resp = factual.monetize(new Query().field("place_factual_id")
        .equal("3226fac0-2f85-49d7-bc67-288fb2fc52ee"));
    assertOk(resp);
  }

  /**
   * Test debug mode
   */
  @Test
  public void testDebug() {
    factual.debug(true);
    ReadResponse resp = factual.fetch("places", new Query().field("country")
        .equal("US"));
    factual.debug(false);
    assertOk(resp);
    assertAll(resp, "country", "US");
  }

  private void assertFactualId(List<Map<String, Object>> crosswalks, String id) {
    for (Map<String, Object> cw : crosswalks) {
      assertEquals(id, cw.get("factual_id"));
    }
  }

  private void assertNamespace(List<Map<String, Object>> crosswalks, String ns) {
    for (Map<String, Object> cw : crosswalks) {
      assertEquals(ns, cw.get("namespace"));
    }
  }

  private void assertNamespaceDeprecated(List<Crosswalk> crosswalks, String ns) {
    for (Crosswalk cw : crosswalks) {
      assertEquals(ns, cw.getNamespace());
    }
  }

  private static final void assertNotEmpty(Response resp) {
    assertFalse(resp.isEmpty());
  }

  private static final void assertOk(Response resp) {
    assertEquals("ok", resp.getStatus());
  }

  private void assertAll(ReadResponse resp, String field, String expected) {
    for (String out : resp.mapStrings(field)) {
      assertEquals(expected, out);
    }
  }

  private void assertStartsWith(ReadResponse resp, String field, String substr) {
    for (String out : resp.mapStrings(field)) {
      assertTrue(out.startsWith(substr));
    }
  }

  private void assertAscendingDoubles(ReadResponse resp, String field) {
    Double prev = Double.MIN_VALUE;
    for (Map<?, ?> rec : resp.getData()) {
      Double d = (Double) rec.get(field);
      assertTrue(d >= prev);
      prev = d;
    }
  }

  /**
   * Reads value from named file in src/test/resources
   */
  public static String read(String name) {
    try {
      File file = new File("src/test/resources/" + name);
      if (file.exists()) {
        return FileUtils.readFileToString(file).trim();
      } else {
        fail("You must provide " + file);
        System.err.println("You must provide " + file);
        throw new IllegalStateException("Could not find " + file);
      }
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

}
