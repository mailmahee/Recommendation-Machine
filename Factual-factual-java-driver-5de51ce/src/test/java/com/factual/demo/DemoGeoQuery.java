package com.factual.demo;

import static com.factual.driver.FactualTest.read;

import com.factual.driver.Circle;
import com.factual.driver.Factual;
import com.factual.driver.Query;


public class DemoGeoQuery {

  public static void main(String[] args) {
    String key = read("key.txt");
    String secret = read("secret.txt");
    Factual factual = new Factual(key, secret);

    // Build a Query that finds entities located within 5000 meters of a latitude, longitude.
    // Sort results by distance, ascending:
    Query q = new Query()
    .within(new Circle(34.06018, -118.41835, 5000))
    .sortAsc("$distance");

    System.out.println(
        factual.fetch("places", q));

  }

}
