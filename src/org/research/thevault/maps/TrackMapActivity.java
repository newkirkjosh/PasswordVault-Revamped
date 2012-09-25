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
import org.research.chatclient.CreateAccountActivity;
import org.research.chatclient.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.google.android.maps.MapActivity;

public class TrackMapActivity extends MapActivity {

	private ListView lv;
	private ProgressDialog mProgress;
	private JSONArray mUsers;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.track_map);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		lv = (ListView) findViewById(R.id.trackedList);
		View listHeader = (View) getLayoutInflater().inflate(R.layout.list_header, null, false);
		Button doneBtn = (Button) listHeader.findViewById(R.id.listHeaderBtn);
		doneBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String users = "";
				try {
					for(int i = 0; i < mUsers.length()-1; i++)
						users += mUsers.get(i).toString().trim() + ",";
					users += mUsers.get(mUsers.length()-1).toString().trim() + ",";
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(!users.equals("")){
					HttpPost post = new HttpPost("http://devimiiphone1.nku.edu/research_chat_client/password_vault_server/get_locations.php");
					try {
						List<NameValuePair> nvp = new LinkedList<NameValuePair>();
						nvp.add(new BasicNameValuePair("users", users));
				        post.setEntity(new UrlEncodedFormEntity(nvp));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
				Log.d("RES", "RES: " + text);
				mUsers = new JSONArray(text);
				TrackedUser[] users = new TrackedUser[mUsers.length()];
				for (int i = 0; i < mUsers.length(); i++)
					users[i] = new TrackedUser(mUsers.getString(i));
				ArrayAdapter<TrackedUser> adapter = new ArrayAdapter<TrackedUser>(TrackMapActivity.this, android.R.layout.simple_list_item_1, users);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				lv.setAdapter(adapter);
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
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (mProgress.isShowing())
				mProgress.dismiss();
			try {
				Log.d("RES", "RES: " + text);
				JSONObject json = new JSONObject(text);
				JSONArray spen = json.getJSONArray("spencer");
				Log.d("Spencer", spen.toString());
//				String[] users = new String[mUsers.length()];
//				for (int i = 0; i < mUsers.length(); i++)
//					users[i] = mUsers.getString(i);
//				ArrayAdapter<String> adapter = new ArrayAdapter<String>(TrackMapActivity.this, android.R.layout.simple_list_item_1, users);
//				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//				lv.setAdapter(adapter);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
