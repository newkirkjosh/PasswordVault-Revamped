package org.research.thevault;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.research.chatclient.BaseActivity;
import org.research.chatclient.R;
import org.research.thevault.phoneactivities.OptionsActivity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class GeneratePass extends MyFragment{
    
	private final String FILENAME = "map.txt";
    private HashMap<String, String> usedSeeds;
    private final char CHARS[] = new char[]{ 'A','a','0','B','b','1','C','c','2','D','d','3','E','e','4','F','f','5','G','g','6','H','h','7','I','i','8',
        'J','j','9','K','k','L','l','M','m','N','n','O','o','P','p','Q','q','R','r','S','s','T','t','U','u','V','v','W','w','X','x','Y','y','Z','z'
    };
    
    EditText password;
    EditText minLgth;
    EditText maxLgth; 
       
    /** Called when the activity is first created. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.generate_pass, container, false);
        
        usedSeeds = new HashMap<String, String>();
        password = (EditText) v.findViewById( R.id.gen_pass );
        minLgth = (EditText) v.findViewById( R.id.min_text ); 
        maxLgth = (EditText) v.findViewById( R.id.max_text ); 
        
        Button genBtn = (Button) v.findViewById(R.id.generate_button);
        genBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				genPassword();
			}
		});
        
        Button clearBtn = (Button) v.findViewById(R.id.clear_button);
        clearBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clearButtonClicked();
			}
		});
        
        try {
            BufferedReader br = new BufferedReader( new FileReader( new File( Environment.getExternalStorageDirectory(), FILENAME) ));
            String str;
            while( (str = br.readLine()) != null ) {
                String pair[] = str.split( "=" );
                usedSeeds.put( pair[0], pair[1] );
            }
        } 
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return v;
    }

	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}*/
    
    @Override
    public void onDestroy() {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter( new File( Environment.getExternalStorageDirectory(), FILENAME) );
            Set<Entry<String, String>> seedKeys = usedSeeds.entrySet();
            for( Entry<String, String> str : seedKeys) {
                pw.println( str.toString() );
            }
        } 
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            if( pw != null ) {
                pw.close();
            }
            super.onDestroy();
        }
    }
    
    public void genPassword(){
    	
    	if( password.getText().toString().toLowerCase().equals("options") ){
    		if(getResources().getBoolean(R.bool.IsTablet) || BaseActivity.HDMI_ACTIVE){
	    		FragmentManager fm = getFragmentManager();
	    		FragmentTransaction ft = fm.beginTransaction();
	    		Fragment options = new OptionsFragment();
	    		ft.replace(R.id.list_frag, options).commit();
    		}
    		else{
				Intent intent = new Intent(getActivity(), OptionsActivity.class);
				startActivity(intent);
			}
    		password.setText("");
    	}else if( !minLgth.getText().toString().equals( "" ) && !maxLgth.getText().toString().equals( "" ) && !password.getText().toString().equals( "" )) {
    		
	        String pass = "";
	        String used = usedSeeds.get( password.getText().toString()  );
	        int maxLgthInt = 0;
	        int minLgthInt = 0;
	        
	        try {
	            maxLgthInt = Integer.parseInt( maxLgth.getText().toString() );
	            minLgthInt = Integer.parseInt( minLgth.getText().toString() );
	        }catch( NumberFormatException e ) {
	            
	        }
	        
	        if( used != null && maxLgthInt >= minLgthInt && minLgthInt >= 3 && maxLgthInt <= 16){
	            pass = used;
	            //display an alert to inform user to input time and pay rate
	            AlertDialog.Builder alertBuild = new AlertDialog.Builder( getActivity() );
	            alertBuild.setMessage( "Your secure Password is: " + pass );
	            alertBuild.setNeutralButton( "OK", null );
	            AlertDialog alert = alertBuild.create();
	            alert.show();
	        }else{
	            if( maxLgthInt >= minLgthInt && minLgthInt >= 3 ) {
	                int lgth = maxLgthInt - minLgthInt;
	                lgth = (int) (Math.random() * lgth) + minLgthInt; 
	                
	                while( !pass.matches( ".*[A-Z].*" ) || !pass.matches( ".*[a-z].*" ) || !pass.matches( ".*[0-9].*" )  ){
	                    pass = "";
	                    for( int i = 0; i < lgth; i++ ){
	                        int passCharLoc = (int) (Math.random() * CHARS.length);
	                        pass += CHARS[passCharLoc];
	                    }
	                }
	                //display an alert to inform user to input time and pay rate
	                AlertDialog.Builder alertBuild = new AlertDialog.Builder( getActivity() );
	                alertBuild.setMessage( "Your secure Password is: " + pass );
	                alertBuild.setNeutralButton( "OK", null );
	                AlertDialog alert = alertBuild.create();
	                alert.show();
	                usedSeeds.put( password.getText().toString(), pass );
	            }else {
	                //display an alert to inform user to input time and pay rate
	                AlertDialog.Builder alertBuild = new AlertDialog.Builder( getActivity() );
	                alertBuild.setMessage( "Max length must be equal or greater than Min length and Min must be greater than 2" );
	                alertBuild.setNeutralButton( "OK", null );
	                AlertDialog alert = alertBuild.create();
	                alert.show();
	            }
	        }
	    }else {
	         //display an alert to inform user to input time and pay rate
	         AlertDialog.Builder alertBuild = new AlertDialog.Builder( getActivity() );
	         alertBuild.setMessage( "Please enter a value for Min length, Max length and password to convert" );
	         alertBuild.setNeutralButton( "OK", null );
	         AlertDialog alert = alertBuild.create();
	         alert.show();
	     }
    }
     
    
    public void clearButtonClicked(){
        
        //clear button clicked so clear all EditTexts
        minLgth.setText( "" );
        maxLgth.setText( "" );
        password.setText( "" );
        minLgth.requestFocus();
    }

	@Override
	public boolean goBack() {
		// TODO Auto-generated method stub
		return false;
	}
}