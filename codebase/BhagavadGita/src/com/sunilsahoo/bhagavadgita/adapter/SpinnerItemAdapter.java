package com.sunilsahoo.bhagavadgita.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sunilsahoo.bhagavadgita.R;


public class SpinnerItemAdapter extends ArrayAdapter<String>{

    public SpinnerItemAdapter(Context context, String[] gamepadSpinnerArr) {
        super(context, R.layout.spinner_title, gamepadSpinnerArr);
    }

    @Override //don't override if you don't want the default spinner to be a two line view
    public View getView(int position, View convertView, ViewGroup parent) {
        return initSpinnerTitleView(position, convertView);
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return initView(position, convertView);
    }

    private View initView(int position, View convertView) {
        if(convertView == null)
            convertView = View.inflate(getContext(),
                                       R.layout.menu_drawer_options,
                                       null);
        TextView tvText1 = (TextView)convertView.findViewById(R.id.menu_iem);
        tvText1.setText(getItem(position));
        return convertView;
    }
    
    private View initSpinnerTitleView(int position, View convertView) {
        if(convertView == null)
            convertView = View.inflate(getContext(),
                                       R.layout.spinner_title,
                                       null);
        TextView tvText1 = (TextView)convertView.findViewById(R.id.spinner_title);
        tvText1.setText(getItem(position));
        return convertView;
    }
}