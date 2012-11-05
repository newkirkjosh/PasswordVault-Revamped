package org.research.thevault.maps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.research.chatclient.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class TrackMapActivity extends MapActivity {

	private ListView lv;
	private ProgressDialog mProgress;
	private TrackedUser[] mUsers;
	private List<TrackedUser> mCheckedUsers;
	protected MapView mMapView;
	protected MapController mMapController;
	protected List<MyItemizedOverlay> mCustomOverlay;
	protected List<Overlay> overlays;
	private static final double MILLION = 1E6;
	private final int NKULAT = (int) (39.029579 * MILLION);
	private final int NKULONG = (int) (-84.463509 * MILLION);
	private int[] colors = new int[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.track_map);
		
		mMapView = (MapView) findViewById(R.id.trackMap);
		mMapView.setBuiltInZoomControls(true);
		
		mMapController = mMapView.getController();
		mMapController.setCenter(new GeoPoint(NKULAT, NKULONG));
		mMapController.setZoom(15);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		lv = (ListView) findViewById(R.id.trackedList);
		View listHeader = (View) getLayoutInflater().inflate(R.layout.list_header, null, false);
		Button doneBtn = (Button) listHeader.findViewById(R.id.listHeaderBtn);
		doneBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String users = "";
				mCheckedUsers = new LinkedList<TrackedUser>();
				for(int i = 0; i < mUsers.length; i++){
					TrackedUser trackedUser = mUsers[i];
					if(trackedUser.isChecked()){
						mCheckedUsers.add(trackedUser);
						users += trackedUser.toString().trim() + ",";
					}
				}
				if(!users.equals("")){
					users = users.substring(0,  users.length() - 1);
					HttpPost post = new HttpPost("http://devimiiphone1.nku.edu/research_chat_client/password_vault_server/get_locations.php");
					try {
						List<NameValuePair> nvp = new LinkedList<NameValuePair>();
						nvp.add(new BasicNameValuePair("users", users));
				        post.setEntity(new UrlEncodedFormEntity(nvp));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					mProgress.show();
					new GetLocationsTask().execute(post);
				}

			}
		});
		lv.addHeaderView(listHeader);
		mProgress = new ProgressDialog(this);
		mProgress.setIndeterminate(true);
		mProgress.setCancelable(false);
		mProgress.setMessage("Getting Tracked Users...");
		mProgress.show();

		new GetPeopleTask().execute(new HttpGet("http://devimiiphone1.nku.edu/research_chat_client/password_vault_server/get_tracked_users.php"));

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public static GeoPoint returnGeopoint(double lat, double lng) {
		int latitude = (int) (lat * MILLION);
		int longitude = (int) (lng * MILLION);

		return new GeoPoint(latitude, longitude);
	}
	
	public void addOverlayItems(JSONObject json) {

		overlays = mMapView.getOverlays();
		overlays.clear();
		mCustomOverlay = new LinkedList<MyItemizedOverlay>();
		for(int i = 0; i < mCheckedUsers.size(); i++){
			MyItemizedOverlay tempOver = new MyItemizedOverlay(this.getResources().getDrawable(R.drawable.inkupin), this, mMapView.getProjection(), colors[i % colors.length]);
			try {
				String user = mCheckedUsers.get(i).toString();
				JSONArray jsonArray = json.getJSONArray(user);
				for( int j = 0; j < jsonArray.length(); j++){
					JSONObject obj = jsonArray.getJSONObject(j);
					tempOver.addOverlay(new OverlayItem(returnGeopoint(obj.getDouble("lat"), obj.getDouble("lon")), "Tracked user", user));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mCustomOverlay.add(tempOver);
		}
		overlays.addAll(mCustomOverlay);
		mMapView.invalidate();
	}
	
	private class TrackedUser{
		private String name;
		private boolean checked;
		
		public TrackedUser(String name){
			this.name = name;
			checked = false;
		}
		
		public void toggleChecked(){
			checked = !checked;
		}
		
		public boolean isChecked(){
			return checked;
		}
		
		public void setChecked(boolean checked){
			this.checked = checked;
		}
		
		@Override
		public String toString(){
			return name;
		}
		
	}

	private class GetPeopleTask extends AsyncTask<HttpGet, Void, InputStream> {

		@Override
		protected InputStream doInBackground(HttpGet... post) {
			HttpClient httpclient = new DefaultHttpClient();
			InputStream stream = null;
			HttpResponse response;
			try {
				response = httpclient.execute(post[0]);
				HttpEntity entity = response.getEntity();
				stream = entity.getContent();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return stream;
		}

		@Override
		protected void onPostExecute(InputStream result) {
			String text = "";

			try {
				if (result != null) {
					BufferedReader br = new BufferedReader(new InputStreamReader(result));
					String line = br.readLine();

					while (line != null) {
						text += line + " ";
						line = br.readLine();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (mProgress.isShowing())
				mProgress.dismiss();
			try {
				JSONArray users = new JSONArray(text);
				mUsers = new TrackedUser[users.length()];
				for (int i = 0; i < users.length(); i++)
					mUsers[i] = new TrackedUser(users.getString(i));
				lv.setAdapter(new CheckBoxAdapter(TrackMapActivity.this));
				lv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						CheckBox cb = (CheckBox) view.findViewById(R.id.CheckBox01);
						cb.setChecked(!cb.isChecked());
			            TrackedUser trackedUser = (TrackedUser) cb.getTag();
			            trackedUser.setChecked( cb.isChecked() );
					}
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private class GetLocationsTask extends AsyncTask<HttpPost, Void, InputStream> {

		@Override
		protected InputStream doInBackground(HttpPost... post) {
			HttpClient httpclient = new DefaultHttpClient();
			InputStream stream = null;
			HttpResponse response;
			try {
				response = httpclient.execute(post[0]);
				HttpEntity entity = response.getEntity();
				stream = entity.getContent();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return stream;
		}

		@Override
		protected void onPostExecute(InputStream result) {
			String text = "";

			try {
				if (result != null) {
					BufferedReader br = new BufferedReader(new InputStreamReader(result));
					String line = br.readLine();

					while (line != null) {
						text += line + " ";
						line = br.readLine();
					}
					Log.d("JSON", text);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				JSONObject json = new JSONObject(text);
				// Add Overlays
				addOverlayItems(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (mProgress.isShowing())
				mProgress.dismiss();
		}
	}
	
	/** Holds child views for one row. */
	private static class TrackedUserHolder {
		
		private CheckBox checkBox ;
	    private TextView textView ;
	    
	    public TrackedUserHolder() {}
	    
	    public TrackedUserHolder( TextView textView, CheckBox checkBox ) {
	    	this.checkBox = checkBox ;
	    	this.textView = textView ;
	    }
	    
	    public CheckBox getCheckBox() {
	    	return checkBox;
	    }
	    public void setCheckBox(CheckBox checkBox) {
	    	this.checkBox = checkBox;
	    }
	    public TextView getTextView() {
	    	return textView;
	    }
	    public void setTextView(TextView textView) {
	    	this.textView = textView;
	    }    
	}
	  
	private class CheckBoxAdapter extends ArrayAdapter<TrackedUser>{
		
		private LayoutInflater inflater;
		
		public CheckBoxAdapter(Context context){
			super(context, R.layout.simple_check_list, R.id.rowTextView, mUsers);
			inflater = LayoutInflater.from(context);
		}
		
		@Override
	    public View getView(int position, View convertView, ViewGroup parent) {
			// Planet to display
			final TrackedUser trackedUser = (TrackedUser) this.getItem( position ); 

			// The child views in each row.
			CheckBox checkBox ; 
			TextView textView ; 
	      
			// Create a new row view
			if ( convertView == null ) {
				convertView = inflater.inflate(R.layout.simple_check_list, parent, false);
	        
	        // Find the child views.
	        textView = (TextView) convertView.findViewById( R.id.rowTextView );
	        checkBox = (CheckBox) convertView.findViewById( R.id.CheckBox01 );
	        
	        // Optimization: Tag the row with it's child views, so we don't have to 
	        // call findViewById() later when we reuse the row.
	        convertView.setTag(new TrackedUserHolder(textView, checkBox));
	      }
	      // Reuse existing row view
	      else {
	        // Because we use a ViewHolder, we avoid having to call findViewById().
	    	  TrackedUserHolder viewHolder = (TrackedUserHolder) convertView.getTag();
	        checkBox = viewHolder.getCheckBox() ;
	        textView = viewHolder.getTextView() ;
	      }

	      // Tag the CheckBox with the Planet it is displaying, so that we can
	      // access the planet in onClick() when the CheckBox is toggled.
	      checkBox.setTag( trackedUser ); 
	      
	      // Display planet data
	      checkBox.setChecked( trackedUser.isChecked() );
	      textView.setText( trackedUser.toString() );
	      checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				trackedUser.setChecked(isChecked);
			}
		});
	      
	      return convertView;
	    }
	}
}
