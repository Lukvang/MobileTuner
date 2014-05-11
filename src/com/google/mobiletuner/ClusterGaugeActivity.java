package com.google.mobiletuner;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

/**
 * ClusterGaugeActivity - This activity grabs the data from the ECU by calling
 * the obdCommandThread and passing that data to our Handler. The data from the
 * Handler is then put into a string and passed to our text views.
 * 
 * @author Luke Vang
 * @date May 12, 2014
 * @email luke.vang@my.uwrf.edu
 * 
 * 
 */
public class ClusterGaugeActivity extends Activity {

	// TextView
	private TextView rpm;
	private TextView vehicle_speed;
	private TextView engine_load;
	private TextView mass_air_flow;
	private TextView engine_coolent;
	private TextView throttle_position;
	private TextView fuel_level;
	private String font_path = "fonts/digital7.ttf";
	private Typeface typeFace;

	// OBDCommandThread object
	private OBDCommandThread obdCommandThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clustergauge_activity);

		typeFace = Typeface.createFromAsset(getAssets(), font_path);
		rpm = (TextView) findViewById(R.id.rpm_text);
		rpm.setTypeface(typeFace);
		vehicle_speed = (TextView) findViewById(R.id.vehicle_speed);
		vehicle_speed.setTypeface(typeFace);
		engine_load = (TextView) findViewById(R.id.engine_load);
		engine_load.setTypeface(typeFace);
		mass_air_flow = (TextView) findViewById(R.id.mass_air_flow);
		mass_air_flow.setTypeface(typeFace);
		engine_coolent = (TextView) findViewById(R.id.engine_coolent);
		engine_coolent.setTypeface(typeFace);
		throttle_position = (TextView) findViewById(R.id.throttle_position);
		throttle_position.setTypeface(typeFace);
		fuel_level = (TextView) findViewById(R.id.fuel_text);
		fuel_level.setTypeface(typeFace);
		fuel_level.setTextColor(Color.RED);

		obdCommandThread = new OBDCommandThread(
				((ApplicationClass) this.getApplication()).mBluetoothService
						.getmSocket(),
				mHandler);
	}

	@Override
	protected void onStart() {
		super.onStart();
		obdCommandThread.start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		obdCommandThread.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		obdCommandThread.stop();
	}

	@Override
	protected void onStop() {
		super.onStop();
		obdCommandThread.stop();
	}

	/*
	 * Handler grabs data to be set to our textViews. Checks each key and
	 * assigns the text view in respect to the key it finds.
	 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			String result;

			if (message.getData().containsKey("ENGINE_RPM")) {
				result = message.getData().getString("ENGINE_RPM");
				rpm.setText(result);

			} else if (message.getData().containsKey("VEHICLE_SPEED")) {
				result = message.getData().getString("VEHICLE_SPEED");
				vehicle_speed.setText(result);

			} else if (message.getData().containsKey("ENGINE_LOAD")) {
				result = message.getData().getString("ENGINE_LOAD");
				engine_load.setText(result);

			} else if (message.getData().containsKey("MASS_AIR_FLOW")) {
				result = message.getData().getString("MASS_AIR_FLOW");
				mass_air_flow.setText(result);

			} else if (message.getData().containsKey("ENGINE_COOLANT")) {
				result = message.getData().getString("ENGINE_COOLANT");
				engine_coolent.setText(result);

			} else if (message.getData().containsKey("THROTTLE")) {
				result = message.getData().getString("THROTTLE");
				throttle_position.setText(result);

			} else if (message.getData().containsKey("FUEL_LEVEL")) {
				result = message.getData().getString("FUEL_LEVEL");
				fuel_level.setText("Fuel Level: " + result);
			}

		}

	};

}
