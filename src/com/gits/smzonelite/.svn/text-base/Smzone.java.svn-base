package com.gits.smzonelite;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.gits.smzonelite.utils.Util;

public class Smzone extends Activity {
	private Context ctx = this;

	private final int DIALOG_MARKET = 0;

	protected int splashTime = 3000; // time to display the splash screen in ms

	private SharedPreferences sharedPreferences;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.splash);

		sharedPreferences = getSharedPreferences(Util.PREF_SMZONE, Context.MODE_PRIVATE);

		if(!sharedPreferences.getBoolean(Util.PREF_KEY_IS_SERVICE_STARTED, false)){
			startSplash();
		}else{
			removeSplash();
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("DUMMY", true);
	}

	private void startSplash(){
		//start splash screen
		// thread for displaying the SplashScreen
		Thread splashTread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;
					while(waited < splashTime) {
						sleep(100);
						//if(splashActive) {
							waited += 100;
							//}
					}
				} catch(InterruptedException e) {
					// do nothing
				} finally {
					//hilangkan splash screen dan bikin ga fullscreen
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// go non-full screen
							removeSplash();
						}
					});
					stop();
				}
			}
		};
		splashTread.start();
	}
	
	private void removeSplash(){
		finish();
		Intent intent = new Intent(ctx, ListZoneActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch(id) {
		case DIALOG_MARKET:
			//gps mati
			AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
			builder.setMessage("You are not allowed to use this application.")
			.setCancelable(false)
			.setPositiveButton("Get it from Market!", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Intent myIntent = new Intent(Intent.ACTION_VIEW,
							Uri.parse("market://details?id=com.gits.smszone"));
					startActivity(myIntent);
				}
			})
			.setNegativeButton("Later", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
					((Activity)ctx).finish();
				}
			});
			dialog = builder.create();

			break;
		default:
			dialog = null;
		}
		return dialog;
	}
}