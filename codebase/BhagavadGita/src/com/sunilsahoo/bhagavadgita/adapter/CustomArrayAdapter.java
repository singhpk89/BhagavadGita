/**
 * 
 */
package com.sunilsahoo.bhagavadgita.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author sunilsahoo
 *
 */
public class CustomArrayAdapter extends ArrayAdapter<String>{

	private LayoutInflater l_Inflater;
	
	/**
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 */
	public CustomArrayAdapter(Context context, int textViewResourceId,
			String[] objects) {
		super(context, textViewResourceId, objects);
		l_Inflater = LayoutInflater.from(context);
	}

	/**
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 */
	public CustomArrayAdapter(Context context, int textViewResourceId,
			List<String> objects) {
		super(context, textViewResourceId, objects);
		l_Inflater = LayoutInflater.from(context);
	}

	/**
	 * @param context
	 * @param resource
	 * @param textViewResourceId
	 * @param objects
	 */
	public CustomArrayAdapter(Context context, int resource,
			int textViewResourceId, String[] objects) {
		super(context, resource, textViewResourceId, objects);
		l_Inflater = LayoutInflater.from(context);
	}

	/**
	 * @param context
	 * @param resource
	 * @param textViewResourceId
	 * @param objects
	 */
	public CustomArrayAdapter(Context context, int resource,
			int textViewResourceId, List<String> objects) {
		super(context, resource, textViewResourceId, objects);
		l_Inflater = LayoutInflater.from(context);
	}

	/**
	 * @param context
	 * @param resource
	 * @param textViewResourceId
	 */
	public CustomArrayAdapter(Context context, int resource,
			int textViewResourceId) {
		super(context, resource, textViewResourceId);
		l_Inflater = LayoutInflater.from(context);
	}

	/**
	 * @param context
	 * @param textViewResourceId
	 */
	public CustomArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		l_Inflater = LayoutInflater.from(context);
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getDropDownView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		
		 if (convertView == null) {          
		        View view = l_Inflater.inflate(android.R.layout.simple_list_item_single_choice, parent, false);
//		        view.setBackgroundColor(Color.WHITE);
		        ((TextView)view).setSingleLine();
		        ((TextView)view).setEllipsize(TruncateAt.END);
		        ((TextView)view).setTextColor(Color.BLACK);
		        convertView = view;
		    }
		return super.getDropDownView(position, convertView, parent);
	}
}
