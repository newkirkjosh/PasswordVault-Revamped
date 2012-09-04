package org.research.thevault.phoneactivities;

import org.research.thevault.Details;
import org.research.thevault.MyFragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class DetailsActivity extends Activity{

	private MyFragment mSiteFrag = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mSiteFrag = new Details();
        mSiteFrag.setArguments(getIntent().getBundleExtra("BUNDLE"));
        getFragmentManager().beginTransaction().add(android.R.id.content, mSiteFrag).commit();
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onBackPressed() {
		if(!mSiteFrag.goBack()){
			super.onBackPressed();
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