package org.research.thevault;

import org.research.chatclient.R;
import org.research.thevault.apps.ShowApps;
import org.research.thevault.contacts.ShowContacts;
import org.research.thevault.maps.DisplayLocationFragment;
import org.research.thevault.maps.TrackMapActivity;
import org.research.thevault.phoneactivities.ShowAppsActivity;
import org.research.thevault.phoneactivities.ShowContactsActivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
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
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		View v = inflater.inflate(R.layout.options_list, container, false);
		lv = (ListView)v.findViewById(R.id.options_list);
		lv.setAdapter(new ArrayAdapter<String>(getActivity(), 
				android.R.layout.simple_list_item_1, new String[]{"Applications", "Contacts", "Tracking", "Locations"}));
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Fragment frag;
				
				switch (position) {
				// Apps
				case 0:
					if(getResources().getBoolean(R.bool.IsTablet)){
						frag = new ShowApps();
			    		ft.replace(R.id.right_frag, frag).commit();
		    		}
		    		else{
						Intent intent = new Intent(getActivity(), ShowAppsActivity.class);
						startActivity(intent);
					}
					break;
				// Contacts
				case 1:
					if(getResources().getBoolean(R.bool.IsTablet)){
						frag = new ShowContacts();
						ft.replace(R.id.right_frag, frag).commit();
					}
					else{
						Intent intent = new Intent(getActivity(), ShowContactsActivity.class);
						startActivity(intent);
					}
					break;
				// Tracking
				case 2:
					Intent mapIntent = new Intent(getActivity(), TrackMapActivity.class);
					startActivity(mapIntent);
					break;
				// Locations
				case 3:
					if(getResources().getBoolean(R.bool.IsTablet)){
						frag = new DisplayLocationFragment();
						ft.replace(R.id.right_frag, frag).commit();
					}
					else{
						Intent intent = new Intent(getActivity(), ShowContactsActivity.class);
						startActivity(intent);
					}
					break;
				default:
					break;
				}
			}
			
		});
		
		
		return v;
	}
}
