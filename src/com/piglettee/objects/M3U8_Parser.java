package com.piglettee.objects;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class M3U8_Parser 
{
	/*
	 * This is a utility method for parsing the returned m3u8 file
	 * and returning the required URI lines in a HashMap
	 */
	public static HashMap<String, Integer> parseHLSMetadata(String[] data)
	{  
        String line;
        HashMap<String, Integer> segmentsMap = null;
        String digitRegex = "\\d+";
        Pattern p = Pattern.compile(digitRegex);

        for(int index = 1; index < data.length; index++)
        {
        	line = data[index];
            if(line.equals("#EXTM3U"))
            { //start of m3u8
                segmentsMap = new HashMap<String, Integer>();
            }
            else if(line.contains("#EXT-X-STREAM-INF"))
            { 
            	//once found EXTINFO use runner to get the next line which contains the media file, parse duration of the segment
                Matcher matcher = p.matcher(line);
                matcher.find(); //find the first matching digit, which represents the duration of the segment, dont call .find() again that will throw digit which may be contained in the description.
                segmentsMap.put(data[index], Integer.parseInt(matcher.group(0)));
            }
        }
        return segmentsMap;
    }	
}
	
