package com.gits.smzonelite;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.admob.android.ads.AdListener;
import com.admob.android.ads.AdView;
import com.gits.smzonelite.db.Item;
import com.gits.smzonelite.utils.Util;

public class ListZoneActivity extends Activity implements OnClickListener, OnItemClickListener{
	private Context ctx = this;

	private final int DIALOG_MARKET_MULTIZONE = 0;
	private final int DIALOG_MARKET_AD = 1;
	
	private final int RC_ADD=1;
	private final int RC_EDIT=2;
	private final int RC_SETTING_LOCATION = 3;

	LocationManager lm;

	public static List<Map<String, String>> LIST_ZONE_ACTIVE;

	ItemAdapter adapter;

	private ListView listView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_list);

		listView = (ListView) findViewById(R.id.list);

		Cursor c = managedQuery(Item.CONTENT_URI, null, null, null, Item._ID);
		startManagingCursor(c);

		String[] from = new String[]{Item.TITLE, Item.PHONE_NUMBER, Item.DISTANCE};
		int[] to = new int[]{android.R.id.text1, android.R.id.text2};

		adapter =  new ItemAdapter(this, android.R.layout.simple_list_item_checked, 
				c, from, to);
		listView.setAdapter(adapter);
		listView.setItemsCanFocus(false);
		listView.setOnItemClickListener(this);
		registerForContextMenu(listView);

		findViewById(R.id.btn_add).setOnClickListener(this);
		findViewById(R.id.btn_close_ad).setOnClickListener(this);

		lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		LIST_ZONE_ACTIVE = new ArrayList<Map<String, String>>();

		checkActiveZone();
		
		AdView ad = (AdView) findViewById(R.id.ad);
		ad.setAdListener(adListener);
		
		/*AdManager.setTestDevices( new String[] { 
			      AdManager.TEST_EMULATOR, // Android emulator 
			      "9133F2AF0A1107C4AC3E7E242B6B2E98", // My HTC Desire 
		});*/
	}
	
	private AdListener adListener = new AdListener() {
		
		@Override
		public void onReceiveRefreshedAd(AdView arg0) {
			
		}
		
		@Override
		public void onReceiveAd(AdView arg0) {
			findViewById(R.id.btn_close_ad).setVisibility(View.VISIBLE);
		}
		
		@Override
		public void onFailedToReceiveRefreshedAd(AdView arg0) {
		}
		
		@Override
		public void onFailedToReceiveAd(AdView arg0) {
		}
	};
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("DUMMY", true);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void checkActiveZone(){
		boolean found=false;
		Cursor c = getContentResolver().query(Item.CONTENT_URI, null, Item.IS_ACTIVE +"=1", null, null);
		if(c.moveToFirst()){
			LIST_ZONE_ACTIVE = new ArrayList<Map<String, String>>();
			do {
				//pindahkan ke array of map biar hemar eksekusi query
				Map<String, String> map = new HashMap<String, String>();
				map.put(Item._ID, c.getString(c.getColumnIndex(Item._ID)));
				map.put(Item.TITLE, c.getString(c.getColumnIndex(Item.TITLE)));
				map.put(Item.PHONE_NUMBER, c.getString(c.getColumnIndex(Item.PHONE_NUMBER)));
				map.put(Item.MESSAGE, c.getString(c.getColumnIndex(Item.MESSAGE)));
				map.put(Item.LATITUDE, c.getString(c.getColumnIndex(Item.LATITUDE)));
				map.put(Item.LONGITUDE, c.getString(c.getColumnIndex(Item.LONGITUDE)));
				map.put(Item.RADIUS, c.getString(c.getColumnIndex(Item.RADIUS)));
				LIST_ZONE_ACTIVE.add(map);
			}while(c.moveToNext());
			found = true;
		}
		c.close();

		if(found){
			boolean gps_enabled=false;
			try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}

			if(gps_enabled){
				startService(new Intent(this, GpsService.class));
			}else{
				//gps mati
				AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
				builder.setMessage("GPS provider required to use this application. Do you want to enable it?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Toast.makeText(ctx, "You must enable GPS provider", Toast.LENGTH_SHORT).show();
						((Activity) ctx).startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), RC_SETTING_LOCATION);
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						((Activity)ctx).finish();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		}else{
			if(stopService(new Intent(this, GpsService.class))){
				
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		editZone(id);
	}

	private void editZone(long id){
		Intent intent = new Intent(this, AddZoneActivity.class);
		intent.putExtra("id", id);
		startActivityForResult(intent, RC_EDIT);
	}

	private void deleteZone(long id){
		if(getContentResolver().delete(Item.CONTENT_URI, Item._ID +"="+id, null)>0){
			checkActiveZone();
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_add:
			if(adapter.getCount()<1){
				startActivityForResult(new Intent(this, AddZoneActivity.class), RC_ADD);
			}else{
				showDialog(DIALOG_MARKET_MULTIZONE);
				break;
			}
			break;
		case R.id.btn_close_ad:
			showDialog(DIALOG_MARKET_AD);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setCancelable(true)
		.setTitle("Hello there,")
		.setPositiveButton("Get it from Market!", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Intent myIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("market://details?id=com.gits.smzone"));
				startActivity(myIntent);
			}
		})
		.setNegativeButton("Later", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		
		switch(id) {
		case DIALOG_MARKET_MULTIZONE:
			builder.setMessage("Buy pro version to unlock Multi Zone");
			dialog = builder.create();

			break;
		case DIALOG_MARKET_AD:
			builder.setMessage("Buy pro version to get rid of ad");
			dialog = builder.create();

			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode){
		case RC_ADD:
			checkActiveZone();
			break;
		case RC_EDIT:
			checkActiveZone();
			break;
		case RC_SETTING_LOCATION:
			checkActiveZone();
			break;
		default:
			break;
		}
	}

	class ItemAdapter extends SimpleCursorAdapter{
		public ItemAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to) {
			super(context, layout, c, from, to);
		}


		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.row_item, parent, false);        
			return v;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ToggleButton tgActive = (ToggleButton)view.findViewById(R.id.row_active);
			TextView tvTitle = (TextView)view.findViewById(R.id.row_title);
			TextView tvPhone= (TextView)view.findViewById(R.id.row_phone);
			TextView tvDistance= (TextView)view.findViewById(R.id.row_distance);
			TextView tvAddress= (TextView)view.findViewById(R.id.row_address);
			TextView tvRadius= (TextView)view.findViewById(R.id.row_radius);

			final int id = cursor.getInt(cursor.getColumnIndex(Item._ID));
			String title = cursor.getString(cursor.getColumnIndex(Item.TITLE));
			String phone = cursor.getString(cursor.getColumnIndex(Item.PHONE_NUMBER));
			String distance = cursor.getString(cursor.getColumnIndex(Item.DISTANCE));
			String address = cursor.getString(cursor.getColumnIndex(Item.ADDRESS));
			String radius = cursor.getString(cursor.getColumnIndex(Item.RADIUS));
			final boolean active = Util.intToBool(cursor.getInt(cursor.getColumnIndex(Item.IS_ACTIVE)));
			tvTitle.setText(title);
			tvPhone.setText("To: "+phone);
			if(address.equals(""))
				tvAddress.setVisibility(View.GONE);
			else
				tvAddress.setText(address);
			tvRadius.setText("Radius: "+radius+" meters");
			tgActive.setTag(id);
			tgActive.setChecked(active);
			tgActive.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ToggleButton tb = (ToggleButton) v;
					//simpan state ke sqlite
					ContentValues cv = new ContentValues();
					cv.put(Item.IS_ACTIVE, Util.boolToInt(tb.isChecked()));
					//hapus distance
					cv.put(Item.DISTANCE, "");
					int tag = (Integer) tb.getTag();
					getContentResolver().update(Item.CONTENT_URI, cv, Item._ID +"="+tag, null);
					//cek zone yang aktif
					checkActiveZone();
				}
			});

			//write distance
			String dist = "";
			if(!distance.equals("")){
				Float f;
				int f_int = Math.round(Float.parseFloat(distance));
				f = Float.valueOf(f_int);
				dist = new DecimalFormat("#,##0").format(f) + " m";
			}
			tvDistance.setText(dist);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.menu_about:
			LayoutInflater inflater = (LayoutInflater) this
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View viewA = inflater.inflate(R.layout.activity_about, null);

			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setView(viewA);
			dialog.setTitle("About");
			//dialog.setIcon(R.drawable.icon_dialog);
			dialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.cancel();
				}
			});
			dialog.show();
			return true;
		case R.id.menu_share:
			Util.shareApp(ctx);
			return true;
		case R.id.menu_rate:
			Intent myIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id="+getPackageName()));
			startActivity(myIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.menu_edit:
			editZone(info.id);
			return true;
		case R.id.menu_delete:
			deleteZone(info.id);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	public void onShareClick(View v){
		Util.shareApp(ctx);
	}

}