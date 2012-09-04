package org.research.thevault;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import org.research.chatclient.R;

public class About extends Activity{
	@Override
	protected void onCreate( Bundle savedInstanceState ){
		super.onCreate( savedInstanceState );
		setContentView( R.layout.about );
	}
	
	@Override
	public boolean onTouchEvent( MotionEvent mEvent ){
		finish();
		return true;
	}
}
