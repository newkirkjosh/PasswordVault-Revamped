package org.research.thevault.phoneactivities;

import org.research.chatclient.R;
import org.research.thevault.OptionsFragment;
import org.research.thevault.TextFrag;
import org.research.thevault.apps.ShowApps;
import org.research.thevault.contacts.ShowContacts;
import org.research.thevault.maps.DisplayLocationFragment;
import org.research.thevault.maps.TrackMapActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class OptionsActivity extends Activity{

	private ActionBar actionBar;
	private Fragment rightFrag = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(!getResources().getBoolean(R.bool.IsTablet))
			setContentView(R.layout.home_layout_hdmi);
		else
			setContentView(R.layout.home_layout);
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		FragmentManager fm = getFragmentManager();
		Fragment optionsFrag = new OptionsFragment();
		FragmentTransaction ft = fm.beginTransaction();
				
		if (getResources().getBoolean(R.bool.IsTablet)) {
			ft.replace(R.id.list_frag, optionsFrag).replace(R.id.right_frag, new TextFrag()).commit();
		} else {
			ft.replace(android.R.id.content, optionsFrag).commit();
		}
	}

	public void startRightFrag(String option) {
		if ((getResources().getBoolean(R.bool.IsTablet)) && !option.equals("Tracking")) {
			Fragment frag;
			if(option.equals("Applications")){
				frag = new ShowApps();
			}else if(option.equals("Contacts")){
				frag = new ShowContacts();
			}else{
				frag = new DisplayLocationFragment();
			}
			rightFrag = frag;
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.right_frag, frag).commit();
		} else {
			Intent intent;
			if(option.equals("Applications")){
				intent = new Intent(OptionsActivity.this, ShowAppsActivity.class);
			}else if(option.equals("Contacts")){
				intent = new Intent(OptionsActivity.this, ShowContactsActivity.class);
			}else if(option.equals("Tracking")){
				if(rightFrag != null){
					FragmentManager fm = getFragmentManager();
					if(rightFrag.equals(fm.findFragmentById(R.id.right_frag))){
						FragmentTransaction ft = fm.beginTransaction();
						ft.remove(rightFrag).commit();
					}
				}
				intent = new Intent(OptionsActivity.this, TrackMapActivity.class);
			}
			else{
				intent = new Intent(OptionsActivity.this, ShowLocationsActivity.class);
			}
			startActivity(intent);
		}
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
}