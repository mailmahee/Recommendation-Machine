package com.factual.demo;

import static com.factual.driver.FactualTest.read;

import com.factual.driver.Factual;
import com.factual.driver.Query;


public class DemoSimpleRead {

  public static void main(String[] args) {
    String key = read("key.txt");
    String secret = read("secret.txt");
    Factual factual = new Factual(key, secret);

    System.out.println(
        factual.fetch("places", new Query().limit(3)));
  }

}
