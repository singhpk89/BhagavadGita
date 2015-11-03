package com.sunilsahoo.bhagavadgita.view;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.sunilsahoo.bhagavadgita.OnMenuDrawerItemSelectionListener;
import com.sunilsahoo.bhagavadgita.R;
import com.sunilsahoo.bhagavadgita.utils.Log;


public class SettingsMenuDrawer implements OnItemClickListener,
        OnDismissListener {
    private static String TAG = SettingsMenuDrawer.class.getName();
    private static int EXTRA_MARGIN_MENU_OPTION = 50;
    private Context context = null;
    private PopupWindow pw = null;
    private static boolean isPopupOpen = false;
    private OnMenuDrawerItemSelectionListener menuDrawerItemListener = null;
    private static MenuDrawerAdapter menuListAdapter = null;
    private static List<String> itemList = null;

    private SettingsMenuDrawer(Context context, OnMenuDrawerItemSelectionListener menuDrawerItemListener) {
        this.context = context;
        this.menuDrawerItemListener = menuDrawerItemListener;
    }

    public class MenuDrawerAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final List<String> values;

        class ViewHolder {
            public TextView text;
        }

        private MenuDrawerAdapter(Context context, List<String> values) {
            super(context, R.layout.menu_drawer_options, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.menu_drawer_options, null,
                        false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.text = (TextView) rowView
                        .findViewById(R.id.menu_iem);
                rowView.setTag(viewHolder);
            }

            ViewHolder holder = (ViewHolder) rowView.getTag();
            holder.text.setText(values.get(position));

            return rowView;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
            long arg3) {
        closeMenuDrawer();
        menuDrawerItemListener.onMenuDrawerItemSelected(position);
    }

    private void closeMenuDrawer() {
        if (pw != null) {
            pw.dismiss();
        }
        isPopupOpen = false;
    }
    
    /**
     * Description: Returns width of Widest View of list view
     * 
     * @param context
     * @param adapter
     * @return
     */
    private int getWidestView(Context context, Adapter adapter) {
        int maxWidth = 0;
        View view = null;
        FrameLayout fakeParent = new FrameLayout(context);
        for (int i = 0, count = adapter.getCount(); i < count; i++) {
            view = adapter.getView(i, view, fakeParent);
            view.measure(View.MeasureSpec.UNSPECIFIED,
                    View.MeasureSpec.UNSPECIFIED);
            int width = view.getMeasuredWidth();
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        return maxWidth;
    }

    /**
     * OPens Menu Drawer
     * 
     * @param rootView
     */
    private void openMenuDrawer(View rootView, int leftMargin, int topMargin,
            int itemsArr) {
        try {
            isPopupOpen = true;
            itemList = Arrays.asList(context.getResources().getStringArray(itemsArr));
            menuListAdapter = new MenuDrawerAdapter(context, itemList);

            LinearLayout listViewContainer = new LinearLayout(context);
            LinearLayout.LayoutParams listViewContainerLP = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            listViewContainer.setLayoutParams(listViewContainerLP);
            listViewContainer.setBackgroundResource(R.drawable.text_bg);
            ListView menuList = new ListView(context);
            menuList.setFadingEdgeLength(0);
            menuList.setVerticalFadingEdgeEnabled(false);
            menuList.setVerticalScrollBarEnabled(false);
            menuList.setHorizontalScrollBarEnabled(false);
            menuList.setOnItemClickListener(this);
            listViewContainer.addView(menuList);
            // set width of list view
            menuList.getLayoutParams().width = getWidestView(context,
                    menuListAdapter) + EXTRA_MARGIN_MENU_OPTION;

            menuList.setAdapter(menuListAdapter);

            pw = new PopupWindow(context);
            pw.setTouchable(true);
            pw.setFocusable(true);
            pw.setOutsideTouchable(true);
            pw.setOnDismissListener(this);

            pw.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
            pw.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            pw.setOutsideTouchable(false);
            pw.setContentView(listViewContainer);
            pw.showAsDropDown(rootView, leftMargin, topMargin);

        } catch (Exception ex) {
            isPopupOpen = false;
            Log.e(TAG, "Error in Opening Menu Drawer :" + ex.getMessage());
        }
    }
    
    public static void updatemenuDrawer(int position, String item){
        itemList.set(position, item);
        if(menuListAdapter!= null){
        menuListAdapter.notifyDataSetChanged();
        }
    }
    
    public static void updatemenuDrawer(){
        if(menuListAdapter!= null){
        menuListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Description: Display Menu drawer returns true if success and false if not
     * success
     * 
     * @param context
     * @param rootView
     * @param leftMargin
     * @param topMargin
     * @return
     */

    public static boolean showMenuDrawer(Context context, OnMenuDrawerItemSelectionListener menuDrawerItemListener, View rootView,
            int leftMargin, int topMargin, int itemsArr) {
        Log.i(TAG, "SHOW MENU DRAWER :" + leftMargin + " :" + topMargin);
        if (!isPopupOpen) {
            new SettingsMenuDrawer(context, menuDrawerItemListener).openMenuDrawer(rootView,
                    leftMargin, topMargin, itemsArr);
        }
        return isPopupOpen;
    }

    @Override
    public void onDismiss() {
        closeMenuDrawer();
    }
    

}
