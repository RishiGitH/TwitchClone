package com.piglettee.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

/*
 * This is a service that handles api calls to the restful twitch
 * service. It uses IntentService as a base.
 */
public class twitch_Service extends IntentService 
{
	private final String TAG  = "[MaxPipes]TwitchTv_Service";
	
	public twitch_Service()
	{
		super("twitch_Service");
		// TODO Auto-generated constructor stub
	}
	
	public twitch_Service(String name)
	{
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent workIntent) 
	{
		final ResultReceiver receiver = workIntent.getParcelableExtra("receiver");
		
		String twitchCommand = workIntent.getStringExtra("twitchRequest");
		String extraInfo = workIntent.getStringExtra("channel");
		String game = workIntent.getStringExtra("game");
		String m3u8_request = workIntent.getStringExtra("m3u8");
		String tempValue;
		
		tempValue = validateURLRequest(twitchCommand, extraInfo, m3u8_request, game);
		
		//Check if request is for a m3u8 stream file
		if(twitchCommand.compareToIgnoreCase("streamFile")==0)
		{
			//Call special method for file reader
			streamFileRequester(receiver, twitchCommand, tempValue, new Bundle());
		}
		else
		{
			//Otherwise use normal url/json request handler
			normalRequester(receiver, twitchCommand, tempValue, new Bundle(), game, extraInfo);
		}
	}
	
	/*
	 * StreamFileRequester Method is a special Handler for returning data from a m3u8 file request
	 * 
	 * It takes as input the ResultReceiver, the Command as a String, the request URL String, and the Bundle
	 * It sends back the data in the bundle with a call to the ResultReceiver object.
	 */
	private void streamFileRequester(ResultReceiver receiver, String twitchCommand, String streamURL, Bundle theBundle)
	{
		//Handles the requests for the m3u8 file.
		if(streamURL !=null && streamURL.length() > 0)
		{
			receiver.send(STATUS.RUNNING, Bundle.EMPTY);
			try
			{
				URL urlForConnection = new URL(streamURL);
				HttpURLConnection urlConnection = (HttpURLConnection) urlForConnection.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.setDoInput(true);

				if(urlConnection.getResponseCode()==200)
				{
					Log.v(TAG,"RESPONSE CODE 200: OK!");
					
					final int m3u8_File_Size = urlConnection.getContentLength();				
					InputStream inStream =  urlConnection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
					String lineData = "";
					String fullData ="";
					int count;
					
					Log.v(TAG," [ Retreiving m3u8 file ]");
					
					while(  (lineData = reader.readLine()) != null)
					{
						fullData += lineData;
					}
					
					Log.v(TAG, "Closed the URL connection!");					

					theBundle.putString("twitchResponse", "streamFile");
					theBundle.putString("results", fullData);
					receiver.send(STATUS.FINISHED, theBundle);
				}	
			}
			catch(MalformedURLException ex)
			{
				theBundle.putString(Intent.EXTRA_TEXT, ex.toString());
				receiver.send(STATUS.ERROR_MalformedURL, theBundle);				
			}
			catch(IOException ex)
			{
				theBundle.putString(Intent.EXTRA_TEXT, ex.toString());
				receiver.send(STATUS.ERROR, theBundle);
			}
		}
	}
	
	/*
	 * NormalRequester Method is a handler for returning json data from a request
	 * 
	 * It takes as input the ResultReceiver, the Command as a String, the request URL String, and the Bundle
	 * It sends back the data in the bundle with a call to the ResultReceiver object.
	 */
	private void normalRequester(ResultReceiver receiver, String twitchCommand, String requestURL, Bundle theBundle, String game, String extraInfo)
	{
		//Normal handler of the stream requests
		
		if(requestURL !=null && requestURL.length() > 0)
		{
			receiver.send(STATUS.RUNNING, Bundle.EMPTY);
			try
			{
				URL urlForConnection = new URL(requestURL);
				HttpURLConnection urlConnection = (HttpURLConnection) urlForConnection.openConnection();
				
				//Need to set the connection properties
				urlConnection.setRequestProperty("Content-type", "application/json");
				urlConnection.setRequestProperty("Accept", "application/json");
				urlConnection.setRequestMethod("GET");
				urlConnection.setDoInput(true);
				
				if(urlConnection.getResponseCode()==200)
				{
					Log.v(TAG,"RESPONSE CODE 200: OK!");
					
					BufferedReader streamReader = new BufferedReader( new InputStreamReader(urlConnection.getInputStream()));
					
					Log.v(TAG, requestURL);
					String stringLine = "";
					String data = "";
					
					while( (data = streamReader.readLine())!=null)
					{
						stringLine += data;
					}
					
					streamReader.close();
					
					Log.v(TAG, "Closed the URL connection!");
					
					//String stringBuffer = new String(buffer);
					
					/*
					 * Before can parse object as Json file need to create Json
					 * object and initialize. 
					 */
					
					Log.v(TAG, "The command recieved was: "+twitchCommand);
					
					if(twitchCommand.equalsIgnoreCase("accessToken"))
					{
						theBundle.putString("twitchResponse", twitchCommand);
						theBundle.putString("channel", extraInfo);
						theBundle.putString("results", stringLine);
						receiver.send(STATUS.FINISHED, theBundle);
					}
					else
					{
						JSONObject jsonObject = new JSONObject(stringLine);
						if(twitchCommand.equalsIgnoreCase("topStreams"))
						{
							theBundle.putString("twitchResponse", "topChannel");
						}
						else
						{
							theBundle.putString("twitchResponse", twitchCommand);
						}
						theBundle.putString("results", jsonObject.toString());
						receiver.send(STATUS.FINISHED, theBundle);
					}
				}

			}
			catch(MalformedURLException ex)
			{
				theBundle.putString(Intent.EXTRA_TEXT, ex.toString());
				receiver.send(STATUS.ERROR_MalformedURL, theBundle);
				
			}
			catch(IOException ex)
			{
				theBundle.putString(Intent.EXTRA_TEXT, ex.toString());
				receiver.send(STATUS.ERROR, theBundle);
			}
			catch (JSONException ex) 
			{
				theBundle.putString(Intent.EXTRA_TEXT, ex.toString());
				receiver.send(STATUS.ERROR_JSONException, theBundle);
			}
		}
	}
	
	/*
	 * Simple method that checks the passed data for correctness
	 * and then gets the corresponding api url.
	 */
	private String validateURLRequest(String workData, String extraInfo, String m3u8_request, String game) 
	{
		String validatedURL = "";
		if(workData.equalsIgnoreCase("topGames"))
		{
			Log.v(TAG, workData+" <:> topGames");
			validatedURL = "https://api.twitch.tv/kraken/games/top?limit=10";
		}
		else if(workData.equalsIgnoreCase("getStreamsByGame") && game != null)
		{
			Log.v(TAG, workData+" <:> getStreamsByGame");
			try
			{
				final String encodedGameString = URLEncoder.encode(game, "UTF-8");
				validatedURL = "https://api.twitch.tv/kraken/streams?game="+encodedGameString+"&limit=10";
			}
			catch (UnsupportedEncodingException ex) 
			{
				// TODO Auto-generated catch block
				Log.v(TAG,"UnsupportedEncodingException "+ex.getMessage());
			}
		}
		else if(workData.equalsIgnoreCase("getStreams"))
		{
			Log.v(TAG, workData+" <:> getStreams");
			validatedURL = "https://api.twitch.tv/kraken/streams?limit=10";
		}
		else if(workData.equalsIgnoreCase("topStreams"))
		{
			Log.v(TAG, workData+" <:> topStreams");
			validatedURL = "https://api.twitch.tv/kraken/streams?limit=10";
		}
		else if(workData.equalsIgnoreCase("streamFile"))
		{
			Log.v(TAG, "<<m3u8 request sent to the handler!>>");
			validatedURL = m3u8_request;
		}
		else if(workData.equalsIgnoreCase("accessToken") && extraInfo != "")
		{
			Log.v(TAG, workData+" <:> accessToken");
			validatedURL = "http://api.twitch.tv/api/channels/"+extraInfo+"/access_token";
		}
		return validatedURL;
	}
	
	
}
