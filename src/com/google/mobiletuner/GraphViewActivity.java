package com.google.mobiletuner;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

/**
 * GraphViewActivity - Creates the graphs in our graph view. Uses
 * com.jjoe64.graphview API to create the graphs
 * 
 * @author Luke Vang
 * @date May 12, 2014
 * @email luke.vang@my.uwrf.edu
 * 
 */
public class GraphViewActivity extends Activity {
	// For debugging
	private final String TAG = "GraphView_Activity";
	private static final boolean D = true;

	// Used to graph in GraphView
	private GraphView graphView;
	private GraphViewSeries series1;
	private GraphViewSeries series2;

	double graph2LastXValue = 5d; // last x value we want to start graphing at

	// OBDCommandThread
	private OBDCommandThread obdCommandThread;

	private LinearLayout layout;

	// Setting RPM and MPH at 0 to being with
	private double RPM = 0;
	private double MPH = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		if (D)
			Log.e(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph_view);

		graphView = new LineGraphView(getApplicationContext(),
				"Graph View Displaying RPM x1000");
		((LineGraphView) graphView).setDrawBackground(true);
		series1 = new GraphViewSeries(new GraphViewData[] { new GraphViewData(
				0, 5d) });

		graphView.setManualYAxisBounds(10000, 500);
		graphView.setVerticalLabels(new String[] { "10", "9", "8", "7", "6",
				"5", "4", "3", "2", "1", "0" });

		graphView.addSeries(series1);

		graphView.setViewPort(1, 8);
		graphView.setScalable(true);

		layout = (LinearLayout) findViewById(R.id.graph);

		layout.addView(graphView);

		// /
		// *********************************************************************

		graphView = new LineGraphView(getApplicationContext(),
				"Graph View Displaying MPH");

		series2 = new GraphViewSeries(new GraphViewData[] { new GraphViewData(
				0, 5d) });

		graphView.setManualYAxisBounds(120, 5);
		graphView.setVerticalLabels(new String[] { "100", "90", "80", "70",
				"60", "50", "40", "30", "20", "10", "05" });

		graphView.addSeries(series2);

		graphView.setViewPort(1, 8);
		graphView.setScalable(true);

		layout = (LinearLayout) findViewById(R.id.graph2);

		layout.addView(graphView);

		obdCommandThread = new OBDCommandThread(
				((ApplicationClass) this.getApplication()).mBluetoothService
						.getmSocket(),
				mHandler);

	}

	@Override
	protected void onStart() {

		if (D)
			Log.e(TAG, "onStart");
		super.onStart();
		this.obdCommandThread.start();

	}

	@Override
	protected void onResume() {
		if (D)
			Log.e(TAG, "onResume");

		super.onResume();
		this.obdCommandThread.start();

	}

	@Override
	protected void onPause() {
		if (D)
			Log.e(TAG, "onPause");

		super.onPause();
		this.obdCommandThread.stop();

	}

	@Override
	protected void onStop() {
		if (D)
			Log.e(TAG, "onStop");

		super.onStop();
		this.obdCommandThread.stop();

	}

	// Handler to receive messages back from the thread. Using this to send data
	// to our graph
	// new data will be placed at the last x value in our graph

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message message) {

			String result;
			if (message.getData().containsKey("ENGINE_RPM")) {
				result = message.getData().getString("ENGINE_RPM");
				RPM = Double.parseDouble(result);
				Log.e(TAG, "RESULT" + RPM);
			}

			if (message.getData().containsKey("VEHICLE_SPEED")) {
				result = message.getData().getString("VEHICLE_SPEED");
				MPH = Double.parseDouble(result);
				Log.e(TAG, "RESULT" + RPM);

				Log.e(TAG, " graph2LastXValue:" + graph2LastXValue);
				Log.e(TAG, " RPM:" + RPM);

				graph2LastXValue += 1d;

				series1.appendData(new GraphViewData(graph2LastXValue, RPM),
						true, 10);
				series2.appendData(new GraphViewData(graph2LastXValue, MPH),
						true, 10);

			}
		}

	};

}
