package com.sunilsahoo.bhagavadgita.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sunilsahoo.bhagavadgita.OnSettingsChangeListener;
import com.sunilsahoo.bhagavadgita.R;
import com.sunilsahoo.bhagavadgita.db.PreferenceUtils;
import com.sunilsahoo.bhagavadgita.utils.Constants;

public class SettingsFragment extends DialogFragment implements
        OnCheckedChangeListener {
    private SeekBar seekBar = null;
    private int progress;
    private TextView fontSizeTV = null;
    private TextView fontSizeTitleTV = null;
    private TextView day_night_title = null;
    private CheckBox readSlokaCB = null;
    private CheckBox showSlokaCB = null;
    private CheckBox enableSpeakCB = null;
    private OnSettingsChangeListener settingsChangeListener = null;
    private static final int SEEKBAR_STEP = 1;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the
            // host
            settingsChangeListener = (OnSettingsChangeListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), getThemeId());        
        final View view = getActivity().getLayoutInflater().inflate(
                R.layout.settings, null);
        dialog.setTitle(getResources().getString(R.string.action_settings));
        progress = PreferenceUtils.getFontSize(getActivity());
        seekBar = (SeekBar) view.findViewById(R.id.fontSizeSB);
        
        readSlokaCB = (CheckBox) view.findViewById(R.id.readSloka);
        readSlokaCB.setChecked(PreferenceUtils.getReadSloka(getActivity()));
        readSlokaCB.setOnCheckedChangeListener(this);
        enableSpeakCB = (CheckBox) view.findViewById(R.id.enableTalkCB);
        enableSpeakCB.setChecked(PreferenceUtils.enableTalk(getActivity()));
        enableSpeakCB.setOnCheckedChangeListener(this);
        showSlokaCB = (CheckBox) view.findViewById(R.id.showSlokaCB);
        showSlokaCB.setChecked(PreferenceUtils.getShowSloka(getActivity()));
        showSlokaCB.setOnCheckedChangeListener(this);
        fontSizeTV = (TextView) view.findViewById(R.id.fontSizeTV);
        fontSizeTitleTV = (TextView) view.findViewById(R.id.fontSizeTitleTV);
        fontSizeTV.setText(String.valueOf(PreferenceUtils
                .getFontSize(getActivity())));
        day_night_title = (TextView) view.findViewById(R.id.day_night_title);
        day_night_title.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PreferenceUtils.setReadMode(getActivity(),
                        !PreferenceUtils.getDayMode(getActivity()));
                setReadModeImg();
                setStyle(STYLE_NORMAL, getThemeId());
                settingsChangeListener
                        .onSettingsChanged(Constants.SettingsItem.READ_MODE);
            }
        });
        setReadModeImg();
        seekBar.setMax(Constants.MAX_FONT_SIZE - Constants.MIN_FONT_SIZE);
        seekBar.setProgress((progress -Constants.MIN_FONT_SIZE)/SEEKBAR_STEP );
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue,
                    boolean fromUser) {
                progress = Constants.MIN_FONT_SIZE + (progresValue * SEEKBAR_STEP);
                progress = progress < Constants.MIN_FONT_SIZE ? Constants.MIN_FONT_SIZE
                        : progress;
                fontSizeTV.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do something here,
                // if you want to do anything at the start of
                // touching the seekbar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                PreferenceUtils.setFontSize(getActivity(), progress);
                fontSizeTV.setText(String.valueOf(progress));
                settingsChangeListener
                        .onSettingsChanged(Constants.SettingsItem.FONT_SIZE);
            }
        });
        updateTextColor(getActivity());
        
        dialog.getWindow().setContentView(view);

        final WindowManager.LayoutParams params = dialog.getWindow()
                .getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;

        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    private void setReadModeImg() {
        if (day_night_title != null) {
            boolean isDay = PreferenceUtils.getDayMode(getActivity());
            int id = isDay ? R.drawable.day_mode : R.drawable.night_mode;
            day_night_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, id, 0);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == readSlokaCB) {
            PreferenceUtils.setReadSloka(getActivity(), isChecked);
            // settingsChangeListener.onSettingsChanged(Constants.SettingsItem.READ_SLOKA);
        } else if (buttonView == showSlokaCB) {
            PreferenceUtils.setShowSloka(getActivity(), isChecked);
            settingsChangeListener
                    .onSettingsChanged(Constants.SettingsItem.SHOW_SLOKA);
        } else if (buttonView == enableSpeakCB) {
            PreferenceUtils.setEnableTalk(getActivity(), isChecked);
             settingsChangeListener.onSettingsChanged(Constants.SettingsItem.ENABLE_SPEAK);
        }
    }
    
    private int getThemeId(){
        return PreferenceUtils.getDayMode(getActivity()) ? android.R.style.Theme_Holo_Light_Dialog: android.R.style.Theme_Holo_Dialog;
    }
    
    private void updateTextColor(Context context){
        int colorId = PreferenceUtils.getDayMode(context) ? R.color.text_black : R.color.text_white;
        fontSizeTitleTV.setTextColor(context.getResources().getColor(colorId));
        fontSizeTV.setTextColor(context.getResources().getColor(colorId));
        day_night_title.setTextColor(context.getResources().getColor(colorId));
        readSlokaCB.setTextColor(context.getResources().getColor(colorId));
        showSlokaCB.setTextColor(context.getResources().getColor(colorId));
        enableSpeakCB.setTextColor(context.getResources().getColor(colorId));
        
        
    }
    
}
