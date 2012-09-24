package org.research.thevault.maps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.research.chatclient.R;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.maps.MapActivity;

public class TrackMapActivity extends MapActivity{

	private ListView lv;
	private ProgressDialog mProgress;
	private JSONArray mUsers;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.track_map);
		lv = (ListView) findViewById(R.id.trackedList);
		mProgress = new ProgressDialog(this);
	    mProgress.setIndeterminate(true);
	    mProgress.setCancelable(false);
	    mProgress.setMessage("Getting Tracked Users...");
	    mProgress.show();
		new GetPeopleTask().execute(new HttpGet("http://devimiiphone1.nku.edu/research_chat_client/chat_client_server/get_users.php"));
		
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
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
	    	 
	    	 try{
	    		 if( result != null ){
			    	 BufferedReader br = new BufferedReader(new InputStreamReader(result));
			    	 String line = br.readLine();
			    	 
			    	 while( line != null ){
			    		 text += line + " ";
			    		 line = br.readLine();
			    	 }
	    		 }
	    	 }catch (IOException e) {
				e.printStackTrace();
			}
	    	 
	    	 if(mProgress.isShowing())
	    		 mProgress.dismiss();
	    	 try {
				mUsers = new JSONArray(text);
				String[] users = new String[mUsers.length()];
				for(int i = 0; i < mUsers.length(); i++)
					users[i] = mUsers.getString(i);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(TrackMapActivity.this, android.R.layout.simple_list_item_1, users);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				lv.setAdapter(adapter);
			} catch (JSONException e) {
				e.printStackTrace();
			}
	     }
	 }
}
