package org.research.thevault;

import org.research.chatclient.R;
import org.research.thevault.maps.PassMapActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class SitesList extends MyFragment implements Constants{

	private SiteTable st;
	private SimpleCursorAdapter adapter;
	private SQLiteDatabase db;
	private Cursor cursor;
	private ListView lv = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.sites_list, container, false);
		String[] from = { URL };
    	int[] to = { R.id.url_item };
    	
		lv = (ListView)v.findViewById(R.id.list_view1);
    	lv.setAdapter(new SimpleCursorAdapter(getActivity().getApplicationContext(), R.layout.site_item, cursor, from, to, 0));
        lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long arg3) {
				cursor.moveToPosition( position );

		    	showDetails();
			}
		});
        registerForContextMenu(lv); 
        
        st = new SiteTable( getActivity().getApplicationContext() );
        cursor = getSites();
        showSites( cursor );
        return v;
	}

	/*
	@Override
	public void onResume() {
		if(lv != null){
			String[] from = { URL };
	    	int[] to = { R.id.url_item };
	    	lv.setAdapter(new SimpleCursorAdapter(getActivity().getApplicationContext(), R.layout.site_item, cursor, from, to, 0));
		}
    	super.onResume();
	}
	*/

	@Override
	public void onDestroy(){
	    if( cursor != null )
	        cursor.close();
	    if( db != null )
	        db.close();
	    super.onDestroy();
	}

    public Cursor getSites(){
    	db = st.getReadableDatabase();
		cursor = db.rawQuery( "SELECT * FROM " + SITE_TABLE_NAME, null);
		getActivity().startManagingCursor( cursor );
		return cursor;
    }
    
    public void showSites( Cursor cursor){
    	
    	String[] from = { URL };
    	int[] to = { R.id.url_item };
    	//adapter = new SimpleCursorAdapter( this, R.layout.site_item, cursor, from, to);
    	adapter = new SimpleCursorAdapter(getActivity().getApplicationContext(), R.layout.site_item, cursor, from, to, 0);
		lv.setAdapter( adapter );
    }
    
    // This method is when the long click is pressed, determines which action to take based on the MenuItem selected
    @Override 
    public boolean onContextItemSelected(MenuItem item) { 
    	if( item.getItemId() == 0 || item.getItemId() == 1 ){
    		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
    		cursor.moveToPosition( info.position );
    	}
    	switch( item.getItemId() ){
    		case 0:
		    	int id = cursor.getInt( cursor.getColumnIndex( _ID ));
		    	db = st.getWritableDatabase();
		    	db.delete(SITE_TABLE_NAME, _ID + "=" + id, null);
		    	cursor.requery();
		    	String[] from = { URL };
		    	int[] to = { R.id.url_item };
		    	lv.setAdapter(new SimpleCursorAdapter(getActivity().getApplicationContext(), R.layout.site_item, cursor, from, to, 0));
		    	break;
    		case 1:
    			showDetails();
    			break;
    		case 2:
    			addSite();
    			break;
    		case 3:
    		    genPass();
    		    break;
    		case 4:
    			getGeneratedLocation();
    			break;
    	}
        return true; 
    } 
    
    @Override 
    // Long click menu options
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) { 
    	super.onCreateContextMenu(menu, v, menuInfo);
    	if(v.getId() == R.id.list_view1 ){
    		menu.add( 1, 0, Menu.NONE, "Delete" );
    		menu.add( 1, 1, Menu.NONE,"View Details" );
    	}
    }
    
    // Details of a listed site
    private void showDetails(){
    	String url = cursor.getString( cursor.getColumnIndex( URL));
    	String uName = cursor.getString( cursor.getColumnIndex( UNAME ));
    	String pWord = cursor.getString( cursor.getColumnIndex( PWORD ));
    	
    	Bundle args = new Bundle();
    	args.putString( URL,  url );
    	args.putString( UNAME, uName );
    	args.putString( PWORD, pWord );
    	((HomeActivity) getActivity()).startRightFrag(args);
    }
    
    private void addSite(){
    	Intent addSite = new Intent(getActivity().getApplicationContext(), AddPage.class);
		startActivity( addSite );
    }
    
    private void genPass() {
        Intent genPass = new Intent(getActivity().getApplicationContext(), GeneratePass.class);
        startActivity( genPass );
    }
    
    // Shows user a random location on the map while telling the user that it is getting user's current location
    // While this is happening we would like to get the current location and make note of it in a file or log
    private void getGeneratedLocation(){
    	Intent showLocation = new Intent(getActivity().getApplicationContext(), PassMapActivity.class);
    	startActivity( showLocation );
    }

	@Override
	public boolean goBack() {
		return false;
	}

	public void refreshList(){
		if(lv != null){
			getSites();
			String[] from = { URL };
	    	int[] to = { R.id.url_item };
	    	lv.setAdapter(new SimpleCursorAdapter(getActivity().getApplicationContext(), R.layout.site_item, cursor, from, to, 0));
		}
	}
}