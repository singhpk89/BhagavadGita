/**
 * 
 */
package com.sunilsahoo.bhagavadgita.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sunilsahoo.bhagavadgita.R;
import com.sunilsahoo.bhagavadgita.beans.Chapter;
import com.sunilsahoo.bhagavadgita.beans.Item;
import com.sunilsahoo.bhagavadgita.beans.Quote;
import com.sunilsahoo.bhagavadgita.db.PreferenceUtils;
import com.sunilsahoo.bhagavadgita.utils.Utility;

/**
 * @author sunilsahoo
 * 
 */
public class QuoteAdapter extends BaseAdapter {

    private ArrayList<Item> data;
    private LayoutInflater l_Inflater;
    private boolean isExpand = false;
    private String fragmentType = null;
    private Context context = null;

    public QuoteAdapter(Context context, String fragmentType,
            ArrayList<Item> data) {
        this.data = data;
        this.context = context;
        l_Inflater = LayoutInflater.from(context);
        this.fragmentType = fragmentType;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int pos) {

        return data.get(pos);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Item item = data.get(position);
        final Quote quote;
        Chapter chapter = null;
        TextView quoteTV = null;
        CheckBox favCB = null;
        if (Utility.isChapterFragment(fragmentType)) {
            convertView = l_Inflater.inflate(R.layout.chapter_index_body, null);
            quoteTV = (TextView) convertView.findViewById(R.id.chapter);
            Utility.setTextColor(quoteTV, context);
        } else {
            if (item instanceof Quote) {
                convertView = l_Inflater.inflate(R.layout.quote_index_body,
                        null);

                quoteTV = (TextView) convertView.findViewById(R.id.qi_content);
                Utility.setTextColor(quoteTV, context);
                favCB = (CheckBox) convertView.findViewById(R.id.qi_fav_cbx);
            } else {
                convertView = l_Inflater.inflate(R.layout.section_header, null);
                quoteTV = (TextView) convertView.findViewById(R.id.chapter);
                convertView.setEnabled(false);
                convertView.setClickable(false);
                convertView.setFocusable(false);
            }
        }
        if(quoteTV != null){
            quoteTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, PreferenceUtils.getFontSize(context));
        }
        if (item instanceof Quote) {
            quote = (Quote) item;
            if (quote.isFavourite() == 1) {
                favCB.setChecked(true);
            } else if (quote.isFavourite() == 0) {
                favCB.setChecked(false);
            }

            favCB.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    onClickCheckBoxListener.OnClick(v, quote, position);
                }
            });
            quoteTV.setText(quote.getChapterNo()<= 0 ? quote.getBody() : quote.getChapterNo() + "(" + quote.getTextId()+")"
                    + " : " + quote.getBody());
        } else {
            chapter = (Chapter) item;
            
            quoteTV.setText(chapter.getId()<= 0 ? chapter.getTitle() :chapter.getId() + " : " + chapter.getTitle());
        }
        if (!this.isExpand && !Utility.isChapterFragment(fragmentType)) {
            quoteTV.setMaxLines(2);
            quoteTV.setEllipsize(TruncateAt.END);
        } else {
            quoteTV.setMaxLines(30);
            quoteTV.setEllipsize(null);
        }

        return convertView;
    }

    /**
     * @param data
     *            the data to set
     */
    public void setData(ArrayList<Item> data) {
        this.data = data;
    }

    /**
     * @param isExpand
     *            the isExpand to set
     */
    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
    }

    /**
     * @return the isExpand
     */
    public boolean isExpand() {
        return isExpand;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.BaseAdapter#notifyDataSetChanged()
     */
    @Override
    public void notifyDataSetChanged() {

        super.notifyDataSetChanged();
    }

    // interface Onclick checbox
    public interface OnClickCheckBoxListener {
        public void OnClick(View v, Item item, int pos);
    }

    OnClickCheckBoxListener onClickCheckBoxListener;

    /**
     * Set onclick image event
     * 
     * @param onClickcheckboxListener
     */
    public void setOnClickCheckBoxListener(
            OnClickCheckBoxListener onClickCheckBoxListener) {
        this.onClickCheckBoxListener = onClickCheckBoxListener;
    }
}
