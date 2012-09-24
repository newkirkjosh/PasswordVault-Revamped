package org.research.thevault.phoneactivities;

import org.research.thevault.maps.DisplayLocationFragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;

public class ShowLocationsActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Fragment optsFrag = new DisplayLocationFragment();
        getFragmentManager().beginTransaction().add(android.R.id.content, optsFrag).commit();
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
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
