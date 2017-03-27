package com.piglettee.maxpipes;

import com.piglettee.services.twitch_Service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;


/*
 * This class handles all of the calls to the twitch tv api
 * 
 *  -- Research into using Android services for api/ajax requests
 */
public class TwitchHandler 
{
	private final long HANDLER_STARTED;							//<-- Long value of start time
	private long last_Updated_TopStreams;
	
	private final int MIN_LAST_UPDATED_CONSTANT = 10000;
	public TwitchHandler()
	{
		//Default constructor for this handler class
		//TODO: Call a init method to setup all the parameters
		HANDLER_STARTED = System.currentTimeMillis();
	}
	
	/*
	 * Simple method that checks if updated within the last 'constant' seconds
	 */
	private boolean isUpdateValid()
	{
		final long coolDown = System.currentTimeMillis() - last_Updated_TopStreams;
		final int coolDown_Int = (int)(coolDown/1000);
		if( coolDown >= MIN_LAST_UPDATED_CONSTANT )
		{
			last_Updated_TopStreams = System.currentTimeMillis();
			return true;
			
		}
		else
		{
			System.out.println("Can update in the next "+coolDown_Int+" seconds! ");
			return false;
		}
	}
	
	/*
	 * This method searches for the top streams currently live streaming
	 * on twitch tv. It grabs the first 'n' number of streams.
	 */
	public void getTopStreams_Number(int number)
	{
		if(isUpdateValid())
		{
			//TODO: Call to services to get top 'number' of games
		}
		
	}
	/*
	 * This method searches for the top streams currently live streaming
	 * on twitch tv. It grabs the top ten (10) streamers across all games.
	 */
	public void getTopStreams(Context context)
	{
		if(isUpdateValid())
		{
			//TODO: Call to services to get top 'number' of games
			//Creates a new IntenService for this request
			/*Intent requestStreamsIntent = new Intent(context, twitch_Service.class);
			requestStreamsIntent.setData(Uri.parse("topStreams"));
			context.startService(requestStreamsIntent);*/
		}
	}
	
	/*
	 * This method grabs 'n' number of top streams currently streaming
	 * a certain type of game.
	 */
	public void getTopStreams_Number_Of_x(String gameToSearch)
	{
		//TODO: Call to the services to get top streams of this 'game'
		if(isUpdateValid())
		{
			//TODO: Call to services to get top 'number' of games
		}
		
	}
	
	
	
}
