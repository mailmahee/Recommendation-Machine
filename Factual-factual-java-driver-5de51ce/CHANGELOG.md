## 1.5.1
 * Adds support for a raw get request using a path and url-encoded parameter string.
 * Fixes bug with list-based filters not being sent as an array in the query string. 

## 1.5.0
 * Removes deprecated Crosswalk-related classes.  Use a table read on the Crosswalk table instead.

## 1.4.3
 * Crosswalk updates: deprecate old API and document usage of Crosswalk table read.

## 1.4.2
 * Adds monetize API support.

## 1.4.1
 * Adds better testing, improved debugging, etc.

## 1.4.0

 * Adds Multi call
 * Adds geopulse
 * Adds reverse geocode

## 1.2.1

 * Renames Contribute feature to Submit

## 1.2.0

 * Adds support for facet, contribute, and flag features.
 * Adds raw read and debug info features.

## 1.1.0

 * Refactored into .driver package
 * Created a Tabular interface for responses that have tabulatable data
 * Fixed bug where lists ($in, etc.) were not formatted properly when sending JSON to API
 * Updated Google API Client Library dependency from 1.4.1-beta to 1.7.0-beta with relevant driver changes

## 1.0.2

 * Added support for fetching table schemas

## 1.0.1

 * Added more docs and demos

## 1.0

 * Initial release
