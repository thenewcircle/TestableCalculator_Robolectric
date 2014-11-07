package com.newcircle.testablecalculator;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.newcircle.calculatorlib.Logic;
import com.newcircle.testablecalculator.R;
import com.newcircle.testablecalculator.R.color;
import com.newcircle.testablecalculator.ShakeListener.OnShakeListener;

public class Calculator extends Activity implements View.OnClickListener, TextWatcher, OnShakeListener{
	Button mFirstNumberB, mSecondNumberB, mActiveNumber;
	EditText mFunctionET;
	Button mOpB;
	TextView mResultsTV;

	ShakeListener mShakeListener;
	Vibrator mVibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calculator);	

		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(color.newCircleBG)));

		mVibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

		mShakeListener = new ShakeListener(this, this);

		mFirstNumberB = (Button)findViewById(R.id.firstNumber);
		mSecondNumberB = (Button)findViewById(R.id.secondNumber);
		mResultsTV = (TextView)findViewById(R.id.result);
		mOpB = (Button)findViewById(R.id.operationButton);
		mFunctionET = (EditText)findViewById(R.id.function);

		mOpB.setOnClickListener(this);
		mFirstNumberB.setOnClickListener(this);
		mSecondNumberB.setOnClickListener(this);

		mFunctionET.addTextChangedListener(this);

		mActiveNumber = mFirstNumberB;
		onClick(mActiveNumber);
	} 

	@Override
	protected void onResume() {
		mShakeListener.onResume();
		super.onResume();
	}

	@Override 
	protected void onPause() {
		mShakeListener.onPause();
		super.onPause();
	}

	@SuppressLint("DefaultLocale")
	public static String fmt(double d)
	{
		if(d == (long)d)
			return Long.toString((long)d);
		else 
			return String.format("%.4f", d);
	}

	private Double CalcResults() throws NumberFormatException, Exception {
		String results = Logic.evaluate(mFunctionET.getText().toString());
		if(results.charAt(0) == Logic.MINUS) {
			Double ret = Double.valueOf(results.substring(1));
			return ret*-1.0;
		} else
			return Double.valueOf(results);
	}

	private void Calc() {
		try {
			String results = fmt(CalcResults());
			mResultsTV.setText(results);
		} catch (NumberFormatException e) {
		} catch (Exception e) {
		}
	}

	@Override
	public void onClick(View v) {
		Boolean updateFunction = true;
		if(v == mOpB) {
			String opText = mOpB.getText().toString();
			if(opText.equals(getString(R.string.plus)))
				mOpB.setText(getString(R.string.div));
			else if(opText.equals(getString(R.string.div)))
				mOpB.setText(getString(R.string.mul));
			else if(opText.equals(getString(R.string.mul)))
				mOpB.setText(getString(R.string.minus));
			else 
				mOpB.setText(getString(R.string.plus));
		} else if(v == mFirstNumberB || v == mSecondNumberB) {
			mActiveNumber.setBackgroundColor(getResources().getColor(color.editSelectedFalse));
			mActiveNumber = (Button)v;
			mActiveNumber.setBackgroundColor(getResources().getColor(color.editSelectedTrue));
			updateFunction = false;
		} else if(v == findViewById(R.id.digit0)) {
			if(mActiveNumber.getText().toString().length() > 0)
				mActiveNumber.setText(mActiveNumber.getText().toString() + "0");
		} else if(v == findViewById(R.id.digit1) ||
				v == findViewById(R.id.digit2) ||
				v == findViewById(R.id.digit3) || 
				v == findViewById(R.id.digit4) || 
				v == findViewById(R.id.digit5) || 
				v == findViewById(R.id.digit6) || 
				v == findViewById(R.id.digit7) || 
				v == findViewById(R.id.digit8) || 
				v == findViewById(R.id.digit9) ||
				v == findViewById(R.id.dot)) {
			mActiveNumber.setText(mActiveNumber.getText().toString() + ((ColorButton)v).getText());
		} else if(v == findViewById(R.id.clear)) {
			mActiveNumber.setText("");
		} else if(v == findViewById(R.id.plus) ||
				v == findViewById(R.id.div) ||
				v == findViewById(R.id.mul) ||
				v == findViewById(R.id.minus)) {
			mOpB.setText(((ColorButton)v).getText());
		} 

		if(updateFunction && !mFirstNumberB.getText().toString().isEmpty() && !mSecondNumberB.getText().toString().isEmpty()) {
			mFunctionET.setText(mFirstNumberB.getText() + " " + mOpB.getText() + " " + mSecondNumberB.getText());
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		Calc();
	}

	@Override
	public void afterTextChanged(Editable s) {		
	}

	@Override
	public void onShake() {
		mVibrator.vibrate(100);

		mFirstNumberB.setText("");
		mSecondNumberB.setText("");
	}
}
