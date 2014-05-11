package com.google.mobiletuner;

import java.io.IOException;
import java.util.UUID;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

/**
 * BluetoothService - The BluetoothService class creates the Bluetooth socket
 * connection we need to communicate to the ELM 327 Bluetooth device.
 * 
 * @author Luke Vang
 * @date May 12, 2014
 * @email luke.vang@my.uwrf.edu
 * 
 */
public class BluetoothService {
	// Debugging
	private static final String TAG = "BluetoothService";
	private static final boolean D = true;
	
	private boolean isConnected = false;

	// Bluetooth socket
	private BluetoothSocket mSocket;
	
	//Bluetooth Device
	private BluetoothDevice mDevice;
	

	/**
	 * gets our Bluetooth Socket
	 * 
	 * @return mSocket
	 */
	public BluetoothSocket getmSocket() {
		return mSocket;
	}

	/**sets our bluetooth socket
	 * 
	 * @param mSocket
	 */
	public void setmSocket(BluetoothSocket mSocket) {
		this.mSocket = mSocket;
	}

	// Our basic UUID to connect to the device
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");


	/**
	 * connect() sets up our connection to the for the ELM 327 Bluetooth Device
	 * 
	 * @param device
	 * @param handler
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws NumberFormatException
	 */
	public void connect(BluetoothDevice device, Handler handler)
			throws IOException, InterruptedException, NumberFormatException {
		if (D)
			Log.d(TAG,
					"connected to: " + device.getName() + " "
							+ device.getAddress());
		mDevice = device;
		try {
			mSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e) {
			// TODO Auto-generated catch block

			Log.d(TAG, "Socket creation has failed");

		}
		try {
			mSocket.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, "Connection has failed");
			setConnectionStatus(false);
			return;
		}
		setConnectionStatus(true);

	}

	/**
	 * Sets the status of our Bluetooth Connection
	 * 
	 * @param _status
	 */
	public void setConnectionStatus(boolean _status) {
		isConnected = _status;
	}

	/**
	 * Returns the current Bluetooth Connection
	 * 
	 * @return isConnected
	 */
	public boolean getConnectionStatus() {
		return isConnected;
	}

	
	//closeSocket closes the Bluetooth Socket we create
	public void closeSocket() {
		try {
			mSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}