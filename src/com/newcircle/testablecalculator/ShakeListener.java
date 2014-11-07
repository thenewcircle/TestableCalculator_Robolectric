package com.newcircle.testablecalculator;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeListener implements SensorEventListener 
{
	private static final int FORCE_THRESHOLD = 450;
	private static final int TIME_THRESHOLD = 250;
	private static final int SHAKE_TIMEOUT = 500;
	private static final int SHAKE_DURATION = 1000;
	private static final int SHAKE_COUNT = 3;

	private SensorManager mSensorMgr;
	private float mLastX=-1.0f, mLastY=-1.0f, mLastZ=-1.0f;
	private long mLastTime;
	private OnShakeListener mShakeListener;
	private Context mContext;
	private int mShakeCount = 0;
	private long mLastShake;
	private long mLastForce;

	public interface OnShakeListener
	{
		public void onShake();
	}

	public ShakeListener(Context context) 
	{ 
		this(context, null);
	}

	public ShakeListener(Context context, OnShakeListener listener) 
	{ 
		mContext = context;
		mSensorMgr = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
	
		if(listener != null) setOnShakeListener(listener);		
	}
	
	public void setOnShakeListener(OnShakeListener listener)
	{
		mShakeListener = listener;
	}

	public void onResume() {
		if (mSensorMgr == null) {
			throw new UnsupportedOperationException("Sensors not supported");
		}

		boolean supported = mSensorMgr.registerListener(this,
				mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);

		if (!supported) {
			mSensorMgr.unregisterListener(this);
			throw new UnsupportedOperationException("Accelerometer not supported");
		}
	}

	public void onPause() {
		if (mSensorMgr != null) {
			mSensorMgr.unregisterListener(this);
		}
	}


	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)  
			return;

		float[] values = event.values;

		long now = System.currentTimeMillis();

		if ((now - mLastForce) > SHAKE_TIMEOUT) {
			mShakeCount = 0;
		}

		if ((now - mLastTime) > TIME_THRESHOLD) {
			long diff = now - mLastTime;
			float speed = Math.abs(values[0] + values[1] + values[2] - mLastX - mLastY - mLastZ) / diff * 10000;
			if (speed > FORCE_THRESHOLD) {
				if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION)) {
					mLastShake = now;
					mShakeCount = 0;
					if (mShakeListener != null) { 
						mShakeListener.onShake(); 
					}
				}
				mLastForce = now;
			}
			mLastTime = now;
			mLastX = values[0];
			mLastY = values[1];
			mLastZ = values[2];
		}		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
}
