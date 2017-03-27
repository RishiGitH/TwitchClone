package com.piglettee.services;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/*
 * This was an example template found on: Stackoverflow for accessing the
 * result of the intentService
 * 
 * @ http://stackoverflow.com/questions/3197335/restful-api-service
 */

public class ServiceResultsReceiver extends ResultReceiver 
{
	private Receiver receiver;
	
	public ServiceResultsReceiver(Handler handler) 
	{
		super(handler);
	}

	public void setReceiver(Receiver receiver)
	{
		this.receiver = receiver;
	}
	
	public interface Receiver {
		public void onReceiveResult(int resultCode, Bundle resultData);
	}
	
	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData)
	{
		if(this.receiver != null)
		{
			this.receiver.onReceiveResult(resultCode, resultData);
		}
	}
}
