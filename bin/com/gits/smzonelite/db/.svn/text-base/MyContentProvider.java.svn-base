package com.gits.smzonelite.db;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class MyContentProvider extends ContentProvider{
	private static final String TAG = "MyContentProvider";

	private static final String DATABASE_NAME = "smszone.db";

	private static final int DATABASE_VERSION = 1;

	private static final String TABLE_NAME = "items";

	public static final String AUTHORITY = "com.gits.smzonelite.contentprovider";

	private static final UriMatcher sUriMatcher;

	private static HashMap<String, String> itemsProjectionMap;

	private static final int ITEMS = 1;


	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + Item._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT," + Item.TITLE+ " TEXT,"
					+ Item.MESSAGE+ " TEXT," + Item.PHONE_NUMBER+ " TEXT,"
					+ Item.LATITUDE + " TEXT," + Item.LONGITUDE+ " TEXT," 
					+ Item.ADDRESS + " TEXT,"  + Item.RADIUS + " INTEGER,"
					+ Item.IS_ACTIVE+ " INTEGER,"+ Item.DISTANCE+ " TEXT"+ ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
					+ ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}

	}


	private DatabaseHelper dbHelper;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case ITEMS:
			count = db.delete(TABLE_NAME, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;

	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case ITEMS:
			return Item.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (sUriMatcher.match(uri) != ITEMS) 
		{ throw new IllegalArgumentException("Unknown URI " + uri); }
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long rowId;
		switch (sUriMatcher.match(uri)) {
		case ITEMS:
			rowId = db.replace(TABLE_NAME, "", values);
			if (rowId > 0) {
				Uri itemUri = ContentUris.withAppendedId(Item.CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(itemUri, null);
				return itemUri;
			}
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DatabaseHelper(getContext());
		return true;

	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch (sUriMatcher.match(uri)) {
		case ITEMS:
			qb.setTables(TABLE_NAME);
			qb.setProjectionMap(itemsProjectionMap);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count = 0;
		switch (sUriMatcher.match(uri)){
		case ITEMS:
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			count = db.update(
					TABLE_NAME, 
					values,
					selection, 
					selectionArgs);
			break;
		default: throw new IllegalArgumentException(
				"Unknown URI " + uri);    
		}       
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	public boolean isUpdated(){
		return true;
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, TABLE_NAME, ITEMS);

		itemsProjectionMap = new HashMap<String, String>();
		itemsProjectionMap.put(Item._ID, Item._ID);
		itemsProjectionMap.put(Item.TITLE, Item.TITLE);
		itemsProjectionMap.put(Item.MESSAGE, Item.MESSAGE);
		itemsProjectionMap.put(Item.PHONE_NUMBER, Item.PHONE_NUMBER);
		itemsProjectionMap.put(Item.LATITUDE, Item.LATITUDE);
		itemsProjectionMap.put(Item.LONGITUDE, Item.LONGITUDE);
		itemsProjectionMap.put(Item.ADDRESS, Item.ADDRESS);
		itemsProjectionMap.put(Item.RADIUS, Item.RADIUS);
		itemsProjectionMap.put(Item.IS_ACTIVE, Item.IS_ACTIVE);
		itemsProjectionMap.put(Item.DISTANCE, Item.DISTANCE);
	}

}

