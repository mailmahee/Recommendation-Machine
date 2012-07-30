package com.factual.demo;

import static com.factual.driver.FactualTest.read;

import com.factual.driver.Factual;
import com.factual.driver.Query;


public class DemoSort {

  public static void main(String[] args) {
    String key = read("key.txt");
    String secret = read("secret.txt");
    Factual factual = new Factual(key, secret);

    // Build a Query to find 10 random entities and sort them by name, ascending:
    Query q = new Query()
    .limit(10)
    .sortAsc("name");

    System.out.println(
        factual.fetch("places", q));

    // Build a Query to find 20 random entities, sorted ascending primarily by region, then by locality, then by name:
    q = new Query()
    .limit(20)
    .sortAsc("region")
    .sortAsc("locality")
    .sortDesc("name");

    System.out.println(
        factual.fetch("places", q));
  }

}
