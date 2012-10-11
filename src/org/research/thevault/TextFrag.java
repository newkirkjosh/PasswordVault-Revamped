package org.research.thevault;

import org.research.chatclient.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TextFrag extends Fragment{
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.text_frag, container, false);
		TextView tv = (TextView) v.findViewById(R.id.welcomeAdmin);
		tv.setText("Welcome " + getActivity().getIntent().getStringExtra("ADMIN") + " to the admin section of Password Vault");
		return v;
	}
}
