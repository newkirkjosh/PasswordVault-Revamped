package org.research.chatclient;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class ConversationActivity extends BaseActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FragmentManager fm = getFragmentManager();
		Fragment convoFrag = new ConversationFrag();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(android.R.id.content, convoFrag).commit();
	}
}
