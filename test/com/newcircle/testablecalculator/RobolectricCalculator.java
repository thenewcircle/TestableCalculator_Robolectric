package com.newcircle.testablecalculator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ActivityController;

import android.annotation.SuppressLint;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.newcircle.calculatorlib.Logic.OP;

@RunWith(RobolectricTestRunner.class)
public class RobolectricCalculator extends BaseTest {
	private static final Double MIN_RANDOM_D = 0.0;
	private static final Double MAX_RANDOM_D = 100000.0;

	private static final Integer MIN_RANDOM_I = 0;
	private static final Integer MAX_RANDOM_I = 100000;
	
	private final ActivityController<Calculator> mAUTController =  Robolectric.buildActivity(Calculator.class);
	Calculator mAUT; // Activity Under Test
	
	Random mRandom = new Random();
	
	EditText mFunctionET;
	TextView mResultTV;
	Button mOpBtn;
	
	@Before
	public void setup(){
		mAUT = mAUTController.create().start().resume().get();
		
		mFunctionET = (EditText)mAUT.findViewById(R.id.function);
		mResultTV = (TextView)mAUT.findViewById(R.id.result);
		mOpBtn = (Button)mAUT.findViewById(R.id.operationButton);
	}
	
	@Test
	public void sanityTest()  throws Exception
	{
		String  appName = mAUT.getString(R.string.app_name);
		assertThat(appName, equalTo("NewCalculator"));
	}
	
	@Test
	public void basicDoubleTest() {
		singleOP(1, 2, OP.PLUS);

		singleOP(getRandomDouble(), getRandomDouble(), OP.DIV);

		singleOP(getRandomDouble(), getRandomDouble(), OP.MUL);

		singleOP(getRandomDouble(), getRandomDouble(), OP.PLUS);

		singleOP(getRandomDouble(), getRandomDouble(), OP.MINUS);
	}
	
	@Test
	public void basicIntegerTest() {
		singleOP(getRandomInteger(), getRandomInteger(), OP.DIV);

		singleOP(getRandomInteger(), getRandomInteger(), OP.MUL);

		singleOP(getRandomInteger(), getRandomInteger(), OP.PLUS);

		singleOP(getRandomInteger(), getRandomInteger(), OP.MINUS);
	}
	
	private void singleOP(Double x, Double y, OP op) {
		calcByEditText(x, y, op);
	}

	private void singleOP(Integer x, Integer y, OP op) {
		singleOP((double)x, (double)y, op);
	}
	
	public void calcByEditText(Double x, Double y, OP op) {
		String expResult = fmt(calcGoldenModel(x, y, op));

		mFunctionET.setText(String.valueOf(x) + opToString(op) + String.valueOf(y));
		
		String result = mResultTV.getText().toString();
		assertThat(result, equalTo(expResult));
	}
	
	private Double getRandomDouble() {
		return MIN_RANDOM_D + (MAX_RANDOM_D - MIN_RANDOM_D)*mRandom.nextDouble();
	}

	private Integer getRandomInteger() {
		return MIN_RANDOM_I +  mRandom.nextInt(MAX_RANDOM_I-MIN_RANDOM_I);
	}
	
	@SuppressLint("DefaultLocale")
	public static String fmt(double d)
	{
		if(d == (long)d)
			return Long.toString((long)d);
		else 
			return String.format("%.4f", d);
	}
}
