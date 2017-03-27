package com.piglettee.objects;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class GameObject 
{
	private int viewers;
	private int channels;
	
	private Game game;
	
	private final String TAG = "[MaxPipes] GameObject";
	
	public GameObject(JSONObject jsonObject) 
	{
		try
		{
			viewers = (jsonObject.get("viewers").toString().compareToIgnoreCase("null")==0) ? null : jsonObject.getInt("viewers");
			channels = (jsonObject.get("channels").toString().compareToIgnoreCase("null")==0) ? null : jsonObject.getInt("channels");
			game = (jsonObject.get("game") == null) ? null : new Game(jsonObject.getJSONObject("game"));
		}
		catch (JSONException ex) 
		{
			Log.v(TAG, "JSON Exception parsing the main Game data"+ex.getMessage());
		}
	}
	
	public class Game
	{
		private String name;
		private int _id;
		private int giantbomb_id;
		
		private box boxart_Images;
		private logo logoart_Images;
		
		//private _links game_Links;
		
		public Game(JSONObject jsonObject)
		{
			
			try 
			{
				name = (jsonObject.get("name").toString().compareToIgnoreCase("null")==0) ? null : jsonObject.getString("name");
				_id = (jsonObject.get("_id").toString().compareToIgnoreCase("null")==0) ? null : jsonObject.getInt("_id");
				giantbomb_id = (jsonObject.get("giantbomb_id").toString().compareToIgnoreCase("null")==0) ? null : jsonObject.getInt("giantbomb_id");
				
				
				boxart_Images = (jsonObject.getJSONObject("box") == null) ? null : new box(jsonObject.getJSONObject("box"));
				logoart_Images = (jsonObject.getJSONObject("logo") == null) ? null : new logo(jsonObject.getJSONObject("logo"));
			}
			catch (JSONException ex) 
			{
				Log.v(TAG, "JSON Exception parsing some {Gmae} data! "+ ex.getMessage());
			}
		}
		
		/*
		 * Box inner class for containing all the box art image urls at the different resolutions
		 */
		public class box 
		{
			private final String large, medium, small, template;
			
			public box(JSONObject jsonObject) throws JSONException
			{
				large = (jsonObject.get("large").toString().compareToIgnoreCase("null")==0) ? null : jsonObject.getString("large");
				medium = (jsonObject.get("medium").toString().compareToIgnoreCase("null")==0) ? null : jsonObject.getString("medium");
				small = (jsonObject.get("small").toString().compareToIgnoreCase("null")==0) ? null : jsonObject.getString("small");
				template = (jsonObject.get("template").toString().compareToIgnoreCase("null")==0) ? null : jsonObject.getString("template");
			}

			public String getLarge() {
				return large;
			}

			public String getMedium() {
				return medium;
			}

			public String getSmall() {
				return small;
			}

			public String getTemplate() {
				return template;
			}			
		}
		
		/*
		 * Logo inner class is for containing all the logo image urls at different resolutions
		 */
		public class logo
		{
			private final String large, medium, small, template;
			
			public logo(JSONObject jsonObject) throws JSONException
			{
				large = (jsonObject.get("large").toString().compareToIgnoreCase("null")==0) ? null : jsonObject.getString("large");
				medium = (jsonObject.get("medium").toString().compareToIgnoreCase("null")==0) ? null : jsonObject.getString("medium");
				small = (jsonObject.get("small").toString().compareToIgnoreCase("null")==0) ? null : jsonObject.getString("small");
				template = (jsonObject.get("template").toString().compareToIgnoreCase("null")==0) ? null : jsonObject.getString("template");
			}

			public String getLarge() {
				return large;
			}

			public String getMedium() {
				return medium;
			}

			public String getSmall() {
				return small;
			}

			public String getTemplate() {
				return template;
			}
		}

		public String getName() {
			return name;
		}

		public int get_id() {
			return _id;
		}

		public int getGiantbomb_id() {
			return giantbomb_id;
		}

		public box getBoxart_Images() {
			return boxart_Images;
		}

		public logo getLogoart_Images() {
			return logoart_Images;
		}
	}

	public int getViewers() {
		return viewers;
	}

	public int getChannels() {
		return channels;
	}

	public Game getGame() {
		return game;
	}
	
	@Override
	public String toString(){
		return "Game name: "+this.getGame().getName()+"\nViewers: "+this.getViewers()+"\nChannels: "+this.getChannels()+"\nBoxArt: "+this.getGame().getBoxart_Images().getLarge();
	}
}
