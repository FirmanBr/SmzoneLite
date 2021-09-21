package com.gits.smzonelite.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class Item implements BaseColumns {
	public static final Uri CONTENT_URI = Uri.parse("content://"
            + MyContentProvider.AUTHORITY + "/items");

	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.gits.smzonelite";
			
	public static String _ID = "_id";
	public static String TITLE = "title";
	public static String MESSAGE = "message";
	public static String PHONE_NUMBER = "phone_number";
	public static String LATITUDE = "latitude";
	public static String LONGITUDE = "longitude";
	public static String ADDRESS = "address";
	public static String RADIUS = "radius";
	public static String IS_ACTIVE = "isActive";
	
	public static String DISTANCE = "distance";
}
