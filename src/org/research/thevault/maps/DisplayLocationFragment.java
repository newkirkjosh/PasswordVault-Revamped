package org.research.thevault.maps;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
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
import org.research.chatclient.CreateAccountActivity;
import org.research.chatclient.R;
import org.research.thevault.structures.Locations;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DisplayLocationFragment extends ListFragment{
	
	private SharedPreferences mPrefs;
	private String mUsername;
	private ProgressDialog myProgressDialog = null;			// Used for dialogbox on listload
	private ArrayList<Locations> myLocations = null;		// list of locations
	private LocationAdapter myAdapter = null;				// Needed an adapter for the locations
	private Runnable viewLocations;							// Used for threading
	private final String FILENAME = "RecentLocations.txt";	// File to be read from
	private final File ROOTSD = Environment.getExternalStorageDirectory();
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.show_locations, container, false);
		
		mPrefs = getActivity().getSharedPreferences( CreateAccountActivity.PREFS, Context.MODE_PRIVATE );
		mUsername = mPrefs.getString( CreateAccountActivity.USER, "" ); 
		
		// Declare new array
		myLocations = new ArrayList<Locations>();
		
		// Set the adapter to populate with 
		this.myAdapter = new LocationAdapter(getActivity(), R.layout.location_item, myLocations);
		setListAdapter(this.myAdapter);
		
		// Used for threading
		viewLocations = new Runnable(){
			public void run()
			{
				getLocations();
			}
		};
		// Created thread
		Thread thread = new Thread(null, viewLocations, "MagentoBackground");
		thread.start();
		
		// Displays the progress dialog when loading the list
		myProgressDialog = ProgressDialog.show(getActivity(), "Please wait...", "Retrieving data...", true);
		return v;
	}
	
	// Method that retrieves all of the stored locations from the text file
	private void getLocations()
	{
		myLocations = new ArrayList<Locations>();
		double lat = 0;
		double lon = 0;
		JSONArray jsonArr = null;
		
		HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost("http://devimiiphone1.nku.edu/research_chat_client/password_vault_server/get_my_locations.php");

	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("username", mUsername));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity = response.getEntity();
	        
	        BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
	        StringBuilder b = new StringBuilder();
	        String tmp;
	        while((tmp = br.readLine()) != null)
        		b.append(tmp + "\n");
	        Log.d("res", "res: " + b.toString());
	        jsonArr = new JSONArray(b.toString());
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    if(jsonArr != null){
	    	for(int i = 0; i < jsonArr.length(); i++){
	    		JSONObject obj;
				try {
					obj = (JSONObject) jsonArr.get(i);
					lat = obj.getDouble("lat");
		    		lon = obj.getDouble("lon");
					Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());                 
					try {
					    List<Address> listAddresses = geocoder.getFromLocation(lat, lon, 1);
					    if(listAddresses != null && listAddresses.size() > 0){
					        String loc = listAddresses.get(0).getAddressLine(1);
					        myLocations.add(new Locations("" + lat, "" + lon, loc));
					    }
					} catch (IOException e) {
					    e.printStackTrace();
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    	}
	    }
		getActivity().runOnUiThread(returnRes);
	}
	
	// Used for threading and letting the adapter know if the data has changed
	private Runnable returnRes = new Runnable(){
		
		public void run()
		{
			if( myLocations != null && myLocations.size() > 0 )
			{
				myAdapter.notifyDataSetChanged();
				for(int i = 0; i < myLocations.size(); i++)
				{
					myAdapter.add(myLocations.get(i));
				}
			}
			
			//myProgressDialog.dismiss();
			myAdapter.notifyDataSetChanged();
			myProgressDialog.dismiss();
		}
	};
}
