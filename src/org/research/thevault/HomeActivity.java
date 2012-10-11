package org.research.thevault;

import org.research.chatclient.BaseActivity;
import org.research.chatclient.CreateAccountActivity;
import org.research.chatclient.R;
import org.research.thevault.maps.PassMapActivity;
import org.research.thevault.phoneactivities.AddPageActivity;
import org.research.thevault.phoneactivities.DetailsActivity;
import org.research.thevault.phoneactivities.GenPassActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class HomeActivity extends Activity {

	private boolean mWebOpen = false;
	private MyFragment mSiteFrag = null;
	private ActionBar ab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(!getResources().getBoolean(R.bool.IsTablet) && BaseActivity.HDMI_ACTIVE)
			setContentView(R.layout.home_layout_hdmi);
		else
			setContentView(R.layout.home_layout);

		if (getResources().getBoolean(R.bool.IsTablet) || BaseActivity.HDMI_ACTIVE) {
			FragmentManager fm = getFragmentManager();
			Fragment listFrag = new SitesList();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.list_frag, listFrag).commit();
		} else {
			FragmentManager fm = getFragmentManager();
			Fragment listFrag = new SitesList();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(android.R.id.content, listFrag).commit();
		}
		ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
	}

	public void startRightFrag(Bundle site) {
		mWebOpen = true;
		if (getResources().getBoolean(R.bool.IsTablet) || BaseActivity.HDMI_ACTIVE) {
			FragmentManager fm = getFragmentManager();
			mSiteFrag = new Details();
			mSiteFrag.setArguments(site);
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.right_frag, mSiteFrag).commit();
		} else {
			Intent intent = new Intent(this, DetailsActivity.class);
			intent.putExtra("BUNDLE", site);
			startActivity(intent);
		}
	}

	@Override
	public void onBackPressed() {
		if (!mWebOpen || mSiteFrag == null || !mSiteFrag.goBack()) {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent;
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft;
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.add:
			mSiteFrag = new AddPage();
			// mSiteFrag.setArguments(site);

			if (getResources().getBoolean(R.bool.IsTablet) || BaseActivity.HDMI_ACTIVE) {
				ft = fm.beginTransaction();
				ft.replace(R.id.right_frag, mSiteFrag).commit();
				ab.setDisplayHomeAsUpEnabled(true);
			} else {
				intent = new Intent(this, AddPageActivity.class);
				startActivity(intent);
			}
			return true;
		case R.id.gen_pwd:
			mSiteFrag = new GeneratePass();
			// mSiteFrag.setArguments(site);
			if (getResources().getBoolean(R.bool.IsTablet) || BaseActivity.HDMI_ACTIVE) {
				ft = fm.beginTransaction();
				ft.replace(R.id.right_frag, mSiteFrag).commit();
				ab.setDisplayHomeAsUpEnabled(true);
			} else {
				intent = new Intent(this, GenPassActivity.class);
				startActivity(intent);
			}
			return true;
		case R.id.location:
			intent = new Intent(this, PassMapActivity.class);
			startActivity(intent);
			return true;
		case R.id.server_connect:
			intent = new Intent(this, CreateAccountActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return super.onPrepareOptionsMenu(menu);
	}
}
