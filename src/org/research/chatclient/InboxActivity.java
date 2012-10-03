package org.research.chatclient;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

public class InboxActivity extends Fragment implements Constants{
	
	private ListView lv;
	private SharedPreferences mPrefs;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.conversation_list, container, false);
		mPrefs = getActivity().getSharedPreferences( CreateAccountActivity.PREFS, Context.MODE_PRIVATE );
		String username = mPrefs.getString( CreateAccountActivity.USER, "" );
		String c2dm = mPrefs.getString( CreateAccountActivity.C2DM, "" );
		try{
    		HttpPost httppost = new HttpPost("http://devimiiphone1.nku.edu/research_chat_client/chat_client_server/get_messages.php");
    		LinkedList<NameValuePair> nameValuePairs = new LinkedList<NameValuePair>();
    		
    		nameValuePairs.add(new BasicNameValuePair("username", username));
    		nameValuePairs.add(new BasicNameValuePair("deviceID", c2dm));
    		
    		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    		
    		lv = (ListView)v.findViewById(R.id.conversation_list);
    		((BaseActivity) getActivity()).setListView(lv);
    		((BaseActivity) getActivity()).loadList();
    		((BaseActivity) getActivity()).downloadMessages(httppost);
    		registerForContextMenu(lv);
    	}catch(UnsupportedEncodingException e){
    		e.printStackTrace();
    	}
		return v;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Context Menu");  
	    menu.add(0, v.getId(), 0, "Delete Conversation");
	}
	
	@Override 
    public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	if(item.getTitle().toString().equals("Delete Conversation")){
    		@SuppressWarnings("unchecked")
			HashMap<String, String> map = (HashMap<String, String>) lv.getItemAtPosition((int)info.id);
			((BaseActivity) getActivity()).deleteConvo(map.get("name"));
    	}
        return true; 
    }
	
}
