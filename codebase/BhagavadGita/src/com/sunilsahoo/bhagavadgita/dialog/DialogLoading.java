/**
 * 
 */
package com.sunilsahoo.bhagavadgita.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;

import com.sunilsahoo.bhagavadgita.utils.Log;


/**
 * @author sunilsahoo
 *
 */
public class DialogLoading {

	
	protected static final String TAG = "DialogLoading";

    public static ProgressDialog Loading(Context context, String mess){
		
		ProgressDialog dialogdownload = new ProgressDialog(context);
	 	dialogdownload.setMessage(mess);
	 	dialogdownload.setIndeterminate(true);
	 	dialogdownload.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(event.getAction()==KeyEvent.ACTION_DOWN)
				{
					if (keyCode == KeyEvent.KEYCODE_BACK) 
					{
						Log.d(TAG, "back action in dialog");
						return true;
					}	
					if(keyCode == KeyEvent.KEYCODE_MENU)
					{
						return true;
					}
				}
				return false;
			}
		});
       return dialogdownload;
       
		
	}

}
