package com.piglettee.objects;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class StreamObject 
{
	private final String TAG = "[MaxPipes]StreamObject"; 
	/*
	 * Json values
	 */
	
	private final long stream_ID;
	private final String game;
	private final int viewers;
	private final String created_Date;
	private final String video_Height;
	private final String avg_Fps;
	
	private _links _links;
	
	private Preview preview;
	
	private Channel channel;
	//Preview Object with screenshot size and links
	
	//Channel Object
	
	private enum Resolution { HD_1080 , HD_720, SD, LOW }
	
	
	public StreamObject(JSONObject stream) throws JSONException
	{
		//Use this shorthand method for null checking!
		/*
		 * this.value = (object.get("some_String").toString().compareToIgnoreCase("null")==0) ? Value_if_ null : object.getString("some_String");
		 */
		this.stream_ID = (stream.get("_id").toString().compareToIgnoreCase("null")==0) ? 0 : stream.getLong("_id");
		this.game = (stream.get("game").toString().compareToIgnoreCase("null")==0) ? "" : stream.getString("game");
		this.viewers = (stream.get("viewers").toString().compareToIgnoreCase("null")==0) ? 0 : stream.getInt("viewers");
		this.created_Date = (stream.get("created_at").toString().compareToIgnoreCase("null")==0) ? "" : stream.getString("created_at");
		this.video_Height = (stream.get("video_height").toString().compareToIgnoreCase("null")==0) ? "" : stream.getString("video_height");
		this.avg_Fps = (stream.get("average_fps").toString().compareToIgnoreCase("null")==0) ? "" : stream.getString("average_fps");
		
		Log.v(TAG,"Main stream values extracted!");
		
		try 
		{
			this._links = new _links(stream.getJSONObject("_links"));
		}
		catch (JSONException ex) 
		{
			//Catch the _links constructor pars error!
			Log.v(TAG,"Error parsing JSONObject in _links constructor! "+ex.getMessage());
		}
		
		Log.v(TAG,"_link values extracted!");
		
		try 
		{
			this.preview = new Preview(stream.getJSONObject("preview"));
		}
		catch (JSONException ex) 
		{
			//Catch the Preview constructor pars error!
			Log.v(TAG,"Error parsing JSONObject in Preview constructor! "+ex.getMessage());
		}
		
		Log.v(TAG,"preview values extracted!");
		
		try 
		{
			this.channel = new Channel(stream.getJSONObject("channel"));
		}
		catch (JSONException ex) 
		{
			//Catch the Channel constructor pars error!
			Log.v(TAG,"Error parsing JSONObject in Channel constructor! "+ex.getMessage());
		}
		
		Log.v(TAG,"channel values extracted!");
		Log.v(TAG,"=== JSON Extract Done! ===");
	}
	
	// -------- Inner classes Below here! ---------
	
	/*
	 * Inner class to hold the link to the stream object itself
	 */
	public class _links
	{
		private final String Self;
		
		public _links(JSONObject _links)  throws JSONException
		{
			Self = (_links.get("self").toString().compareToIgnoreCase("null")==0) ? "none" : _links.getString("self");
		}
		
		public String getSelf_Link(){
			return Self;
		}
	}
	
	/*
	 * This inner class holds all of the links to different sized
	 * screenshots of the current live stream.
	 * 
	 * Throws JSONException if passed Object is not JSONObject
	 */
	public class Preview
	{
		private final String small_url;
		private final String medium_url;
		private final String large_url;
		private final String template_url;
		
		public Preview(JSONObject previewObject) throws JSONException
		{
			small_url = previewObject.getString("small");
			medium_url = previewObject.getString("medium");
			large_url = previewObject.getString("large");
			template_url = previewObject.getString("template");
			
		}

		public String getSmall_url() {
			return small_url;
		}

		public String getMedium_url() {
			return medium_url;
		}

		public String getLarge_url() {
			return large_url;
		}

		public String getTemplate_url() {
			return template_url;
		}		
	}
	
	/*
	 * Main Channel inner class.
	 * 
	 * Holds detailed information about the channel of the stream.
	 * 
	 * Throws JSONException if passed Object is not JSONObject
	 */
	public class Channel
	{
		private _links _links;
		private String background;
		private String banner;
		private String broadcaster_language;
		private String display_name;
		private String game;
		private String logo;
		private boolean mature;
		private String status;
		private boolean partner;
		private String url;
		private String video_banner;
		private long id;
		private String name;
		private String created_at;
		private String updated_at;
		private int delay;
		private int followers;
		private String profile_banner;
		private String profile_banner_background_color;
		private long views;
		private String language;
		
		
		public Channel(JSONObject channelObject) throws JSONException
		{
			try 
			{
				_links = new _links(channelObject.getJSONObject("_links"));
			} 
			catch (JSONException ex) 
			{
				//Catch the _links JSON object pars error!
				Log.v(TAG,"JSON Parse error in _links constructor! "+ex.getMessage());
			}
			
			this.background = (channelObject.get("background").toString().compareToIgnoreCase("null")==0) ? null : channelObject.getString("background");
			this.banner = (channelObject.get("banner").toString().compareToIgnoreCase("null")==0) ? null : channelObject.getString("banner");
			this.broadcaster_language = (channelObject.get("broadcaster_language").toString().compareToIgnoreCase("null")==0) ? null : channelObject.getString("broadcaster_language");
			this.display_name = (channelObject.get("display_name").toString().compareToIgnoreCase("null")==0) ? null : channelObject.getString("display_name");
			this.game = (channelObject.get("game").toString().compareToIgnoreCase("null")==0) ? null : channelObject.getString("game");
			this.logo = (channelObject.get("logo").toString().compareToIgnoreCase("null")==0) ? null : channelObject.getString("logo");

			this.mature = (channelObject.get("mature").toString().compareToIgnoreCase("null")==0) ? false : channelObject.getBoolean("mature");

			
			this.status = (channelObject.get("status").toString().compareToIgnoreCase("null")==0) ? null : channelObject.getString("status");
			this.partner = (channelObject.get("partner").toString().compareToIgnoreCase("null")==0) ? false : channelObject.getBoolean("partner");
			this.url = (channelObject.get("url").toString().compareToIgnoreCase("null")==0) ? null : channelObject.getString("url");
			this.video_banner = (channelObject.get("video_banner").toString().compareToIgnoreCase("null")==0) ? null : channelObject.getString("video_banner");
			this.id = (channelObject.get("_id").toString().compareToIgnoreCase("null")==0) ? 0 : channelObject.getLong("_id");
			this.name = (channelObject.get("name").toString().compareToIgnoreCase("null")==0) ? null : channelObject.getString("name");
			this.created_at = (channelObject.get("created_at").toString().compareToIgnoreCase("null")==0) ? null : channelObject.getString("created_at");
			this.updated_at = (channelObject.get("updated_at").toString().compareToIgnoreCase("null")==0) ? null : channelObject.getString("updated_at");
			this.delay = (channelObject.get("delay").toString().compareToIgnoreCase("null")==0) ? 0 : channelObject.getInt("delay");
			this.followers = (channelObject.get("followers").toString().compareToIgnoreCase("null")==0) ? 0 : channelObject.getInt("followers");
			this.profile_banner = (channelObject.get("profile_banner").toString().compareToIgnoreCase("null")==0) ? null : channelObject.getString("profile_banner");
			this.profile_banner_background_color = (channelObject.get("profile_banner_background_color").toString().compareToIgnoreCase("null")==0) ? null : channelObject.getString("profile_banner_background_color");
			this.views = (channelObject.get("views").toString().compareToIgnoreCase("null")==0) ? 0 : channelObject.getLong("views");
			this.language = (channelObject.get("language").toString().compareToIgnoreCase("null")==0) ? null : channelObject.getString("language");
					
			
		}
		
		
		//-------------------- _links inner class ----------------------
		public class _links
		{
			//Channel links urls
			private final String Self;
			private final String Follows;
			private final String Commercial;
			private final String Stream_Key;
			private final String Chat;
			private final String Features;
			private final String Subscriptions;
			private final String Editors;
			private final String Videos;
			private final String Teams;
			
			public _links(JSONObject channel_links) throws JSONException
			{
				Self = (channel_links.get("self").toString().compareToIgnoreCase("null")==0) ? "none" : channel_links.getString("self");
				Follows = (channel_links.get("follows").toString().compareToIgnoreCase("null")==0) ? "none" : channel_links.getString("follows");
				Commercial = (channel_links.get("commercial").toString().compareToIgnoreCase("null")==0) ? "none" : channel_links.getString("commercial");
				Stream_Key = (channel_links.get("stream_key").toString().compareToIgnoreCase("null")==0) ? "none" : channel_links.getString("stream_key");
				Chat = (channel_links.get("chat").toString().compareToIgnoreCase("null")==0) ? "none" : channel_links.getString("chat");
				Features = (channel_links.get("features").toString().compareToIgnoreCase("null")==0) ? "none" : channel_links.getString("features");
				Subscriptions = (channel_links.get("subscriptions").toString().compareToIgnoreCase("null")==0) ? "none" : channel_links.getString("subscriptions");
				Editors = (channel_links.get("editors").toString().compareToIgnoreCase("null")==0) ? "none" : channel_links.getString("editors");
				Videos = (channel_links.get("videos").toString().compareToIgnoreCase("null")==0) ? "none" : channel_links.getString("videos");
				Teams = (channel_links.get("teams").toString().compareToIgnoreCase("null")==0) ? "none" : channel_links.getString("teams");
			}

			public String getSelf() {
				return Self;
			}

			public String getFollows() {
				return Follows;
			}

			public String getCommercial() {
				return Commercial;
			}

			public String getStream_Key() {
				return Stream_Key;
			}

			public String getChat() {
				return Chat;
			}

			public String getFeatures() {
				return Features;
			}

			public String getSubscriptions() {
				return Subscriptions;
			}

			public String getEditors() {
				return Editors;
			}

			public String getVideos() {
				return Videos;
			}

			public String getTeams() {
				return Teams;
			}
			
			
		}

		
		//-------- Getter Methods ------------
		
		public _links get_links() {
			return _links;
		}

		public String getBackground() {
			return background;
		}

		public String getBanner() {
			return banner;
		}

		public String getBroadcaster_language() {
			return broadcaster_language;
		}

		public String getDisplay_name() {
			return display_name;
		}

		public String getGame() {
			return game;
		}

		public String getLogo() {
			return logo;
		}

		public boolean isMature() {
			return mature;
		}

		public String getStatus() {
			return status;
		}

		public boolean isPartner() {
			return partner;
		}

		public String getUrl() {
			return url;
		}

		public String getVideo_banner() {
			return video_banner;
		}

		public long getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getCreated_at() {
			return created_at;
		}

		public String getUpdated_at() {
			return updated_at;
		}

		public int getDelay() {
			return delay;
		}

		public int getFollowers() {
			return followers;
		}

		public String getProfile_banner() {
			return profile_banner;
		}

		public String getProfile_banner_background_color() {
			return profile_banner_background_color;
		}

		public long getViews() {
			return views;
		}

		public String getLanguage() {
			return language;
		}
		
		
	}

	
	public long getStream_ID() {
		return stream_ID;
	}

	public String getGame() {
		return game;
	}

	public int getViewers() {
		return viewers;
	}

	public String getCreated_Date() {
		return created_Date;
	}

	public String getVideo_Height() {
		return video_Height;
	}

	public String getAvg_Fps() {
		return avg_Fps;
	}

	public _links get_links() {
		return _links;
	}

	public Preview getPreview() {
		return preview;
	}

	public Channel getChannel() {
		return channel;
	}

	
}
