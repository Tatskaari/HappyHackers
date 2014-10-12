package com.happyhacker.ourmarket;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListItem extends ArrayAdapter<String>{
	private final Activity context;
	private final String[] web;
	private final JSONArray jArray;
	private OurMarketActivity ui;
	
	public ListItem(Activity context, String[] web, JSONArray jArray, OurMarketActivity ui) {
		super(context, R.layout.list_item, web);
		this.context = context;
		this.web = web;
		this.jArray = jArray;
		this.ui = ui;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView= inflater.inflate(R.layout.list_item, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.title);
		TextView txtContent = (TextView) rowView.findViewById(R.id.content);
		
		
		ImageView img = (ImageView) rowView.findViewById(R.id.image);
		try {
			JSONObject jObj = jArray.getJSONObject(position);
			
			txtContent.setText(jObj.getString("stall_description"));
			txtTitle.setText(web[position]);
			
			ImageLoader imageLoader = new ImageLoader(img, ui);
			imageLoader.execute(jObj.getString("image_url"));
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return rowView;
	}
	
	public JSONObject getJSON(int index) throws JSONException{
		return jArray.getJSONObject(index);
	}
	
	class ImageLoader extends AsyncTask<String, Void, Bitmap> {
		ImageView img;
		OurMarketActivity ui;
		
		public ImageLoader(ImageView img, OurMarketActivity ui){
			this.img = img;
			this.ui = ui;
		}
		
		
		void setImage(){
			
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			try {
				URL imgUrl = new URL(params[0]);
				final Bitmap bmp = BitmapFactory.decodeStream(imgUrl.openConnection().getInputStream());
				ui.runOnUiThread(new Runnable() {
				     @Override
				     public void run() {
				    	 img.setImageBitmap(bmp);
				    	 img.setVisibility(ImageView.VISIBLE);
				    	 View v = (View) img.getParent();
				    	 v.findViewById(R.id.loadingPanel).setVisibility(v.GONE);
				     }
				});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
}
