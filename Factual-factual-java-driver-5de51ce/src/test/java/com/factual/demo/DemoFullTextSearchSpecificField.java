package com.factual.demo;

import static com.factual.driver.FactualTest.read;

import com.factual.driver.Factual;
import com.factual.driver.Query;


public class DemoFullTextSearchSpecificField {

  public static void main(String[] args) {
    String key = read("key.txt");
    String secret = read("secret.txt");
    Factual factual = new Factual(key, secret);

    // Build a query to full text search against the name field:
    Query q = new Query().field("name").search("Fried Chicken");

    System.out.println(
        factual.fetch("places", q));
  }

}
