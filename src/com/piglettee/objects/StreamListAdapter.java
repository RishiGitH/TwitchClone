package com.piglettee.objects;

import java.util.ArrayList;
import java.util.List;

import com.piglettee.maxpipes.R;


import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StreamListAdapter extends BaseAdapter 
{
	private final String TAG = "[MaxPipes] GameListAdapter"; 
			
	private List<StreamObject> dataList = new ArrayList<StreamObject>();
	private List<String> imageList = new ArrayList<String>();
	
	private Context context;
	private boolean enableIndicators;
	
	public StreamListAdapter(Context newContext, boolean enableIndicators)
	{
		this.context = newContext;
		this.enableIndicators = enableIndicators;
	}
	
	public void updateDataList(List<StreamObject> objectList)
	{
		this.dataList = objectList;
	}
	public void updateImageList(List<String> objectList)
	{
		this.imageList = objectList;
	}
	

	public int getCount() {
		// TODO Auto-generated method stub
		return dataList.size();
	}


	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return dataList.get(position);
	}


	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}


	public View getView(int position, View convertView, ViewGroup parent) 
	{
		if(convertView==null)
		{
			LayoutInflater inflater = (LayoutInflater) LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.stream_view, parent,false);
		}
		TextView StreamerName = (TextView)convertView.findViewById(R.id.streamName);
		TextView StreamerViews = (TextView)convertView.findViewById(R.id.streamViews);
		ImageView ImageURL = (ImageView)convertView.findViewById(R.id.streamImage_ScreenShot);
		
		final StreamObject tempGame = dataList.get(position);
		if(tempGame.getChannel()!=null)
		{	
			StreamerName.setText(tempGame.getChannel().getName());
			StreamerViews.setText(Integer.toString(tempGame.getViewers()));
			if(!imageList.isEmpty())
			{
				Picasso.with(this.context).load(imageList.get(position)).into(ImageURL);
			}
		}
		return convertView;
	}
	
}
