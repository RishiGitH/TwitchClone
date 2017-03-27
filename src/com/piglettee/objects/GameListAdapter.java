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

public class GameListAdapter extends BaseAdapter 
{
	private final String TAG = "[MaxPipes] GameListAdapter"; 
	private Context context;
	
	private List<GameObject> dataList = new ArrayList<GameObject>();
	private List<String> imageList = new ArrayList<String>();
	
	public GameListAdapter(Context passedContext, boolean indicatorsEnabled)
	{
		this.context = passedContext;
		Picasso.with(passedContext).setIndicatorsEnabled(indicatorsEnabled);
	}
	
	public void updateDataList(List<GameObject> objectList)
	{
		this.dataList = objectList;
	}
	public void resetLists()
	{
		dataList.clear();
		imageList.clear();
	}
	public void updateImageList(List<String> objectList)
	{
		this.imageList = (List<String>) objectList;
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
			convertView = inflater.inflate(R.layout.game_view, parent,false);
		}
		TextView GameName = (TextView)convertView.findViewById(R.id.gameName);
		TextView GameViews = (TextView)convertView.findViewById(R.id.gameViews);
		TextView GameChannels = (TextView)convertView.findViewById(R.id.gameChannels);
		ImageView ImageURL = (ImageView)convertView.findViewById(R.id.gameImage);
		
		final GameObject tempGame = dataList.get(position);
		if(tempGame.getGame()!=null)
		{	
			GameName.setText(tempGame.getGame().getName());
			GameViews.setText(Integer.toString(tempGame.getViewers()));
			GameChannels.setText(Integer.toString(tempGame.getChannels()));
			if(!imageList.isEmpty())
			{
				Picasso.with(this.context).load(imageList.get(position)).into(ImageURL);
				//ImageURL.setImageBitmap(imageList.get(position));
			}
		}
		return convertView;
	}
	
}
