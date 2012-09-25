package org.research.thevault;

import org.research.chatclient.R;
import org.research.thevault.phoneactivities.OptionsActivity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class OptionsFragment extends Fragment{
	
	private ListView lv = null;
	private String[] options;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		options = new String[]{"Applications", "Contacts", "Tracking", "Locations"};
		View v = inflater.inflate(R.layout.options_list, container, false);
		lv = (ListView)v.findViewById(R.id.options_list);
		lv.setAdapter(new ArrayAdapter<String>(getActivity(), 
				android.R.layout.simple_list_item_1, options));
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				((OptionsActivity) getActivity()).startRightFrag(options[position]);
			}
		});
		return v;
	}
}
