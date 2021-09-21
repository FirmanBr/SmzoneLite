package com.gits.smzonelite;

import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.gits.smzonelite.db.Item;
import com.gits.smzonelite.utils.Util;

public class GpsService extends Service{
	//private Context ctx = this;
	private SharedPreferences sharedPreferences;
	private NotificationManager mNM;

	private LocationManager locationManager;
	private SmsManager smsManager;

	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private int NOTIFICATION = 0;

	/**
	 * Class for clients to access.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with
	 * IPC.
	 */
	public class LocalBinder extends Binder {
		GpsService getService() {
			return GpsService.this;
		}
	}

	@Override
	public void onCreate() {
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		// Display a notification about us starting.  We put an icon in the status bar.
		showNotification();

		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		smsManager = SmsManager.getDefault();

		sharedPreferences = getSharedPreferences(Util.PREF_SMZONE, Context.MODE_PRIVATE);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);

		Editor editor = sharedPreferences.edit();
		editor.putBoolean(Util.PREF_KEY_IS_SERVICE_STARTED, true);
		editor.commit();
	}

	@Override
	public void onDestroy() {
		// Cancel the persistent notification.
		mNM.cancel(NOTIFICATION);

		// Stop gps listener
		locationManager.removeUpdates(locationListener);

		// Tell the user we stopped.
		Toast.makeText(this, R.string.gps_service_stopped, Toast.LENGTH_SHORT).show();

		Editor editor = sharedPreferences.edit();
		editor.putBoolean(Util.PREF_KEY_IS_SERVICE_STARTED, false);
		editor.commit();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients.  See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification() {
		// In this sample, we'll use the same text for the ticker and the expanded notification
		CharSequence text = getText(R.string.gps_service_started);

		// Set the icon, scrolling text and timestamp
		Notification notification = new Notification(R.drawable.icon, text,
				System.currentTimeMillis());
		notification.flags |= Notification.FLAG_ONGOING_EVENT;

		// The PendingIntent to launch our activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 7,
				new Intent(this, ListZoneActivity.class), 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this, getText(R.string.gps_service_label),
				text, contentIntent);

		// Send the notification.
		mNM.notify(NOTIFICATION, notification);
	}
	
	private void showSentNotification(int id, String text) {
		// Set the icon, scrolling text and timestamp
		Notification notification = new Notification(R.drawable.icon, text,
				System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		// The PendingIntent to launch our activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 8,
				new Intent(this, ListZoneActivity.class), 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this, getText(R.string.gps_service_label),
				text, contentIntent);

		// Send the notification.
		mNM.notify(id, notification);
	}

	// Define a listener that responds to location updates
	LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			// Called when a new location is found by the network location provider.
			checkNewLocation(location.getLatitude(), location.getLongitude());
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {}

		public void onProviderEnabled(String provider) {}

		public void onProviderDisabled(String provider) {
			stopSelf();
		}
	};

	private void checkNewLocation(double latitude, double longitude){
		Location myLoc = new Location("myLocation");
		Location destLoc = new Location("destinationLocation");
		for (int i = 0; i < ListZoneActivity.LIST_ZONE_ACTIVE.size(); i++) {
			Map<String, String> map = ListZoneActivity.LIST_ZONE_ACTIVE.get(i);
			//ambil jarak saat ini ke tujuan
			myLoc.setLatitude(latitude);
			myLoc.setLongitude(longitude);
			destLoc.setLatitude(Double.parseDouble(map.get(Item.LATITUDE)));
			destLoc.setLongitude(Double.parseDouble(map.get(Item.LONGITUDE)));

			String id = map.get(Item._ID); 
			float distance = myLoc.distanceTo(destLoc);
			ContentValues cv = new ContentValues();
			cv.put(Item.DISTANCE, distance);
			getContentResolver().update(Item.CONTENT_URI, cv, Item._ID +"="+id, null);

			//jika lebih kecil dari radius, kirim sms
			if(distance < Float.parseFloat(map.get(Item.RADIUS))){
				//kirim sms
				PendingIntent pi = PendingIntent.getActivity(this, 6, new Intent(this, ListZoneActivity.class), 0);
				smsManager.sendTextMessage(map.get(Item.PHONE_NUMBER), null, map.get(Item.MESSAGE),pi, null);

				showSentNotification(Integer.parseInt(id), "Message sent to "+map.get(Item.PHONE_NUMBER));

				//buang dari list dan update ke database
				ListZoneActivity.LIST_ZONE_ACTIVE.remove(i);
				cv = new ContentValues();
				cv.put(Item.IS_ACTIVE, 0);
				cv.put(Item.DISTANCE, "");
				getContentResolver().update(Item.CONTENT_URI, cv, Item._ID+"="+id, null);

				if(ListZoneActivity.LIST_ZONE_ACTIVE.size()<=0){
					stopSelf();
				}
			}else{
				//Toast.makeText(this, map.get(Item.TITLE)+": Belum sampai", Toast.LENGTH_SHORT).show();
			}
		}

	}
}
