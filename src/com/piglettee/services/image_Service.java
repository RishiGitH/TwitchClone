package com.piglettee.services;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;

public class image_Service extends IntentService
{
	private final String TAG = "[MaxPipes] image_Service";
	public image_Service()
	{
		super("image_Service");
		
	}
	
	public image_Service(String name)
	{
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) 
	{
		final ResultReceiver receiver = intent.getParcelableExtra("receiver");
		
		String command = intent.getStringExtra("command");
		String list = intent.getStringExtra("list");
		System.out.println(list);
		ArrayList<String> data = intent.getStringArrayListExtra("imageUrls");
		ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
		Bundle bundle = new Bundle();
		Log.v(TAG, "Imgae Service started!");
		
		receiver.send(STATUS.RUNNING, bundle.EMPTY);
		//String[] urlStrings = data.split("|");
		for(String imageURL : data)
		{
			try 
			{
				bitmapArray.add(getImageBitmap(imageURL));
			}
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				receiver.send(STATUS.ERROR, bundle.EMPTY);
				Log.v(TAG, "Exception getting image!");
			}
		}
		
		bundle.putParcelableArrayList("images", bitmapArray);
		if( list != null && list.equalsIgnoreCase("streams"))
		{
			bundle.putString("list", list.toString());
		}
		receiver.send(STATUS.FINISHED, bundle);
		
	}
	
	private Bitmap getImageBitmap(String url) throws IOException
	{
		Bitmap bitmap = null;
		URL aURL = new URL(url);
		URLConnection conn = aURL.openConnection();
		conn.connect();
		InputStream is = conn.getInputStream();
		BufferedInputStream bis = new BufferedInputStream(is);
		bitmap = BitmapFactory.decodeStream(bis);
		bis.close();
		is.close();

		return bitmap;
	}
	
	
}
