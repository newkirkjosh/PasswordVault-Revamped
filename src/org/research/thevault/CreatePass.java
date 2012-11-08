package org.research.thevault;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.research.chatclient.CreateAccountActivity;
import org.research.chatclient.R;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;

public class CreatePass extends Activity implements OnClickListener, Constants{

	PasswordTable pw;
	private SiteTable st;
	EditText pWord;
	EditText pWordConf;
	public static String CREATED = "created";
	@Override
	public void onCreate( Bundle savedInstanceState ){
		super.onCreate( savedInstanceState );
		SharedPreferences sharedPrefs = getSharedPreferences( CreateAccountActivity.PREFS, Context.MODE_PRIVATE );
		String c2dm = sharedPrefs.getString( CreateAccountActivity.C2DM, "" );
        if(c2dm.equals("")){
	        Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
	        registrationIntent.putExtra("app", PendingIntent.getBroadcast( this, 0, new Intent(), 0));
	        registrationIntent.putExtra("sender", "leibreichb2@gmail.com" );
	        startService(registrationIntent);
        }
		pw = new PasswordTable(this);
		st = new SiteTable(this);
		if( checkPassword() ){
			Intent passwordVault = new Intent( this, PasswordVault.class );
    		startActivity( passwordVault );
			finish();
		}
		else{
			setContentView( R.layout.create_password );
			pWord = (EditText) findViewById( R.id.password );
	        pWordConf = (EditText) findViewById( R.id.conf_password );
		}
	}
	
	public void onClick( View v ){
    	if( !pWord.getText().toString().equals("") && (pWord.getText().toString()).equals( pWordConf.getText().toString()) ){
    		RadioButton yesBtn = (RadioButton) findViewById(R.id.yesDemo);
    		if(yesBtn.isChecked()){
    			loadDemo();
    		}
    		addPassword( pWord.getText().toString() );
    		Intent passwordVault = new Intent( this, PasswordVault.class );
    		startActivity( passwordVault );
    		finish();
    	}
    	else{
    		pWord.setText( "" );
    		pWordConf.setText( "" );
    		pWord.requestFocus();
    		Intent fail = new Intent( this, CreateFail.class );
    		startActivity( fail );
    		
    	}
    }
	
	private void addPassword( String password ){
		SQLiteDatabase db = pw.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put( PWORD, password );
		db.insertOrThrow( PASS_TABLE_NAME, null, values );
		db.close();
	}
	
	public boolean checkPassword(){
		SQLiteDatabase db = pw.getReadableDatabase();
    	
    	Cursor hasPass = db.rawQuery( "SELECT * FROM " + PASS_TABLE_NAME, null);
    	boolean hasElement = hasPass.moveToFirst();
    	db.close();
    	hasPass.close();
        return hasElement;
    }
	
	private void loadDemo(){
		SQLiteDatabase db = st.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put( URL, "http://www.google.com" );
		values.put( UNAME, "user" );
		values.put( PWORD, "pass");
		db.insert( SITE_TABLE_NAME, null, values );
		
		values.clear();
		values.put( URL, "http://www.nku.edu" );
		values.put( UNAME, "user@gmail.com" );
		values.put( PWORD, "pass");
		db.insert( SITE_TABLE_NAME, null, values );
		
		values.clear();
		values.put( URL, "http://www.yahoo.com" );
		values.put( UNAME, "username" );
		values.put( PWORD, "password");
		db.insert( SITE_TABLE_NAME, null, values );
		
		values.clear();
		values.put( URL, "http://www.facebook.com" );
		values.put( UNAME, "demouser@demo.com" );
		values.put( PWORD, "pass22");
		db.insert( SITE_TABLE_NAME, null, values );
		
		values.clear();
		values.put( URL, "http://www.woot.com" );
		values.put( UNAME, "wootuser" );
		values.put( PWORD, "wootpass");
		db.insert( SITE_TABLE_NAME, null, values );
		
		db.close();
		
		SharedPreferences sharedPrefs = getSharedPreferences( CreateAccountActivity.PREFS, Context.MODE_PRIVATE );
		String c2dm = sharedPrefs.getString( CreateAccountActivity.C2DM, "" );
        if(!c2dm.equals("")){
	    	try{
	    		HttpPost httppost = new HttpPost("http://devimiiphone1.nku.edu/research_chat_client/chat_client_server/get_next_demo.php");
	    		LinkedList<NameValuePair> nameValuePairs = new LinkedList<NameValuePair>();
	    		
	    		nameValuePairs.add(new BasicNameValuePair("OS", "Android"));
	    		nameValuePairs.add(new BasicNameValuePair("deviceID", c2dm));
	    		
	    		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			    new DownloadFilesTask().execute(httppost);
	    		
	    	}catch(UnsupportedEncodingException e){
	    		e.printStackTrace();
	    	}
        }
	}
	
	private class DownloadFilesTask extends AsyncTask<HttpPost, Void, InputStream> {
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
	    	 try{
	    		 if( result != null ){
			    	 BufferedReader br = new BufferedReader(new InputStreamReader(result));
			    	 String line = br.readLine();
			    	 
			    	 while( line != null ){
			    		 if(line.trim().startsWith("DEMOUSER")){
			    			 Editor editor = getSharedPreferences( CreateAccountActivity.PREFS, Context.MODE_PRIVATE).edit();
				    		 editor.putBoolean(CreateAccountActivity.CREATED, true);
				    		 editor.putString(CreateAccountActivity.USER, line.trim());
				    		 editor.commit();
			    		 }
			    		 line = br.readLine();
			    	 }
	    		 }
	    	 }catch (IOException e) {
				e.printStackTrace();
			}
	     }
	 }
}
