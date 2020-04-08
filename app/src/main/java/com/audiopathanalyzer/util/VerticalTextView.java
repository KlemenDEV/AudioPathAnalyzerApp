package com.audiopathanalyzer.util;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import androidx.appcompat.widget.AppCompatTextView;

public class VerticalTextView extends AppCompatTextView {

	boolean topDown;

	public VerticalTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		final int gravity = getGravity();
		Gravity.isVertical(gravity);
	}

	@Override protected void onMeasure(int w, int h) {
		super.onMeasure(w, h);
		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
	}

	@Override protected void onDraw(Canvas canvas) {
		TextPaint textPaint = getPaint();
		textPaint.setColor(getCurrentTextColor());
		textPaint.drawableState = getDrawableState();

		canvas.save();

		if (topDown) {
			canvas.translate(getWidth(), 0);
			canvas.rotate(90);
		} else {
			canvas.translate(0, getHeight());
			canvas.rotate(-90);
		}

		canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());

		getLayout().draw(canvas);
		canvas.restore();
	}
}