package com.newcircle.testablecalculator;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ColorButton extends Button implements OnClickListener {
	int CLICK_FEEDBACK_COLOR;
	static final int CLICK_FEEDBACK_INTERVAL = 10;
	static final int CLICK_FEEDBACK_DURATION = 350;

	float mTextX;
	float mTextY;
	long mAnimStart;
	OnClickListener mListener;

	public ColorButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(!isInEditMode()) {
			mListener = (OnClickListener)context;
			setOnClickListener(this);
		}
	}

	public void onClick(View view) {
		mListener.onClick(this);
	}

	@Override
	public void onSizeChanged(int w, int h, int oldW, int oldH) {
		measureText();
	}

	private void measureText() {
		Paint paint = getPaint();
		mTextX = (getWidth() - paint.measureText(getText().toString())) / 2;
		mTextY = (getHeight() - paint.ascent() - paint.descent()) / 2;
	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int before, int after) {
		measureText();
	}
}
