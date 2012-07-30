package com.factual.driver;

import java.util.Map;

/**
 * Represents metadata to be sent with submit and flag requests
 * @author brandon
 */
public class Metadata {
	
	private static final String USER = "user";
	private static final String DEBUG = "debug";
	private static final String COMMENT = "comment";
	private static final String REFERENCE = "reference";
	
	private Parameters queryParams = new Parameters();

	/**
	 * Constructor.  Create metadata to associate with submit and flag requests
	 */
	public Metadata() {
	}
	
	private Metadata(Parameters queryParams) {
		this.queryParams = queryParams;
	}

	/**
	 * Set a user name for the person submitting the data 
	 * @param username
	 * @return new Metadata, with a username set
	 */
	public Metadata user(String username) {
		Parameters params = queryParams.copy();
		params.setParam(USER, username);
		return new Metadata(params);
	}
	
	/**
	 * The request will only be a test query and no actual data will be written
	 * @return new Metadata, marked as a debug request
	 */
	private Metadata debug() {
		return debug(true);
	}
	
	/**
	 * When true, the request will only be a test query and no actual data will be written.
	 * The default behavior is to NOT include debug.
	 * @param debug true if you want this to be a test query where no actual date is written
	 * @return new Metadata, marked with whether or not this is a debug request
	 */
	private Metadata debug(boolean debug) {
		Parameters params = queryParams.copy();
		params.setParam(DEBUG, debug);
		return new Metadata(params);
	}

	/**
	 * Set a comment that will help to explain your corrections
	 * @param comment the comment that may help explain your corrections
	 * @return new Metadata, with a comment set
	 */
	public Metadata comment(String comment) {
		Parameters params = queryParams.copy();
		params.setParam(COMMENT, comment);
		return new Metadata(params);
	}

	/**
	 * Set a reference to a URL, title, person, etc. that is the source of this data
	 * @param reference a reference to a URL, title, person, etc. that is the source of this data
	 * @return new Metadata, with a reference set
	 */
	public Metadata reference(String reference) {
		Parameters params = queryParams.copy();
		params.setParam(REFERENCE, reference);
		return new Metadata(params);
	}

	protected Map<String, Object> toUrlParams() {
		return queryParams.toUrlParams(null);
	}
	
}
