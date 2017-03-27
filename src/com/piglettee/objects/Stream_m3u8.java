package com.piglettee.objects;

import java.util.HashMap;
import java.util.Map;


public class Stream_m3u8 
{
	private HashMap<String, Stream_m3u8_playlist> mapOfStreamURLs;

	private final String videoPattern = "VIDEO=";
	private final String httpPattern = "http:";
	
	public Stream_m3u8(Map<String,Integer> map)
	{
		mapOfStreamURLs = new HashMap<String,Stream_m3u8_playlist>();
		
		//HashMap<String, Integer> map = M3U8_Parser.parseHLSMetadata(fileArray);
		for(Object key : map.keySet())
		{
			//Stream_m3u8_playlist newResURL;
			String lineData = key.toString();
			//Log.v(TAG,lineData);
			String quality = lineData.substring(lineData.indexOf(videoPattern)+videoPattern.length()+1,lineData.indexOf(httpPattern)-1);
			if(quality.contains("chunked"))
			{ 
				mapOfStreamURLs.put("source", new Stream_m3u8_playlist(key.toString()));
			}
			else if(quality.contains("high")){
				mapOfStreamURLs.put("high", new Stream_m3u8_playlist(key.toString()));
			}
			else if(quality.contains("medium")){
				mapOfStreamURLs.put("medium", new Stream_m3u8_playlist(key.toString()));
			}
			else if(quality.contains("low")){
				mapOfStreamURLs.put("low", new Stream_m3u8_playlist(key.toString()));
			}
			else if(quality.contains("mobile")){
				mapOfStreamURLs.put("mobile", new Stream_m3u8_playlist(key.toString()));
			}
			else if(quality.contains("audio_only")){
				mapOfStreamURLs.put("audio_only", new Stream_m3u8_playlist(key.toString()));
			}
		}
	}
	
	public Stream_m3u8_playlist getResURL_Object(String key)
	{
		return this.mapOfStreamURLs.get(key);
	}
	/*
	 * This is the inner class for holding the usable data 
	 * of the parsed m3u8 file from the stream.
	 */
	public class Stream_m3u8_playlist
	{
		private String Quality;
		private String uri_String;
		private String Rest;
		
		
		
		public Stream_m3u8_playlist(String data)
		{
			Rest = data.substring(0, data.indexOf(videoPattern));
			Quality = (data.substring(data.indexOf(videoPattern)+videoPattern.length()+1,data.indexOf(httpPattern)-1));
			if(Quality.compareToIgnoreCase("chunked")==0)
			{
				Quality = "source";
			}
			uri_String = data.substring(data.indexOf(httpPattern));
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
}