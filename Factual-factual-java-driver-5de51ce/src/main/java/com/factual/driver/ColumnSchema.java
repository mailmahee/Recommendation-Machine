package com.factual.driver;

import java.util.Map;

public class ColumnSchema {
  public final String name;
  public final String description;
  public final boolean faceted;
  public final boolean sortable;
  public final String label;
  public final String datatype;
  public final boolean searchable;

  /**
   * Constructor. Maps raw column schema data into a new ColumnSchema.
   * 
   * @param map
   *          A column schema map object as provided by Factual.
   */
  public ColumnSchema(Map<String, Object> map) {
    name = (String)map.get(Constants.SCHEMA_COLUMN_NAME);
    description = (String)map.get(Constants.SCHEMA_COLUMN_DESCRIPTION);
    label = (String)map.get(Constants.SCHEMA_COLUMN_LABEL);
    datatype = (String)map.get(Constants.SCHEMA_COLUMN_DATATYPE);
    faceted = (Boolean)map.get(Constants.SCHEMA_COLUMN_FACETED);
    sortable = (Boolean)map.get(Constants.SCHEMA_COLUMN_SORTABLE);
    searchable = (Boolean)map.get(Constants.SCHEMA_COLUMN_SEARCHABLE);
  }

}
