package org.research.chatclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

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
import org.research.thevault.Constants;
import org.research.thevault.PVDatamanager;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ConversationFrag extends Fragment implements Constants{
	
	private PVDatamanager pvm;
	private ProgressDialog mProgress;
	private SharedPreferences mPrefs;
	private JSONArray mUsers;
	private Spinner spin;
	private ScrollView convoScroll;
	private View rootView;
	private boolean insertMessage = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.conversation, container, false);
		pvm = PVDatamanager.getInstance();
		spin = (Spinner)rootView.findViewById(R.id.personSpin);
		convoScroll = (ScrollView)rootView.findViewById(R.id.convoScroll);
		convoScroll.postDelayed(new Runnable() {
			@Override
			public void run() {
				((ScrollView)rootView.findViewById(R.id.convoScroll)).fullScroll(ScrollView.FOCUS_DOWN);
			}
		}, 500);
		String convo;
		if(!getResources().getBoolean(R.bool.IsTablet))
			convo = ((ConversationActivity) getActivity()).getConvo();
		else
			convo = ((BaseActivity) getActivity()).getConvo();
	    if(convo != null){
	    	mPrefs = getActivity().getSharedPreferences( CreateAccountActivity.PREFS, Context.MODE_PRIVATE );
			mProgress = new ProgressDialog(getActivity());
		    mProgress.setIndeterminate(true);
		    mProgress.setCancelable(false);
		    mProgress.setMessage("Getting Conversation...");
		    mProgress.show();
	    	spin.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, new String[]{convo}));
	    	spin.setClickable(false);
	    	((TextView)rootView.findViewById(R.id.placeHoldText)).setVisibility(View.GONE);
	    	loadConvo(convo);
	    }
	    else{
			mPrefs = getActivity().getSharedPreferences( CreateAccountActivity.PREFS, Context.MODE_PRIVATE );
			mProgress = new ProgressDialog(getActivity());
		    mProgress.setIndeterminate(true);
		    mProgress.setCancelable(false);
		    mProgress.setMessage("Getting Users...");
		    mProgress.show();
	    	new GetUsersTask().execute(new HttpGet("http://devimiiphone1.nku.edu/research_chat_client/chat_client_server/get_users.php"));
	    }
	    
	    Button sendBtn = (Button)rootView.findViewById(R.id.sendMessBtn);
	    sendBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				sendMessage();
			}
		});
		return rootView;
	}
	
	public void sendMessage(){
		
		EditText messBox = (EditText)rootView.findViewById(R.id.messageText);
		String message = messBox.getText().toString();
		messBox.setText("");
		String sender = mPrefs.getString(CreateAccountActivity.USER, "");
		String time = "" + Calendar.getInstance().getTimeInMillis();
		time = time.substring(0, 10);
		String recipient = (String)spin.getSelectedItem();
		if(!message.equals("")){
			LayoutInflater inflate = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflate.inflate(R.layout.sent_view, null, false);
			TextView tv = (TextView) v.findViewById(R.id.sentText);
			tv.setText(message);
			LinearLayout wrapper = (LinearLayout)rootView.findViewById(R.id.convoLay);
			wrapper.addView(v);
			TextView placeText = (TextView) rootView.findViewById(R.id.placeHoldText);
			placeText.setVisibility(View.GONE);
			convoScroll.scrollBy(0, 50);
			try{
	    		HttpPost httppost = new HttpPost("http://devimiiphone1.nku.edu/research_chat_client/chat_client_server/send_message.php");
	    		LinkedList<NameValuePair> nameValuePairs = new LinkedList<NameValuePair>();
	    		
	    		nameValuePairs.add(new BasicNameValuePair("recipient", recipient));
	    		nameValuePairs.add(new BasicNameValuePair("sender", sender));
	    		nameValuePairs.add(new BasicNameValuePair("message", message));
	    		
	    		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			    insertMessage = true;
			    new SendMessageTask().execute(httppost);
	    		
	    	}catch(UnsupportedEncodingException e){
	    		e.printStackTrace();
	    	}
		}
	}
	
	private void loadConvo(String otherPers){
		ArrayList<HashMap<String, String>> convo = pvm.getConversation(getActivity(), otherPers);
		LayoutInflater inflate = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	for(int i = 0; i < convo.size(); i++){
			HashMap<String, String> map = convo.get(i);
			View v = null;
			if(map.get(RECIPIENT).equals(map.get(OTHER_MEMBER))){
				v = inflate.inflate(R.layout.sent_view, null, false);
				TextView tv = (TextView) v.findViewById(R.id.sentText);
				tv.setText(map.get(MESSAGE));
				LinearLayout wrapper = (LinearLayout)rootView.findViewById(R.id.convoLay);
				wrapper.addView(v);
			}
			if(map.get(SENDER).equals(map.get(OTHER_MEMBER))){
				v = inflate.inflate(R.layout.received_view, null, false);
				TextView tv = (TextView) v.findViewById(R.id.receivedText);
				tv.setText(map.get(MESSAGE));
				LinearLayout wrapper = (LinearLayout)rootView.findViewById(R.id.convoLay);
				wrapper.addView(v);
			}
    	}
    	if(mProgress.isShowing())
    		mProgress.dismiss();
	}
	
	private class GetUsersTask extends AsyncTask<HttpGet, Void, InputStream> {
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
				Spinner personSpinner = (Spinner)rootView.findViewById(R.id.personSpin);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, users);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				personSpinner.setAdapter(adapter);
			} catch (JSONException e) {
				e.printStackTrace();
			}
	     }
	 }
	
	protected class SendMessageTask extends AsyncTask<HttpPost, Void, InputStream> {
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
	    	 
	    	 try{
	    		 if( result != null ){
			    	 BufferedReader br = new BufferedReader(new InputStreamReader(result));
			    	 String line = br.readLine();
			    	 
			    	 while( line != null ){
			    		 text += line + " ";
			    		 line = br.readLine();
			    	 }
			    	 if(insertMessage){
			    		 insertMessage = false;
			    		 try {
			    			 Log.d("RES", text);
							JSONObject jobj = new JSONObject(text);
							if(jobj.has("sender") && jobj.has("message") && jobj.has("timestamp") && jobj.has("recipient")){
								String sender = jobj.getString("sender");
								String message = jobj.getString("message");
								String time = jobj.getString("timestamp");
								String reci = jobj.getString("recipient");
								((BaseActivity) getActivity()).insertMessage(sender, message, time, reci);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    	 }
	    		 }
	    	 }catch (IOException e) {
	    		 e.printStackTrace();
	    	 }
	    	 Toast.makeText(getActivity(), "Message Sent", Toast.LENGTH_LONG).show();
	    	 ((BaseActivity) getActivity()).loadList();
	     }
	 }
}

