package org.research.thevault;

import org.research.chatclient.BaseActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class HdmiBroadcastReceiver extends BroadcastReceiver{
	
	private static final String EXTDISP_PUBLIC_STATE = "com.motorola.intent.action.externaldisplaystate";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Bundle extras = (intent != null) ? intent.getExtras() : null;
		
		if (action.equals(EXTDISP_PUBLIC_STATE)){
			if(extras != null){
				int hdmi = extras.getInt("hdmi");
				int hdcp = extras.getInt("hdcp");
				if(hdmi == 1 && hdcp == 1){
					BaseActivity.HDMI_ACTIVE = true;
					Log.d("HDMI", "CONNECTED");
				}
				else{
					BaseActivity.HDMI_ACTIVE = false;
					Log.d("HDMI", "DIS-CONNECTED");
				}
			}
		}
	}

}
