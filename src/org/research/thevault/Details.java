package org.research.thevault;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebView.PictureListener;
import android.webkit.WebViewClient;
import android.widget.TextView;
import org.research.chatclient.R;

@SuppressWarnings("deprecation")
public class Details extends MyFragment implements Constants{
	
	ProgressDialog progress = null;
	WebView mWebView = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.details, container, false);
		Bundle args = getArguments();
		String url = args.getString( URL );
		String uName = args.getString( UNAME );
		String pWord = args.getString( PWORD );
		
		TextView textBox = (TextView) v.findViewById( R.id.site_details );
		
		mWebView = (WebView)v.findViewById(R.id.website_view);
		if(!url.startsWith("http")){
			url = "http://www." + url;
		}
		mWebView.setWebViewClient(new PasswordVaultWebClient());
		mWebView.loadUrl(url);
		
		textBox.setText("URL: " + url + "\nUsername: " + uName + "\nPassword: " + pWord );
		return v;
	}
	
	public boolean goBack(){
		if(mWebView.canGoBack()){
			mWebView.goBack();
			return true;
		}
		return false;
	}
	
	class PasswordVaultWebClient extends WebViewClient implements PictureListener {
		@Override
		public void onPageFinished(WebView view, String url) {
			// String javascriptObjectHook =
			// "window.location='#milage'";

			// view.loadUrl("javascript:(function() { " + javascriptObjectHook +
			// "})()");
			if (progress != null) {
				progress.dismiss();
			}
			
			
			// super.onPageFinished(view, url);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// super.shouldOverrideUrlLoading(view, url)

			if (url.startsWith("mailto:")) // Handle mail links by opening an
											// email intent
			{

				Intent i = new Intent(Intent.ACTION_SENDTO);
				Uri uri = Uri.parse(url);
				i.setData(uri);
				view.getContext().startActivity(
						Intent.createChooser(i, "Send mail..."));

			} else if (url.startsWith("tel:")) // Handle phone numbers
			{
				view.getContext().startActivity(
						new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
			} else // Normal web page link
			{
				Log.i("INkuWebActivity", view.getId() + " Loading " + url);

				view.loadUrl(url);
			}
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			if (progress != null) {
				progress = new ProgressDialog(getActivity());
				progress.setMessage("Loading...");
				progress.show();
			}
		}

		@Override
		public void onNewPicture(WebView view, Picture picture) {
			Log.i("New Picture", "");
			if (view.getProgress() == 100) {
				if (progress != null) {
					progress.dismiss();
				}
			}
		}
	}
}
