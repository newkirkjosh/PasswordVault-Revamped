package org.research.thevault;

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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.research.chatclient.R;
import org.research.thevault.phoneactivities.OptionsActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AdminActivity extends Activity {

	private EditText username;
	private EditText password;
	private TextView errorText;
	private ProgressDialog mProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		username = (EditText) findViewById(R.id.login_username);
		password = (EditText) findViewById(R.id.login_password);
		errorText = (TextView) findViewById(R.id.errorText);

		mProgress = new ProgressDialog(this);
		mProgress.setIndeterminate(true);
		mProgress.setCancelable(false);
		mProgress.setMessage("Logging in...");
	}

	public void onLogin(View v) {
		if(!username.getText().toString().equals("") && !password.getText().toString().equals("")){
			errorText.setVisibility(View.INVISIBLE);
			mProgress.show();
			HttpPost post = new HttpPost("http://devimiiphone1.nku.edu/research_chat_client/password_vault_server/acct_login.php");
			List<NameValuePair> nvp = new LinkedList<NameValuePair>();
			nvp.add(new BasicNameValuePair("username", username.getText().toString()));
			nvp.add(new BasicNameValuePair("password", password.getText().toString()));
	        try {
				post.setEntity(new UrlEncodedFormEntity(nvp));
				new LoginTask().execute(post);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			errorText.setText("Bad username/password combo");
			errorText.setVisibility(View.VISIBLE);
		}
	}

	public void onCancel(View v) {
		finish();
	}

	private class LoginTask extends AsyncTask<HttpPost, Void, InputStream> {

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
				errorText.setText("Connection Error");
				errorText.setVisibility(View.VISIBLE);
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
					String line;

					while ((line = br.readLine()) != null) {
						text += line;
					}
					if (mProgress.isShowing())
						mProgress.dismiss();
					if(text.equals("AUTHORIZED")){
						Intent intent = new Intent(AdminActivity.this, OptionsActivity.class);
						startActivity(intent);
						finish();
					} else if(text.equals("NOT-AUTHORIZED")){
						errorText.setText("Invalid Username/Password");
						errorText.setVisibility(View.VISIBLE);
					} else{
						errorText.setText("Connection error");
						errorText.setVisibility(View.VISIBLE);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
