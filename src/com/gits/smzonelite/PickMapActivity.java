package com.gits.smzonelite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class PickMapActivity extends MapActivity{
	Context ctx = this;
	private MapView mMapView;
	private MapController mapController;
	private String address;
	private double longitude;
	private double latitude;
	private TextView mapAddress;

	GeoPoint restaurantPoint;
	MyLocationOverlay myLocationOverlay;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_pick_map);

		mMapView = (MapView) findViewById(R.id.map);
		mapController = mMapView.getController();

		mapAddress = (TextView) findViewById(R.id.map_address);

		longitude=getIntent().getExtras().getDouble("longitude");
		latitude=getIntent().getExtras().getDouble("latitude");

		initMap();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		myLocationOverlay.disableCompass();
		myLocationOverlay.disableMyLocation();
	}
	
	private void initMap(){
		mMapView.setClickable(true);
		mMapView.setEnabled(true);
		mMapView.setBuiltInZoomControls(true);
		mapController.setZoom(18);

		//overlay my location
		myLocationOverlay = new MyLocationOverlay(this, mMapView);

		myLocationOverlay.enableCompass();
		myLocationOverlay.enableMyLocation();

		mMapView.getOverlays().clear();
		mMapView.getOverlays().add(myLocationOverlay);

		//jika sudah pernah pilih
		if(longitude!=0.0 && latitude!=0.0){
			GeoPoint p = new GeoPoint(
					(int) (latitude * 1E6), 
					(int) (longitude * 1E6));
			mapController.animateTo(p);    
			restaurantPoint = p;

			mMapView.getOverlays().add(new RestaurantOverlay());
		}else{
			mMapView.getOverlays().add(new RestaurantOverlay());
			myLocationOverlay.runOnFirstFix(new Runnable() {
				public void run() {
					mapController.animateTo(myLocationOverlay.getMyLocation());
					restaurantPoint = myLocationOverlay.getMyLocation();
					setAddressText();
				}
			});

		}

		//mMapView.invalidate();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}


	class RestaurantOverlay extends com.google.android.maps.Overlay
	{
		final float SCALE = getResources().getDisplayMetrics().density;

		@Override
		public boolean draw(Canvas canvas, MapView mapView, 
				boolean shadow, long when) 
		{
			super.draw(canvas, mapView, true);                   

			if(restaurantPoint!=null){
				//---translate the GeoPoint to screen pixels---
				Point screenPts = new Point();
				mapView.getProjection().toPixels(restaurantPoint, screenPts);

				//---add the marker---
				Bitmap bmp = BitmapFactory.decodeResource(
						getResources(), R.drawable.point_restaurant);            
				canvas.drawBitmap(bmp, screenPts.x-(9*SCALE), screenPts.y-(33*SCALE), null);
			}
			return true;
		}

		@Override
		public boolean onTap(GeoPoint p, MapView mapView) {
			restaurantPoint = p;
			mMapView.invalidate();

			setAddressText();
			return true;
		}
	} 

	private void setAddressText(){
		new Thread(){
			String strAddr="";
			public void run() {
				//Reverse Geo
				List<Address> addresses = new ArrayList<Address>();
				//center ke jalan (geo reverse)
				try {
					Geocoder geoCoder = new Geocoder(ctx, Locale.getDefault());
					addresses = geoCoder.getFromLocation(
							restaurantPoint.getLatitudeE6()/ 1E6, restaurantPoint.getLongitudeE6()/ 1E6, 1);

				} catch (IOException e) {
					//e.printStackTrace();
				}
				if (addresses.size() > 0) {
					for (int i=0; i<addresses.get(0).getMaxAddressLineIndex();i++)
						strAddr += ", "+addresses.get(0).getAddressLine(i);
					//Tulis ke textview
					strAddr = strAddr.replaceFirst(", ", "");
					address=strAddr;
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mapAddress.setText(strAddr);
						}
					});
				}
			};
		}.start();
	}

	public void onMyLocationClick(View v){
		try {
			mapController.animateTo(myLocationOverlay.getMyLocation());
		} catch (Exception e) {
		}
	}
	public void onOKClick(View v){
		Intent intent = new Intent();
		intent.putExtra("longitude", (restaurantPoint.getLongitudeE6()/ 1E6)+"");
		intent.putExtra("latitude", (restaurantPoint.getLatitudeE6()/ 1E6)+"");
		intent.putExtra("address", address);
		setResult(RESULT_OK, intent);
		finish();
	}
}
