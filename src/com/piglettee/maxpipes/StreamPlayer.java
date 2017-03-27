package com.piglettee.maxpipes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.MediaController;

import com.piglettee.objects.STREAM_RESOLUTION;
import com.piglettee.objects.VideoControllerView;

public class StreamPlayer extends Activity implements 
SurfaceHolder.Callback, OnPreparedListener, VideoControllerView.MediaPlayerControl
{
	//Defaults and application final variables
	private HashMap<String, String> qualityMap;
	
	private final String TAG = "[MaxPipes] MediaPlayer Activity";
	private final boolean DEFAULT_WAKE_LOCK_STATUS = true;
	
	
	private enum PlayerState { PLAYING, PAUSING, RESUMING, STOPPING, STARTING, CLOSING, PREPARING, INITIALIZING, RESTARTING };
	private PlayerState currentState = PlayerState.INITIALIZING;
	
	private MediaPlayer.OnErrorListener errorListener;
	private MediaPlayer.OnInfoListener infoListener;
	private SurfaceView videoSurface;
	private MediaPlayer mediaPlayer;
	private boolean isPaused = true;
	private VideoControllerView mediaController;
	private String sourceURL, newURL;
	
	private STREAM_RESOLUTION CURRENT_RES;
	
	@SuppressWarnings("unused")
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		qualityMap = new HashMap<String,String>();
		
		//Setting up the full-screen player view
		if(false)
		{
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		else
		{
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE);
		}
		setContentView(R.layout.activity_stream_player);
		
		//Setting up all the player specific listeners and objects
		initializePlayer();
	}
	
	private void initializePlayer()
	{

		Bundle passedData = getIntent().getExtras();
		if(passedData != null)
		{
			//Extracting passed in stream URL
			sourceURL = passedData.get("url_source").toString();
			CURRENT_RES = Enum.valueOf(STREAM_RESOLUTION.class, passedData.get("url_res").toString());

			
			
			//Test the other res URL's
			
			ArrayList<String> tempKeys = passedData.getStringArrayList("res_keys");
			ArrayList<String> tempValues = passedData.getStringArrayList("res_values");
			
			//Iterator<String> keys = tempKeys.iterator();
			//Iterator<String> values = tempValues.iterator();
			
			//while(keys.hasNext() && values.hasNext())
			//{
			//		qualityMap.put(keys.next(), values.next());
			//}
			
			
			
			for(int index1=0; index1<tempValues.size(); index1++)
			{
				qualityMap.put(tempKeys.get(index1), tempValues.get(index1));
				Log.v(TAG,"Res: "+tempKeys.get(index1)+" "+tempValues.get(index1));
				
			}
			Log.v(TAG, qualityMap.get("source")+" is in 'source'");
			
		}
		
		//Keeping track of player State
		currentState = PlayerState.INITIALIZING;
		
		//Creating and assigning the surface view
		videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
		SurfaceHolder videoHolder = videoSurface.getHolder();
		videoHolder.setKeepScreenOn(DEFAULT_WAKE_LOCK_STATUS);	//<----- Allowing for the wake lock while playing: Keep screen on!
		videoHolder.addCallback(this);
		
		//Creating the media-player
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setScreenOnWhilePlaying(DEFAULT_WAKE_LOCK_STATUS); //<----- Allowing for the wake lock while playing: Keep screen on!
		
		//TODO: Must add Code to handle keeping the aspect ratio on screen resize/orientation
		
		//Added Error listener in order to use Info Listener!
		errorListener = new MediaPlayer.OnErrorListener() 
		{	
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra)
			{
				boolean handled;
				switch(what)
				{
					case 1: Log.v(TAG,"<< MEDIA_ERROR_UNKNOWN >>");
						handled = true;
						return true;
					case 100: Log.v(TAG,"<< MEDIA_ERROR_SERVER_DIED >>");
						handled = true;
						return true;
					default: handled = false;
						break;
				}
				return handled;
			}
		};
		
		//Added Info listener in order to handle media startup info/messages
		infoListener = new MediaPlayer.OnInfoListener() {
			
			@Override
			public boolean onInfo(MediaPlayer mp, int what, int extra) 
			{
				boolean handled;
				switch(what)
				{
					case 1: Log.v(TAG,"<< MEDIA_INFO_UNKNOWN >>");
						handled = true;
						break;
					case 700: Log.v(TAG,"<< MEDIA_INFO_VIDEO_TRACK_LAGGING >>");
						handled = true;
						break;
					case 3: Log.v(TAG,"<< MEDIA_INFO_VIDEO_RENDERING_START >>");
						handled = true;
						break;
					case 701: Log.v(TAG,"<< MEDIA_INFO_BUFFERING_START >>");
						handled = true;
						break;
					case 702: Log.v(TAG,"<< MEDIA_INFO_BUFFERING_END >>");
						handled = true;
						break;
					case 703: Log.v(TAG,"<< MEDIA_INFO_NETWORK_BANDWIDTH >> -- "+extra);
						handled = true;
						break;
					case 800: Log.v(TAG,"<< MEDIA_INFO_BAD_INTERLEAVING >>");
						handled = true;
						break;
					case 801: Log.v(TAG,"<< MEDIA_INFO_NOT_SEEKABLE >>");
						handled = true;	
						break;
					case 802: Log.v(TAG,"<< MEDIA_INFO_METADATA_UPDATE >>");
						handled = true;
						break;
					case 901: Log.v(TAG,"<< MEDIA_INFO_UNSUPPORTED_SUBTITLE >>");
						handled = true;	
						break;
					case 902: Log.v(TAG,"<< MEDIA_INFO_SUBTITLE_TIMED_OUT >>");
						handled = true;
						break;
					default: handled = false;
						break;
					
				}
				return handled;
			}
		};
		
		//Setting the listeners to the mediaPlayer
		mediaPlayer.setOnInfoListener(infoListener);
		mediaPlayer.setOnErrorListener(errorListener);
		Log.v(TAG, " . {1}MediaPlayer Has been created!");
		mediaController = new VideoControllerView(this);
		Log.v(TAG, " . {2}VideoControllerView Has been created!");
		
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);			//<----- Maybe need this too!
		try 
		{
			mediaPlayer.setDataSource(this, Uri.parse(sourceURL));
			Log.v(TAG, " .. {3}setDataSource Has been set!");
			mediaPlayer.prepareAsync();
			currentState = PlayerState.PREPARING;
			//mediaPlayer.setOnPreparedListener(this);
			Log.v(TAG, " ... {4}prepareAsync Has been called!");
			mediaPlayer.setOnPreparedListener(this);
			Log.v(TAG, " .... {5}setOnPreparedListener Has been set!");
		}
		catch (IllegalArgumentException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SecurityException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalStateException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void updateThePlayer()
	{
		
		
		try {
			if(newURL != null){
				mediaPlayer.stop();
				mediaPlayer.reset();
				sourceURL = newURL;
				mediaPlayer.setDataSource(this, Uri.parse(sourceURL));
				Log.v(TAG, " .. {3}setDataSource Has been set!");
				mediaPlayer.prepareAsync();
				currentState = PlayerState.PREPARING;
			}
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void onBackPressed()
	{
		Log.v(TAG, " onBackPressed Has been pressed!");
		this.finish();
	}
	
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{

		//Need to get the Resolution change if there was one!
		STREAM_RESOLUTION tempRes = mediaController.getCurrentSelectedRes(CURRENT_RES);
		
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			if(!mediaController.isShowing())
			{
				mediaController.show();
				return true;
			}
			else 
			{
				mediaController.hide();
				return true;
			}
		}
		else if(tempRes != null && tempRes.compareTo(CURRENT_RES)!=0)
		{
			Log.v(TAG, "CURRENT RES SET TO: "+CURRENT_RES+" CHANGING TO: "+tempRes);
			//Here we change to the new Res if the current is different
			updateStreamResolution(tempRes);
			CURRENT_RES = tempRes;
			return true;
		}
		else return false;
		
	}
	
	private void updateStreamResolution(STREAM_RESOLUTION newStreamRes)
	{
		//Check and select correct Res
		if(newStreamRes.compareTo(CURRENT_RES)!=0)
		{
			switch(newStreamRes)
			{
				case Source : newURL = qualityMap.get(newStreamRes.name().toLowerCase());
				Log.v(TAG, "Trying to change Resolution to : "+newStreamRes.name());
					break;
				case High : newURL = qualityMap.get(newStreamRes.name().toLowerCase());
				Log.v(TAG, "Trying to change Resolution to : "+newStreamRes.name());
					break;
				case Medium : newURL = qualityMap.get(newStreamRes.name().toLowerCase());
				Log.v(TAG, "Trying to change Resolution to : "+newStreamRes.name());
					break;
				case Low : newURL = qualityMap.get(newStreamRes.name().toLowerCase());
				Log.v(TAG, "Trying to change Resolution to : "+newStreamRes.name());
					break;
				case Mobile : newURL = qualityMap.get(newStreamRes.name().toLowerCase());
				Log.v(TAG, "Trying to change Resolution to : "+newStreamRes.name());
					break;
				default : newURL = "NULL";
					break;
			}
			updateThePlayer();
		}
		
	}
	
	@Override
	public void onPrepared(MediaPlayer mp) 
	{
		Log.v(TAG, " ..... {6}onPrepared Has been called!");
		mediaController.setMediaPlayer(this);
		mediaController.setAnchorView( (FrameLayout) findViewById(R.id.videoSurfaceContainer));
		currentState = PlayerState.STARTING;
		onStart();
	}
	
	
	
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{

		mediaController.setMediaPlayer(this);
		mediaController.setAnchorView( (FrameLayout) findViewById(R.id.videoSurfaceContainer));
    	mediaPlayer.setDisplay(holder);
    	Log.v(TAG, " ...... {7}setDisplay Has been called!");

	}
	
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) 
	{
		//Nothing in this method!
		
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		//Nothing to change here yet!
		
	}
	
	public void onStop(){
		super.onStop();
		Log.v(TAG, " onStop Has been called!");
		if(this.isFinishing())
		{
			
			mediaPlayer.stop();
			Log.v(TAG, " stop Has been called!");
			mediaPlayer.release();
			Log.v(TAG, " release Has been called!");
		}
		else if(currentState == PlayerState.PAUSING)
		{
			currentState = PlayerState.STOPPING;
			mediaPlayer.stop();
			Log.v(TAG, " stop Has been called!");
			mediaPlayer.reset();
			Log.v(TAG, " media player has been Reset!");
		}
		else 
		{
			mediaPlayer.stop();
			Log.v(TAG, " stop Has been called!");
		}
		
	}
	
	public void onStart(){
		super.onStart();
		if(currentState != PlayerState.RESTARTING && currentState != PlayerState.PREPARING)
		{
			//Not sure what to do here!
			start();
		}
		else {
			Log.v(TAG, " ... onStart method skipped because MediaPlayer is: "+currentState);
		}
	}
	
	public void onRestart()
	{
		super.onRestart();
		currentState = PlayerState.RESTARTING;
		Log.v(TAG, " onRestart Has been called!");
	}
	
	/*
	 * Adding this method so you can properly pause the stream
	 * when the activity has lost focused.
	 */
	public void onPause()
	{
		super.onPause();
		Log.v(TAG, " onPause Has been called!");
		//This should be called when the media activity has lost focus
		if(!this.isFinishing())
		{
			if( mediaPlayer.isPlaying() )
			{
				currentState = PlayerState.PAUSING;
				Log.v(TAG, "<< MediaPlayer is now Paused! >>");
				pause();
			}
			
		}
		
		
	}
	
	/*
	 * Adding this method so you can properly resume the stream
	 * when android has regained the activity focus!
	 */
	public void onResume()
	{
		super.onResume();
		Log.v(TAG, " onResume Has been called!");
		
		if (currentState == PlayerState.RESTARTING) 
		{
			initializePlayer();
		}
	
		else if (!this.isFinishing() && (currentState != PlayerState.PREPARING) )
		{
			if(!mediaPlayer.isPlaying())
			{
				Log.v(TAG, "<< MediaPlayer is Starting! >>");
				
			    start();
			}
		}
		else
		{
			Log.v(TAG, " ... OnResume method skipped because MediaPlayer is: "+currentState);
		}
		
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		Log.v(TAG, " Destroying the Actitivy!");
		if(mediaPlayer != null) mediaPlayer.release();
		mediaPlayer = null;
		this.finish();
		Log.v(TAG, " Finished the Actitivy!");
	}
	
	@Override
	public int getDuration() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void seekTo(int pos) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean isPlaying()
	{
		if(this.isFinishing())
		{
			return false;
		}
		else if(currentState == PlayerState.RESTARTING || currentState == PlayerState.STOPPING || currentState == PlayerState.PREPARING){
			return false;
		}
		else return mediaPlayer.isPlaying();
		
	}
	@Override
	public int getBufferPercentage() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public boolean canPause() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public boolean canSeekBackward() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean canSeekForward() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isFullScreen() {
		// TODO Auto-generated method stub
		return false;
	}

	public void toggleFullScreen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		Log.v(TAG,"<< Start has been called! >>");
		if(currentState == PlayerState.PREPARING){
			//do nothing!
		}
		else if(!isPlaying() || currentState == PlayerState.STARTING)
		{
			mediaPlayer.start();
			currentState = PlayerState.PLAYING;
		}
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		Log.v(TAG,"<< Pause has been called! >>");
		if(isPlaying() || (currentState == PlayerState.PAUSING) )
		{
			mediaPlayer.pause();
		}
	}

	
	

//	public class OnErrorListener
//	{
//		public boolean onError(MediaPlayer mp, int what, int extra)
//		{
//			switch(what)
//			{
//				case 1: Log.v(TAG,"<< MEDIA_ERROR_UNKNOWN >>");
//					break;
//				case 100: Log.v(TAG,"<< MEDIA_ERROR_SERVER_DIED >>");
//					break;
//			}
//			return true;
//		}
//	}	


}
