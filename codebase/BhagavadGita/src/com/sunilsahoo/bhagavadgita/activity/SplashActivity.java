/**
 * 
 */
package com.sunilsahoo.bhagavadgita.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;

import com.sunilsahoo.bhagavadgita.R;
import com.sunilsahoo.bhagavadgita.utils.Constants;
import com.sunilsahoo.bhagavadgita.utils.Log;

/**
 * @author sunilsahoo
 * 
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash_screen);
        new ChangeActivity().execute();
    }

    private class ChangeActivity extends AsyncTask<Object, Object, Object> {

        private static final String TAG = "ChangeActivity";

        @Override
        protected Object doInBackground(Object... params) {
            try {
                Thread.sleep(Constants.SPLASH_SCREEN_DURATION);
            } catch (InterruptedException e) {
                Log.w(TAG, "Exception in Splash : " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {

            Intent intent = new Intent();
            intent.setClass(SplashActivity.this, BhagavadGitaMainActivity.class);
            startActivity(intent);
            // finish the current activity
            SplashActivity.this.finish();
            super.onPostExecute(result);
        }

    }

}
