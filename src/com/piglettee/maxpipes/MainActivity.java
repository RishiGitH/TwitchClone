package com.piglettee.maxpipes;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.piglettee.services.*;
import com.piglettee.objects.*;
import com.squareup.picasso.Picasso;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

public class MainActivity extends Activity implements ServiceResultsReceiver.Receiver, ImageFileReceiver.Receiver
{
	private HashMap<String, Stream_m3u8_playlist> m3u8_playlist_Object;
	private final String SOURCE = "source", HIGH = "high", MEDIUM = "medium", LOW = "low", MOBILE = "mobile", AUDIO_ONLY = "audio_only";
	private String screenWidth;
	private String screenHeight;
	
	private List<GameObject> topGamesList;
	private List<StreamObject> streamList;
	
	private StreamPlayer playerActivity;
	private String currentStreamURL;
	
	private GameListAdapter gameListAdapter;
	private StreamListAdapter streamListAdapter;
	
	private ProgressDialog progressBar;
	
	private final String TAG  = "[MaxPipes] MainActivity";
	private final String service_TAG = "[MaxPipes] TwitchTv_Service";
	
	private StreamObject stream;
	private GameObject topGame;
	
	final String mimeType = "text/html";
    final String encoding = "UTF-8";
    
    private View main;
    
    private AppViews currentView;
    
    //Picasso Variables
    
    private boolean enablePicassoIndicators;
	
	public ServiceResultsReceiver serviceResultsReceiver;
	public ImageFileReceiver imageFileReceiver;
	
	FrameLayout.LayoutParams COVER_SCREEN_GRAVITY_CENTER = new FrameLayout.LayoutParams(
	        ViewGroup.LayoutParams.WRAP_CONTENT,
	        ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
	
	private Intent requestStreamsIntent, imageLoader;
	public GridView listAdapterView, streamListAdapterView;
	
	private GamesItemClickListener gamesItemClickListener = new GamesItemClickListener();
	private StreamItemClickListener streamItemClickListener = new StreamItemClickListener();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//Enable or disable Picasso storage indicators;
		enablePicassoIndicators = true;
		
		topGamesList = new ArrayList<GameObject>();
		streamList = new ArrayList<StreamObject>();
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
	    //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		m3u8_playlist_Object = new HashMap<String, Stream_m3u8_playlist>();
		
		serviceResultsReceiver = new ServiceResultsReceiver(new Handler());
		serviceResultsReceiver.setReceiver(this);
		
		imageFileReceiver = new ImageFileReceiver(new Handler());
		imageFileReceiver.setReceiver(this);
		

		//listAdapterView = (ListView) findViewById(R.layout.games_list);
				
		setContentView(R.layout.games_list);
		
		//main.setBackgroundColor(Color.rgb(255, 255, 255));
		
		
		//Getting the screen size of the device!
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		screenWidth = ""+size.x;
		screenHeight = ""+size.y;
		
		//Replace progressDialogue with loading cursor!
		progressBar = new ProgressDialog(MainActivity.this);
		progressBar.setTitle("MaxPipes Multi-Streamer");
		progressBar.setMessage("Getting Top stream Meta-data");
		progressBar.setCancelable(false);							//<---- Sets if un-cancelable on user touch
		progressBar.show();
		
		//Call to load the gameList intent!
		loadTopGames();
		
	}
	
	//Handles applications state when the it is paused etc.
	public void onPause()
	{
		super.onPause();
		if(playerActivity != null)
		{
			playerActivity.pause();
		}
		serviceResultsReceiver.setReceiver(null);
		imageFileReceiver.setReceiver(null);
		
	}
	
	//Handles resuming of the applications state
	public void onResume()
	{
		super.onResume();
		if(playerActivity != null)
		{
			Bundle playerBundle = new Bundle();
			Intent newIntent = new Intent(this, StreamPlayer.class);
			playerBundle.putString("source",currentStreamURL);
			newIntent.putExtras(playerBundle);
			playerActivity.recreate();
		}

		imageFileReceiver.setReceiver(this);
		serviceResultsReceiver.setReceiver(this);
		
	}
	
	public void onBackPressed()
	{
		if(currentView != null && currentView == AppViews.GAME_LIST )
		{
			this.finish();
		}
		
		if(currentView != null && currentView == AppViews.STREAM_LIST )
		{
			//GO Back and record state change!
			loadTopGames();
			
		}		
		
	}
	
	/*
	 * Handles receiving events or data returned form the Image specific
	 * intent service
	 */
	@Override
	public void onReceiveImageResult(int resultCode, Bundle resultData)
	{
		//Specific handler for image results
		switch (resultCode)
		{
			case STATUS.RUNNING: //do something while service is running and not returned!
				break;
			case STATUS.FINISHED:
				//TODO:Add imageList to list adapter
				if( resultData.getString("list") != null)
				{
					//streamListAdapter.updateImageList(resultData.getParcelableArrayList("images"));
					//streamListAdapter.notifyDataSetChanged();
				}
				else 
				{
					//gameListAdapter.updateImageList(resultData.getParcelableArrayList("images"));
					//gameListAdapter.notifyDataSetChanged();
				}
				break;
			case STATUS.ERROR: //Handle any errors in the service
				//String error = resultData.getString(Intent.EXTRA_TEXT);
				Log.v(service_TAG,"ERROR reading response! ");
				break;
			case STATUS.ERROR_JSONException: //Handle any errors in the service
				//String json_error = resultData.getString(Intent.EXTRA_TEXT);
				Log.v(service_TAG,"JSONException creating JSON object! ");
				break;
			case STATUS.ERROR_MalformedURL: //Handle any errors in the service
				//String url_error = resultData.getString(Intent.EXTRA_TEXT);
				Log.v(service_TAG,"MalformedURLException reading response! ");
				break;
		}
	}
	
	/*
	 * Handles receiving events or data returned from the 
	 * intent service.
	 */
	@Override
	public void onReceiveResult(int resultCode, Bundle resultData)
	{
		switch (resultCode)
		{
			case STATUS.RUNNING: //do something while service is running and not returned!
				break;
			case STATUS.FINISHED: //do something with the returned data.
				validateResults(resultData);
				break;
			case STATUS.ERROR: //Handle any errors in the service
				String error = resultData.getString(Intent.EXTRA_TEXT);
				Log.v(service_TAG,"ERROR reading response! "+error);
				break;
			case STATUS.ERROR_JSONException: //Handle any errors in the service
				String json_error = resultData.getString(Intent.EXTRA_TEXT);
				Log.v(service_TAG,"JSONException creating JSON object! "+json_error);
				break;
			case STATUS.ERROR_MalformedURL: //Handle any errors in the service
				String url_error = resultData.getString(Intent.EXTRA_TEXT);
				Log.v(service_TAG,"MalformedURLException reading response! "+url_error);
				break;
		}
		
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void validateResults(Bundle resultData)
	{
		Log.v(TAG, "<< Response check: "+resultData.getString("twitchResponse")+" >>");
	
		if(resultData.getString("twitchResponse").compareToIgnoreCase("accessToken")==0)
		{
			/*
			 * First must remove special characters from the string, as response is
			 * not in json form, and will therefore not be parsable.
			 */
			String parsedString = resultData.getString("results").replace("\\", "");
			parsedString = parsedString.replace("\"", "");
			String channelName = resultData.getString("channel");
			/* Make JSONObject
			 * - Root
			 * 		- Token Object
			 * 		- Sig value
			 */
			try 
			{
				JSONObject jsonResponse = new JSONObject(parsedString);
				JSONObject tokenObject = jsonResponse.getJSONObject("token");
				String sigString = jsonResponse.getString("sig");
				//Log.v(TAG, "Token value is: "+jsonResponse.toString());
				//Log.v(TAG, "Sig value is: "+sigString);
				
				String nauth = /*URLEncoder.encode(*/tokenObject.toString()/*,"UTF-8")*/;
				String nauthsig = /*URLEncoder.encode(*/sigString/*,"UTF-8")*/;
				//Log.v(TAG, nauth);
				//Log.v(TAG, nauthsig);
				
				/* Final URL
				 * -------------
				 * first ===> http://usher.twitch.tv/api/channel/hls/[channel].m3u8?player=twitchweb
				 * next ====> &token==[]
				 * add ====> &allow_audio_only=true&allow_source=true&type=any&p={9333029}
				 * finally ===> &sig=[]
				 */
				String urlStart = "http://usher.twitch.tv/api/channel/hls/"+channelName+".m3u8?player=twitchweb";
				String tokenPart = "&token="+nauth;
				String lastPart = "&allow_audio_only=true&allow_source=true&type=any&p={9333029}";
				String sigPart = "&sig="+nauthsig;
				String finalURL = urlStart + tokenPart + sigPart + lastPart;				
				requestStreamsIntent.removeExtra("twitchRequest");
				requestStreamsIntent.removeExtra("channel");
				requestStreamsIntent.putExtra("twitchRequest", "streamFile");
				requestStreamsIntent.putExtra("m3u8",finalURL);
				startService(requestStreamsIntent);
				
				Log.v(TAG, finalURL);
			}
			catch (JSONException ex) 
			{
				Log.v(TAG, "JSONException validating json response! "+ex.getMessage());
			}
		}
		else if(resultData.getString("twitchResponse").compareToIgnoreCase("getStreamsByGame")==0)
		{
			//progressBar.dismiss();
			setContentView(R.layout.stream_list);
			currentView = AppViews.STREAM_LIST;
			parseStreamsByGameResults(resultData.getString("results"));
			
			streamListAdapter = new StreamListAdapter(this.getApplicationContext(), this.enablePicassoIndicators);
			if(!this.streamList.isEmpty())
			{
				streamListAdapter.updateDataList(streamList);
			}
			//streamListAdapterView = (ListView) findViewById(R.layout.stream_list);
			streamListAdapterView = (GridView) findViewById(R.id.streamListView);
			
			streamListAdapterView.setAdapter(streamListAdapter);
			streamListAdapterView.setOnItemClickListener(streamItemClickListener);
			
			streamListAdapter.updateImageList(getStreamListMediumImageUrls_List());
			streamListAdapter.notifyDataSetChanged();
			//Log.v(TAG, this.topGame.toString());
			
		}
		else if(resultData.getString("twitchResponse").compareToIgnoreCase("topGames")==0)
		{
			progressBar.dismiss();
			parseTopGameResults(resultData.getString("results"));
			
			//Setting current view state for back functionality!
			currentView = AppViews.GAME_LIST;
			
			if(gameListAdapter == null){
				gameListAdapter = new GameListAdapter(this.getApplicationContext(), enablePicassoIndicators );
			}
			
			if(!topGamesList.isEmpty())
			{
				gameListAdapter.updateDataList(topGamesList);
			}
			//listAdapterView = (ListView) findViewById(R.id.listView1);
			listAdapterView = (GridView) findViewById(R.id.gridViewParent);
			
			listAdapterView.setAdapter(gameListAdapter);
			listAdapterView.setOnItemClickListener(gamesItemClickListener);
			
			gameListAdapter.updateImageList(getLargeBoxArtImageUrls_List());
			gameListAdapter.notifyDataSetChanged();
			
			Log.v(TAG,"Image Loader created AND Started");
			//Log.v(TAG, this.topGame.toString());
			
		}
		else if(resultData.getString("twitchResponse").compareToIgnoreCase("streamFile")==0)
		{
			//Log.v(TAG, "M3U8: File equals:: "+resultData.getString("results"));
			String[] fileArray = parseM3U8_File(resultData.getString("results"));
			//Log.v(TAG, " ======== \n"+fileArray[1].toString());
			Stream_m3u8 streamDataObject = new Stream_m3u8(M3U8_Parser.parseHLSMetadata(fileArray));
			/*for(Object key : map.keySet())
			{
				String lineData = key.toString();
				//Log.v(TAG,lineData);
				String quality = lineData.substring(lineData.indexOf("VIDEO="),lineData.indexOf("http:"));
				if(quality.contains("chunked"))
				{
					m3u8_playlist_Object.put("source", new Stream_m3u8_playlist(key.toString()));
				}
				else if(quality.contains("high")){
					m3u8_playlist_Object.put("high", new Stream_m3u8_playlist(key.toString()));
				}
				else if(quality.contains("medium")){
					m3u8_playlist_Object.put("medium", new Stream_m3u8_playlist(key.toString()));
				}
				else if(quality.contains("low")){
					m3u8_playlist_Object.put("low", new Stream_m3u8_playlist(key.toString()));
				}
				else if(quality.contains("mobile")){
					m3u8_playlist_Object.put("mobile", new Stream_m3u8_playlist(key.toString()));
				}
				else if(quality.contains("audio_only")){
					m3u8_playlist_Object.put("audio_only", new Stream_m3u8_playlist(key.toString()));
				}
			}*/
			//Stream_m3u8_playlist  selectedQuality = ;
			
			String resName = STREAM_RESOLUTION.Medium.name();
			Log.v(TAG,streamDataObject.getResURL_Object("source").getQuality());
			Log.v(TAG,streamDataObject.getResURL_Object("source").getUri_String());
			//Log.v(TAG,selectedQuality.getQuality());
			String resURL = streamDataObject.getResURL_Object("medium").getUri_String();
			currentStreamURL = resURL;
			
			
			Log.v(TAG,"<<< Selected RES: "+resName+" with url: "+resURL+" >>>");
			//
			progressBar.dismiss();
			
			Bundle playerBundle = new Bundle();
			Intent newIntent = new Intent(this, StreamPlayer.class);
			
			/*
			 * Need to create object to pass back the different qualities and their URL's
			 */
			
			//Create parcelable object!
			ArrayList<String> resArrayKeys = new ArrayList<String>();
			ArrayList<String> resArrayValues = new ArrayList<String>();
			
			resArrayKeys.add(streamDataObject.getResURL_Object("source").getQuality());
			resArrayValues.add(streamDataObject.getResURL_Object("source").getUri_String());
			resArrayKeys.add(streamDataObject.getResURL_Object("high").getQuality());
			resArrayValues.add(streamDataObject.getResURL_Object("high").getUri_String());
			resArrayKeys.add(streamDataObject.getResURL_Object("medium").getQuality());
			resArrayValues.add(streamDataObject.getResURL_Object("medium").getUri_String());
			resArrayKeys.add(streamDataObject.getResURL_Object("low").getQuality());
			resArrayValues.add(streamDataObject.getResURL_Object("low").getUri_String());
			resArrayKeys.add(streamDataObject.getResURL_Object("mobile").getQuality());
			resArrayValues.add(streamDataObject.getResURL_Object("mobile").getUri_String());
			resArrayKeys.add(streamDataObject.getResURL_Object("audio_only").getQuality());
			resArrayValues.add(streamDataObject.getResURL_Object("audio_only").getUri_String());
			
			playerBundle.putStringArrayList("res_keys", resArrayKeys);
			playerBundle.putStringArrayList("res_values", resArrayValues);
			
			playerBundle.putString("url_res",resName);
			playerBundle.putString("url_source",resURL);
			newIntent.putExtras(playerBundle);
			startActivity(newIntent,playerBundle);
		}
		else if(resultData.getString("twitchResponse").compareToIgnoreCase("topChannel")==0)
		{
			Log.v(TAG, "Extra info: "+resultData.getString("topChannel"));
			parseTopStreamResults( resultData.getString("results") );
			requestStreamsIntent.removeExtra("twitchRequest");
			requestStreamsIntent.putExtra("twitchRequest","accessToken");
			requestStreamsIntent.putExtra("channel",stream.getChannel().getName());
			startService(requestStreamsIntent);
		}
	}
	
	public void loadTopGames()
	{
		if(requestStreamsIntent == null)
		{
			requestStreamsIntent = new Intent(this, twitch_Service.class);
			requestStreamsIntent.putExtra("twitchRequest","topGames");
			requestStreamsIntent.putExtra("receiver",serviceResultsReceiver);
		}
		setContentView(R.layout.games_list);
		requestStreamsIntent.putExtra("twitchRequest","topGames");
		requestStreamsIntent.putExtra("receiver",serviceResultsReceiver);
		startService(requestStreamsIntent);
	}
	
	private void parseStreamsByGameResults(String stringResults) 
	{
		/*
		 * streams []
		 * 	- channel []
		 * 		-name
		 */

		//Clear the list when reloading the view
		if(!streamList.isEmpty()){
			streamList.clear();
			Log.v(TAG, "The {StreamList} was not empty, so cleared it!");
		}
		
		JSONArray streamArray;
		JSONObject streamRootObject;
		try 
		{
			streamRootObject = new JSONObject(stringResults);
			streamArray = streamRootObject.getJSONArray("streams");
			for(int index = 0; index < streamArray.length(); index++)
			{
				streamList.add(new StreamObject(streamArray.getJSONObject(index)));
				final String stringPrint = streamList.get(index).getChannel().getName().toString();
				Log.v(TAG,"Stream: "+index+" "+stringPrint);
			}		
		}
		catch (JSONException ex) 
		{
			Log.v(TAG,"JSONException validating json response! "+ex.getMessage());
		}
	}

	private ArrayList<String> getStreamListMediumImageUrls_List()
	{
		ArrayList<String> returnedURLs = new ArrayList<String>();
		for(StreamObject stream : streamList)
		{
			returnedURLs.add(stream.getPreview().getMedium_url());
		}
		return returnedURLs;
	}
	
	private ArrayList<String> getMediumBoxArtImageUrls_List()
	{
		ArrayList<String> returnedURLs = new ArrayList<String>();
		for(GameObject game : this.topGamesList)
		{
			returnedURLs.add(game.getGame().getBoxart_Images().getMedium());
		}
		return returnedURLs;
	}
	
	private ArrayList<String> getLargeBoxArtImageUrls_List()
	{
		ArrayList<String> returnedURLs = new ArrayList<String>();
		for(GameObject game : this.topGamesList)
		{
			returnedURLs.add(game.getGame().getBoxart_Images().getLarge());
		}
		return returnedURLs;
	}
	
	private ArrayList<String> getLargeLogoArtImageUrls_List()
	{
		ArrayList<String> returnedURLs = new ArrayList<String>();
		for(GameObject game : this.topGamesList)
		{
			returnedURLs.add(game.getGame().getLogoart_Images().getLarge());
		}
		return returnedURLs;
	}
	
	/*
	 * This method simply parses the returned json object to retrieve the required values
	 */
	private void parseTopGameResults(String stringResults)
	{
		/*
		 * top []
		 * 	- game []
		 * 
		 */
		
		//Clear the list when reloading the view
		if(!topGamesList.isEmpty()){
			topGamesList.clear();
			Log.v(TAG, "The {topGamesList} was not empty, so cleared it!");
		}
		
		JSONArray gameArray;
		JSONObject gameObject, gameRootObject;
		try 
		{
			gameRootObject = new JSONObject(stringResults);
			gameArray = gameRootObject.getJSONArray("top");
			//gameArray.optJSONArray(index)
			
			Log.v(TAG,"testing the size of the array! "+gameArray.length());
			for(int index = 0; index < gameArray.length(); index++)
			{
				final GameObject tempObject = new GameObject(gameArray.getJSONObject(index)); 
						
				topGamesList.add(tempObject);
			}

			//Used for debugging
			/*for ( int iteratorIndex = 0; iteratorIndex < topGamesList.size(); iteratorIndex++)
			{
				Log.v(TAG,topGamesList.get(iteratorIndex).getGame().getName()+": "+topGamesList.get(iteratorIndex).getViewers());
			}*/
			
		}
		catch (JSONException ex) 
		{
			Log.v(TAG,"JSONException validating json response! "+ex.getMessage());
		}
		
	}
	
	private void parseTopStreamResults(String stringResults)
	{
		/*
		 * streams []
		 * 	- channel []
		 * 		-name
		 */
		
		JSONArray streamArray;
		JSONObject streamObject, streamRootObject;
		try 
		{
			streamRootObject = new JSONObject(stringResults);
			streamArray = streamRootObject.getJSONArray("streams");
			streamObject = streamArray.getJSONObject(0);
			stream = new StreamObject(streamObject);
			Log.v(TAG,"Top most stream: "+stream.getChannel().getName());
			
		}
		catch (JSONException ex) 
		{
			Log.v(TAG,"JSONException validating json response! "+ex.getMessage());
		}
		
	}
	
	private String[] parseM3U8_File(String fileToParse)
	{		
		String[] splitString = fileToParse.split("(?=#)");
		
		return splitString;
	}
	
	/*
	 * This is the inner class for holding the usable data 
	 * of the parsed m3u8 file from the stream.
	 */
	private class Stream_m3u8_playlist
	{
		private String Quality;
		private String uri_String;
		private String Rest;
		
		public Stream_m3u8_playlist(String data)
		{
			Rest = data.substring(0, data.indexOf("VIDEO="));
			Quality = data.substring(data.indexOf("VIDEO="),data.indexOf("http:"));
			uri_String = data.substring(data.indexOf("http:"));
		}

		public String getQuality() {
			return Quality;
		}

		public String getUri_String() {
			return uri_String;
		}

		public String getRest() {
			return Rest;
		}
		
	}
	
	private class GamesItemClickListener implements OnItemClickListener
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id) 
		{
			Log.v(TAG,"You have clicked a game! \nView id is "+view.getId()+"\nId is "+id+"\nWith position: "+position);
			GameObject selectedGameItem = (GameObject) parent.getItemAtPosition(position);
			Log.v(TAG, "You have selected: "+(selectedGameItem.getGame()).getName());
			
			requestStreamsIntent.removeExtra("twitchRequest");
			requestStreamsIntent.removeExtra("channel");
			requestStreamsIntent.putExtra("twitchRequest","getStreamsByGame");
			requestStreamsIntent.putExtra("game",(selectedGameItem.getGame()).getName());
			startService(requestStreamsIntent);
			
			//TODO: Now need to make another http call like: https://api.twitch.tv/kraken/streams?game=League%20of%20Legends
		}

		
	}
	private class StreamItemClickListener implements OnItemClickListener
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id) 
		{
			Log.v(TAG,"You have clicked ! \nView id is "+view.getId()+"\nId is "+id+"\nWith position: "+position);
			StreamObject selectedStreamItem = (StreamObject) parent.getItemAtPosition(position);
			Log.v(TAG, "You have selected: "+(selectedStreamItem.getChannel().getName()));
			String streamName = selectedStreamItem.getChannel().getName();
			
			//TODO: Load up player with stream
			Log.v(TAG, "Extra info: "+streamName);
			requestStreamsIntent.removeExtra("twitchRequest");
			requestStreamsIntent.putExtra("twitchRequest","accessToken");
			requestStreamsIntent.putExtra("channel",streamName);
			startService(requestStreamsIntent);
			
			//TODO: Example game list api call:  http call like: https://api.twitch.tv/kraken/streams?game=League%20of%20Legends
		}

		
	}
}
