package com.google.mobiletuner;

import java.io.IOException;

import pt.lighthouselabs.obd.commands.ObdCommand;
import pt.lighthouselabs.obd.commands.SpeedObdCommand;
import pt.lighthouselabs.obd.commands.control.DtcNumberObdCommand;
import pt.lighthouselabs.obd.commands.engine.EngineLoadObdCommand;
import pt.lighthouselabs.obd.commands.engine.EngineRPMObdCommand;
import pt.lighthouselabs.obd.commands.engine.MassAirFlowObdCommand;
import pt.lighthouselabs.obd.commands.engine.ThrottlePositionObdCommand;
import pt.lighthouselabs.obd.commands.fuel.FuelLevelObdCommand;
import pt.lighthouselabs.obd.commands.protocol.ObdResetCommand;
import pt.lighthouselabs.obd.commands.protocol.SelectProtocolObdCommand;
import pt.lighthouselabs.obd.commands.temperature.EngineCoolantTemperatureObdCommand;
import pt.lighthouselabs.obd.enums.ObdProtocols;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * OBDCommandThread - This is our thread class. It handles send and receiving
 * data from the ECU. This class uses the methods in the
 * pt.lighthouselabs.obd.commands api to talk to the ECU.
 * 
 * @author Luke Vang
 * @date May 12, 2014
 * @email luke.vang@my.uwrf.edu
 * 
 * 
 */
public class OBDCommandThread implements Runnable {

	// Used for Debugging
	private final String TAG = "OBDCommandThread";

	private boolean isRunning = true;

	// Handler object
	private Handler mHandler;

	// Thread object
	private Thread mThread;

	// Bluetooth Socket
	private BluetoothSocket socket;

	private ObdCommand[] obdCommandList = new ObdCommand[8]; // The 8 commands I
																// have hard
																// coded.
																// Need to
																// generate a
																// list of
																// possible
																// commands that
																// the user can
																// select from
																// if possible.

	private String[] keys = new String[8]; // The keys we are going to use for
											// our handler

	/**
	 * 
	 * @param _socket
	 * @param _handler
	 */
	public OBDCommandThread(BluetoothSocket _socket, Handler _handler) {
		if (_socket == null || _handler == null) {
			return;
		}

		socket = _socket;
		mHandler = _handler;

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		try {
			new ObdResetCommand().run(socket.getInputStream(),
					socket.getOutputStream());
			new SelectProtocolObdCommand(ObdProtocols.AUTO).run(
					socket.getInputStream(), socket.getOutputStream());
			Log.e(TAG, "Protocols Set to Auto");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Loop continues to run if isRunning is true

		while (isRunning) {
			/*
			 * Each of these index in this array have to be instantiated again
			 * in the loop or we get a NumberformateException. Have not found
			 * another way to not get that exception except this way.
			 */
			obdCommandList[0] = (new EngineRPMObdCommand());
			obdCommandList[1] = (new SpeedObdCommand());
			obdCommandList[2] = (new EngineLoadObdCommand());
			obdCommandList[3] = (new MassAirFlowObdCommand());
			obdCommandList[4] = (new EngineCoolantTemperatureObdCommand());
			obdCommandList[5] = (new ThrottlePositionObdCommand());
			obdCommandList[6] = (new FuelLevelObdCommand());
			obdCommandList[7] = (new DtcNumberObdCommand());

			keys[0] = ("ENGINE_RPM");
			keys[1] = ("VEHICLE_SPEED");
			keys[2] = ("ENGINE_LOAD");
			keys[3] = ("MASS_AIR_FLOW");
			keys[4] = ("ENGINE_COOLANT");
			keys[5] = ("THROTTLE");
			keys[6] = ("FUEL_LEVEL");
			keys[7] = ("CHECK_ENGINE");

			try {

				for (int i = 0; i < obdCommandList.length; i++) {

					obdCommandList[i].run(socket.getInputStream(),
							socket.getOutputStream());
					Log.e(TAG, obdCommandList[i].getFormattedResult());

					mHandler.obtainMessage().sendToTarget();
					Message msg = mHandler.obtainMessage();
					Bundle bundle = new Bundle();

					bundle.putString(keys[i],
							obdCommandList[i].getFormattedResult());

					msg.setData(bundle);
					mHandler.sendMessage(msg);

				}

			} catch (IOException e) {
				Log.e(TAG, e.getMessage());

			} catch (InterruptedException e) {
				Log.e(TAG, e.getMessage());

			} catch (NumberFormatException e) {
				Log.e(TAG, e.getMessage());

			} catch (NullPointerException e) {

				Log.e(TAG, e.getMessage());
			}

		}

	}

	// Start thread
	public void start() {
		Log.e(TAG, "STARTING THREAD");
		if (mThread == null) {
			mThread = new Thread(this);
			mThread.start();
		}

	}

	// Stop thread
	public void stop() {
		isRunning = false;
	}

}