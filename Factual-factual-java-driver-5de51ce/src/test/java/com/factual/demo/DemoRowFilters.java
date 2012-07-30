package com.factual.demo;

import static com.factual.driver.FactualTest.read;

import com.factual.driver.Factual;
import com.factual.driver.Query;


public class DemoRowFilters {

  public static void main(String[] args) {
    String key = read("key.txt");
    String secret = read("secret.txt");
    Factual factual = new Factual(key, secret);

    // Find places whose name field starts with "Starbucks"
    Query q1 = new Query().field("name").beginsWith("Starbucks");

    System.out.println(
        factual.fetch("places", q1));

    // Find places with a blank telephone number
    Query q2 = new Query().field("tel").blank();

    System.out.println(
        factual.fetch("places", q2));
  }

}
