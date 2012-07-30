package com.factual.demo;

import static com.factual.driver.FactualTest.read;

import com.factual.driver.Factual;
import com.factual.driver.Query;


public class DemoRowFilterAnd {

  public static void main(String[] args) {
    String key = read("key.txt");
    String secret = read("secret.txt");
    Factual factual = new Factual(key, secret);

    // Build a query to find entities where the name begins with "Coffee" AND the telephone is blank:
    Query q = new Query();
    q.and(
        q.field("name").beginsWith("Coffee"),
        q.field("tel").blank()
    );

    System.out.println(
        factual.fetch("places", q));

    // Alternatively:
    Query q2 = new Query()
    .field("name").beginsWith("Coffee")
    .field("tel").blank();

    System.out.println(
        factual.fetch("places", q2));
  }

}
