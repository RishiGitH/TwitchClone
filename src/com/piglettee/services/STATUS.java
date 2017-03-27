package com.piglettee.services;

/*
 * This is simply a code class for determining the status of the service.
 */

public class STATUS 
{
	public static final int FINISHED = 200;
	public static final int RUNNING = 202;
	
	/*
	 * ERROR codes are below
	 * 
	 *  Added the error codes for MalformedURL & JSON Exceptions.
	 *  Codes values are simply for differentiating the errors, 
	 *  nothing more than that. :)
	 */
	
	public static final int ERROR = 404;
	public static final int ERROR_MalformedURL = 503;
	public static final int ERROR_JSONException = 504;
}
