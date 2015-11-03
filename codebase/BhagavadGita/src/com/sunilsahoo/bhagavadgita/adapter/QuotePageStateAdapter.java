package com.sunilsahoo.bhagavadgita.adapter;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sunilsahoo.bhagavadgita.beans.Item;
import com.sunilsahoo.bhagavadgita.utils.Constants;
import com.sunilsahoo.bhagavadgita.view.QuoteBodyFragment;

/**
 * Adapter class
 * 
 * This adapter class sets up GridFragment objects to be displayed by a
 * ViewPager.
 */

public class QuotePageStateAdapter extends FragmentStatePagerAdapter {

	private ArrayList<Item> totalGridList = null;
	private String fragType = null;

	/**
	 * Return a new adapter.
	 */

	public QuotePageStateAdapter(FragmentManager fm, String fragType,
			ArrayList<Item> totalGridList) {

		super(fm);
		this.fragType = fragType;
		this.totalGridList = totalGridList;
	}

	/**
	 * Get the number of fragments to be displayed in the ViewPager.
	 */

	@Override
	public int getCount() {
		return totalGridList.size();
	}

	@Override
	public Fragment getItem(int position) {
	    QuoteBodyFragment fragment = new QuoteBodyFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.Bundle_quote, totalGridList.get(position));
        bundle.putString(Constants.FRAG_TYPE, fragType);
        fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

} // end class MyAdapter
