package org.zdev.recall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(arg0);

		// do we need to start on boot?
		if(sharedPreferences.getBoolean("pref_start_on_boot", false)){
			
			// start backkground service
			arg0.startService(new Intent(arg0, BackgroundService.class));
		}
				
	}

}
