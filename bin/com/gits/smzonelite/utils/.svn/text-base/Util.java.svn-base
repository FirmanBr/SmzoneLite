package com.gits.smzonelite.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.gits.smzonelite.R;

public class Util {
	public static String PREF_SMZONE = "smzone";
	public static String PREF_KEY_IS_SERVICE_STARTED= "is_service_started";
	
	public static boolean intToBool(int val){
		return(val>0);
	}

	public static int boolToInt(boolean val){
		if(val)
			return 1;
		else
			return 0;
	}
	
	public static final void shareApp(Context c) {
		Intent send = new Intent(Intent.ACTION_SEND);
		send.setType("text/plain");
		send.putExtra(Intent.EXTRA_TEXT, c.getString(R.string.share_app));
		//send.putExtra(Intent.EXTRA_SUBJECT, title);
		try {
			c.startActivity(Intent.createChooser(send, "Share via"));
		} catch(android.content.ActivityNotFoundException ex) {
			// if no app handles it, do nothing
			Toast.makeText(c, R.string.share_not_found, Toast.LENGTH_SHORT);
		}
	}
}
