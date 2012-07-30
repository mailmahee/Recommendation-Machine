package com.factual.driver;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class UrlUtil {

  /**
   * Get a url-encoded request string from a path and a collection of parameters
   * 
   * @param path
   *          the path for the request
   * @param param
   *          a map of parameter keys and values
   * @return the url-encoded request string
   */
  public static String toUrl(String path, Map<String, Object> param) {
    return toUrl(path, toUrlQuery(param));
  }

  /**
   * Convert parameters to a single serialized string, including "&" delimiters
   */
  protected static String toUrlQuery(Map<String, Object> paramMap) {
    return Joiner.on("&").skipNulls().join(toParamList(paramMap));
  }

  public static String toUrl(String root, String parameters) {
    return root + "?" + parameters;
  }

  /**
   * Url-encode a string
   * 
   * @param value
   *          a string to url-encode
   * @return url-encoded string
   */
  public static String urlEncode(String value) {
    try {
      return URLEncoder.encode(value.toString(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  private static String urlPair(Object name, Object val, boolean urlEncode) {
    if (val != null) {
      return name
          + "="
          + ((urlEncode && val instanceof String) ? UrlUtil.urlEncode(val
              .toString()) : val);
    } else {
      return null;
    }
  }

  /**
   * Convert parameters to a list of parameter strings
   */
  private static List<String> toParamList(Map<String, Object> paramMap) {
    return toParamList(paramMap, true);
  }

  /**
   * Convert parameters to a list of parameter strings
   */
  private static List<String> toParamList(Map<String, Object> paramMap,
      boolean urlEncode) {
    List<String> paramList = Lists.newArrayList();
    for (Entry<String, Object> entry : paramMap.entrySet()) {
      paramList.add(UrlUtil.urlPair(entry.getKey(),
          String.valueOf(entry.getValue()), urlEncode));
    }
    return paramList;
  }

}
