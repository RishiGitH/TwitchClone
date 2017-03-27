package com.piglettee.services;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class ImageFileReceiver extends ResultReceiver
{
	private Receiver receiver;
	
	public ImageFileReceiver(Handler handler) 
	{
		super(handler);
	}

	public void setReceiver(Receiver receiver)
	{
		this.receiver = receiver;
	}
	
	public interface Receiver {
		public void onReceiveImageResult(int resultCode, Bundle resultData);
	}
	
	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData)
	{
		if(this.receiver != null)
		{
			this.receiver.onReceiveImageResult(resultCode, resultData);
		}
	}
}
