package com.sunilsahoo.bhagavadgita.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.sunilsahoo.bhagavadgita.R;

public class InfoFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(),
                android.R.style.Theme_Translucent_NoTitleBar);
        final View view = getActivity().getLayoutInflater().inflate(
                R.layout.help_overlay, null);
        final Drawable d = new ColorDrawable(getResources().getColor(
                R.color.settings_bg));
         d.setAlpha(220);

        dialog.getWindow().setBackgroundDrawable(d);
        dialog.getWindow().setContentView(view);
        RelativeLayout layout = (RelativeLayout) view
                .findViewById(R.id.overlayLayout);
        WebView webView = (WebView) view.findViewById(R.id.info);
        webView.setBackgroundColor(0);
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.loadUrl("file:///android_asset/help.html");
        Button okBtn = (Button) view.findViewById(R.id.ok);
        okBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dismiss();
            }
        });
        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dismiss();
            }
        });
        /*
         * final WindowManager.LayoutParams params =
         * dialog.getWindow().getAttributes(); params.width =
         * WindowManager.LayoutParams.WRAP_CONTENT; params.height =
         * WindowManager.LayoutParams.WRAP_CONTENT; params.gravity =
         * Gravity.CENTER;
         */

        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
    
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d("Info", "Inside Dismiss");
    }

}
