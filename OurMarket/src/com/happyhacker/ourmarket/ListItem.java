package com.happyhacker.ourmarket;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListItem extends ArrayAdapter<String>{
	private final Activity context;
	private final String[] web;
	private final String[] content;
	
	public ListItem(Activity context, String[] web, String[] content) {
		super(context, R.layout.list_item, web);
		this.context = context;
		this.web = web;
		this.content = content;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView= inflater.inflate(R.layout.list_item, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.title);
		TextView txtContent = (TextView) rowView.findViewById(R.id.content);
		txtTitle.setText(web[position]);
		txtContent.setText(content[position]);
		return rowView;
	}
}
