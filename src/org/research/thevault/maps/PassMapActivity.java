package org.research.thevault.maps;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import mapviewballoons.example.simple.SimpleItemizedOverlay;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.research.chatclient.BaseActivity;
import org.research.chatclient.CreateAccountActivity;
import org.research.chatclient.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.maps.OnSingleTapListener;
import com.readystatesoftware.maps.TapControlledMapView;

public class PassMapActivity extends MapActivity implements LocationListener{

	private SharedPreferences mPrefs;
	private String mUsername;
	private final String PREF_FILE = "location_info";
	// private final String JSON_STRING = "json";
	
	// Location variables
	private LocationManager mLocationManager;
	private TapControlledMapView mMapView;
	private SimpleItemizedOverlay mOverlay;
	private List<Overlay> overlays;
	private MapController mMapController;
	private String provider, gpsProvider, networkProvider;
	private Location loc, gpsLoc, netLoc;
	//private final double DISTANCE_LATITUDE = (25/69.047);
	//private final double DISTANCE_LONGITUDE = (25/Math.cos(69.047));
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mPrefs = getSharedPreferences( CreateAccountActivity.PREFS, Context.MODE_PRIVATE );
		mUsername = mPrefs.getString( CreateAccountActivity.USER, "" ); 
				
		mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		if( mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) 
				|| mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ){
			setContentView(R.layout.show_current_location);
			
			mMapView = (TapControlledMapView) findViewById(R.id.nku_map_view);
			mMapView.setBuiltInZoomControls(true);
			
			// Dismiss' balloon when tapping on the mapview
			mMapView.setOnSingleTapListener(new OnSingleTapListener() {

				@Override
				public boolean onSingleTap(MotionEvent e) {
					mOverlay.hideAllBalloons();
					return false;
				}
			});
			
			overlays = mMapView.getOverlays();
			
			mOverlay = new SimpleItemizedOverlay(this.getResources().getDrawable(R.drawable.inkupin), mMapView);
			mOverlay.setShowDisclosure(false);
			mOverlay.setSnapToCenter(true);
			mOverlay.setBalloonBottomOffset(50);
			
			mMapController = mMapView.getController();
			mMapController.setCenter(new GeoPoint((int)(39.031508 * 1000000),(int)(-84.464078 * 1000000)));
			mMapController.setZoom(18);
			
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			
			provider = mLocationManager.getBestProvider(criteria, true);
			gpsProvider = LocationManager.GPS_PROVIDER;
			networkProvider = LocationManager.NETWORK_PROVIDER;
			
		}else{
			AlertDialog.Builder ad = new AlertDialog.Builder(
					PassMapActivity.this);
			ad.setMessage(
					"Enable GPS or Wifi in order to use the maps.")
					.setCancelable(true);
			ad.setPositiveButton("Enable GPS",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							startActivity(new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS));
						}
					})
					.setNeutralButton("Enable Wifi",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									startActivity(new Intent(
											Settings.ACTION_WIFI_SETTINGS));
								}
							})
					.setNegativeButton("Exit",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});

			final AlertDialog alert = ad.create();
			alert.show();
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map_menu, menu);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.find_me:
			if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
					&& !mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				AlertDialog.Builder ad = new AlertDialog.Builder(
						PassMapActivity.this);
				ad.setMessage(
						"In order to use the map, please enable your GPS or Wifi connectivity.")
						.setCancelable(true);
				ad.setPositiveButton("Enable GPS",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								startActivity(new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
						.setNeutralButton("Enable Wifi",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										startActivity(new Intent(
												Settings.ACTION_WIFI_SETTINGS));
									}
								})
						.setNegativeButton("Exit",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();
									}
								});

				final AlertDialog alert = ad.create();
				alert.show();
			} else {
				
				loc = mLocationManager.getLastKnownLocation(networkProvider);
				loc = (loc == null) ? mLocationManager.getLastKnownLocation(gpsProvider) : loc;
				loc = (loc == null) ? mLocationManager.getLastKnownLocation(provider) : loc;
				
				Log.d("Location first recieved", loc.toString());
				makeUseOfLocation(loc);
			}
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if( netLoc != null ){
			mLocationManager.requestLocationUpdates(networkProvider, 0, 0, this);
		}else if( gpsLoc != null ){
			mLocationManager.requestLocationUpdates(gpsProvider, 0, 0, this);
		}else{
			mLocationManager.requestLocationUpdates(provider, 0, 0, this);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mLocationManager.removeUpdates(this);
	}
	
	/*
	private static boolean isConnected(Context context){
		
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = null;
		
		if( cm != null ){
			networkInfo = cm.getActiveNetworkInfo();
		}
		
		return networkInfo == null ? false : networkInfo.isConnected();
	}
	*/
	
	private void makeUseOfLocation(Location loc){
		
		String temp = "";
		JSONObject jObject = new JSONObject();
		JSONArray jArray;
		SharedPreferences mPrefs = this.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
		Editor editor = mPrefs.edit();
		
		/*
		File path = new File(ROOTSD.getAbsolutePath() + "/PasswordVault/locations/");
		if( !path.exists() ){
			path.mkdirs();
		}
		File file = new File(path, FILENAME);
		*/
		
		Address address;
		
		try{
			jArray = new JSONArray();
			
			address = getAddressForLocation(PassMapActivity.this, loc); 
			jObject.put("location_lat", address.getLatitude());
			jObject.put("location_lng", address.getLongitude());
			jObject.put("location_address", addressToString(address));
			
			jArray.put(jObject);
			temp = jArray.toString();
			editor.putString("json", temp);
			editor.commit();
			
		}catch (IOException e) {
			e.printStackTrace();
		}catch (JSONException e) {
			e.printStackTrace();
		}
		
		// Add overlay item to current position
		overlays.clear();
		mOverlay.hideAllBalloons();
		
		if(overlays.isEmpty()){
			mOverlay.addOverlay(new OverlayItem(getGeoPoint(loc), "Current Location", loc.getLatitude() + "," + loc.getLongitude()));
			overlays.add(mOverlay);
		}
		
		mMapView.invalidate();
		
		mMapController.setCenter(getGeoPoint(loc));
		mMapController.setZoom(18);
	}
	
	private GeoPoint getGeoPoint(Location loc){
		double lat = loc.getLatitude();
		double lng = loc.getLongitude();
		
		return new GeoPoint( (int)(lat * 1000000), (int)(lng * 1000000) );
	}
	
	/*
	private GeoPoint generateRandomGeopoint(Location loc){
		double latitude = loc.getLatitude() + genLatitude();
		double longitude = loc.getLongitude() + genLongitude();
		
		return new GeoPoint((int)(latitude * 1000000), (int)(longitude * 1000000));
	}
	*/
	
	// Because the Address, when you try to call it with the Geocoder, will return multiple Addresses,
	// you have to get the closest position, so that is what this method does, while also doing a null check
	private Address getAddressForLocation(Context context, Location location) throws IOException
	{	
		if(location != null)
		{
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			int maxResults = 1;
			
			Geocoder geo = new Geocoder( context, Locale.getDefault() );
			List<Address> addresses = geo.getFromLocation(latitude, longitude, maxResults);
			
			if(addresses.size() == 1)
			{
				return addresses.get(0);
			}
		}
		
		return null;
	}
	
	private String addressToString(Address address)
	{
		int max = address.getMaxAddressLineIndex();
		String str = "";
		
		if( max > 0 )
		{
			for(int i = 0; i < max; i++)
			{
				str += address.getAddressLine(i);
			}
		}
		else{
			str = address.getAddressLine(0);
		}
		
		return str;
	}
	
	/* Represents N and S directions so bounds go from -90 to 90 degrees
    private float genLatitude(){ 
    	return (float) ((Math.random() * DISTANCE_LATITUDE) - (DISTANCE_LATITUDE/2));
    }
    
    // Represents E and W directions so bounds go from -180 to 180 degrees
    private float genLongitude(){
    	return (float)((Math.random() * DISTANCE_LONGITUDE) - (DISTANCE_LONGITUDE/2));
    }
    */

	@Override
	public void onLocationChanged(Location location) {
		final String lat = "" + location.getLatitude();
		final String lon = "" + location.getLongitude();
		if(inTimeWindow()){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try{
						HttpClient httpclient = new DefaultHttpClient();
			    		HttpPost httppost = new HttpPost("http://devimiiphone1.nku.edu/research_chat_client/password_vault_server/add_location.php");
			    		LinkedList<NameValuePair> nameValuePairs = new LinkedList<NameValuePair>();
			    		
			    		nameValuePairs.add(new BasicNameValuePair("user", mUsername));
			    		nameValuePairs.add(new BasicNameValuePair("lat", lat));
			    		nameValuePairs.add(new BasicNameValuePair("lon", lon));
			    		
			    		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			    		HttpResponse response = httpclient.execute(httppost);
			    		Log.d("RES", "fail? " + response.getEntity().toString());
			    	}catch(UnsupportedEncodingException e){
			    		e.printStackTrace();
			    	} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}
		makeUseOfLocation(location);
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	// method to validate user creditials and time window
	private synchronized boolean inTimeWindow() {
		long windowStart = 300000;
		long lastPost = mPrefs.getLong("POST_TIME", 0);
		long curTime = System.currentTimeMillis();

		// check if user outside time window
		if (curTime - windowStart > lastPost) {
			SharedPreferences.Editor editor = mPrefs.edit();
			editor.putLong("POST_TIME", curTime);
			editor.commit();

			// return outside time window
			return true;
		}
		// return inside time window and logged in
		return false;
	}
}
