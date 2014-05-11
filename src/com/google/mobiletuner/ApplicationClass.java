package com.google.mobiletuner;

import android.app.Application;

/**
 * ApplicationClass - This class allows our Bluetooth Connection to live through
 * the life of the application. This means that we do not have to restart our
 * Bluetooth connection in every activity. The connection remains activity
 * throughout the life-time of the applicaiton.
 * 
 * @author Luke Vang
 * @date May 12, 2014
 * @email luke.vang@my.uwrf.edu
 * 
 */
public class ApplicationClass extends Application {

	public BluetoothService mBluetoothService;

	@Override
	public void onCreate() {
		super.onCreate();

		mBluetoothService = new BluetoothService();
	}

}
