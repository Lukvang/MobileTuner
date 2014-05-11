package com.google.mobiletuner;

import java.io.IOException;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * MainAcitivty - This is the main UI the user uses to navigate activities and
 * connect to Bluetooth Device
 * 
 * You will need an ELM 327 Bluetooth Device to make this application work!!
 * 
 * @author Luke Vang
 * @date May 12, 2014
 * @email luke.vang@my.uwrf.edu
 * 
 * 
 * @author pires
 * pt.lighthouselabs.obd.commands api was taken from 
 * https://github.com/pires/obd-java-api
 * 
 */
public class MainActivity extends Activity {

	// Debugging
	public static final String TAG = "MainActivity";
	public static final boolean D = true;

	// Bluetooth
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice mBluetoothDevice;
	private boolean isConnected = false;

	// Handler
	private Handler mHandler;

	// Intent
	private Intent intent = null;

	// Intent request code
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_ENABLE_BT = 2;

	// ImageView Buttons
	private ImageView connect_button;
	private ImageView gauge_cluster_button;
	private ImageView graph_vew_button;
	private ImageView liveLogging_button;
	private ImageView settings_button;
	private ImageView trouble_codes_button;

	// TextViews
	private TextView connect_bttn_textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (D)
			Log.d(TAG, "onCreate ");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		connect_button = (ImageView) findViewById(R.id.connectButtonImage);
		gauge_cluster_button = (ImageView) findViewById(R.id.gaugeClusterImage);
		graph_vew_button = (ImageView) findViewById(R.id.graphViewImage);
		liveLogging_button = (ImageView) findViewById(R.id.liveDataImage);
		settings_button = (ImageView) findViewById(R.id.settingsImage);
		trouble_codes_button = (ImageView) findViewById(R.id.troubleCodesImage);
		connect_bttn_textView = (TextView) findViewById(R.id.connectText);

		updateButton();
		initButtons();

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(getApplicationContext(),
					"Bluetooth Service Not Supported!", Toast.LENGTH_SHORT)
					.show();
			finish();
			return;
		} else if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

		}

		connect_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Connect Button ");
				if (!isConnected) {
					intent = new Intent(MainActivity.this,
							DeviceListActivity.class);
					startActivityForResult(intent,
							REQUEST_CONNECT_DEVICE_SECURE);
				} else if (isConnected) {
					closeSocket();
				}

			}

		});

	}

	/*
	 * During the onActivityResult method, we get a request code from the
	 * deviceListActivty If the connection is made then isConnected is set to
	 * true, if the connection fails we get toast message that shows us the
	 * connection failed
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE_SECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				try {
					try {
						connectDevice(data);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (IOException e) {
					e.printStackTrace();
					((ApplicationClass) this.getApplication()).mBluetoothService
							.setConnectionStatus(false);
				}
				if (((ApplicationClass) this.getApplication()).mBluetoothService
						.getConnectionStatus()) {
					Toast.makeText(getApplicationContext(),
							"Bluetooth Socket Connected", Toast.LENGTH_SHORT)
							.show();
					((ApplicationClass) this.getApplication()).mBluetoothService
							.setConnectionStatus(true);
					isConnected = true;
					updateButton();
				} else if (!((ApplicationClass) this.getApplication()).mBluetoothService
						.getConnectionStatus()) {
					Toast.makeText(getApplicationContext(),
							"Bluetooth Socket Connection Failed",
							Toast.LENGTH_SHORT).show();
					((ApplicationClass) this.getApplication()).mBluetoothService
							.setConnectionStatus(false);
					isConnected = false;
					updateButton();
				}
				break;
			}
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				return;
			} else {
				// User did not enable Bluetooth or an error occurred
				Log.d(TAG, "BT not enabled");
				return;
			}
		}
	}

	// Method used to create connection to device
	public void connectDevice(Intent data) throws IOException,
			NumberFormatException, InterruptedException {
		if (D)
			Log.d(TAG, "connectDevice ");
		String address = data.getExtras().getString(
				DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		if (D)
			Log.d(TAG, address);
		mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
		if (D) {
			Log.d(TAG, mBluetoothDevice.getAddress());
			((ApplicationClass) this.getApplication()).mBluetoothService
					.connect(mBluetoothDevice, mHandler);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	// initButtons initiates all the onClickListeners for buttons
	public void initButtons() {

		graph_vew_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, "Graph View Button ");
				if (isConnected) {
					intent = new Intent(MainActivity.this,
							GraphViewActivity.class);
					startActivity(intent);
				} else if (!isConnected) {
					Toast.makeText(getApplicationContext(),
							"ELM327 device not connected!", Toast.LENGTH_LONG)
							.show();
				}
			}

		});

		gauge_cluster_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, "Gauge Cluster Button ");
				if (isConnected) {
					intent = new Intent(MainActivity.this,
							ClusterGaugeActivity.class);
					startActivity(intent);
				} else if (!isConnected) {
					Toast.makeText(getApplicationContext(),
							"ELM327 device not connected!", Toast.LENGTH_LONG)
							.show();
				}
			}

		});

		liveLogging_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, "Live Logging Button ");
				Toast.makeText(getApplicationContext(),
						"Live Logging Coming Soon!", Toast.LENGTH_LONG).show();
			}

		});
		settings_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, "Settings Button ");
				Toast.makeText(getApplicationContext(),
						"Settings Coming Soon!", Toast.LENGTH_LONG).show();
			}

		});
		trouble_codes_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, "Trouble Codes Button ");
				Toast.makeText(getApplicationContext(),
						"Trouble Codes Coming Soon!", Toast.LENGTH_LONG).show();
			}

		});

	}

	// updateButton updates the text of the connect button
	public void updateButton() {
		if (!isConnected) {
			connect_bttn_textView.setText("Connect");
		} else if (isConnected) {
			connect_bttn_textView.setText("Disconnect");
		}

	}

	// closeSocket allows us to close the bluetooth socket
	public void closeSocket() {
		((ApplicationClass) this.getApplication()).mBluetoothService
				.closeSocket();
	}

}
