package org.research.chatclient;

import org.research.thevault.Constants;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class ConversationActivity extends BaseActivity implements Constants{
	
	private String convo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		convo = getIntent().getStringExtra(BaseActivity.CONVO);
		FragmentManager fm = getFragmentManager();
		Fragment convoFrag = new ConversationFrag();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(android.R.id.content, convoFrag).commit();
	}
	
	public String getConvo(){
		return convo;
	}
}
