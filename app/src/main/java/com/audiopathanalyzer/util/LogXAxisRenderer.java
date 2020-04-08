package com.audiopathanalyzer.util;

import android.graphics.Canvas;
import android.graphics.Path;
import android.util.Log;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

public class LogXAxisRenderer extends XAxisRenderer {

	private final List<Float> posBuilder;

	public LogXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans, List<Entry> entries) {
		super(viewPortHandler, xAxis, trans);

		int min = (int) Math.pow(10, entries.get(0).getX());
		int max = (int) Math.pow(10, entries.get(entries.size() - 1).getX());

		int min_pow = (int) Math.ceil(Math.log10(min)) - 1;
		int max_pow = (int) Math.ceil(Math.log10(max));

		posBuilder = new ArrayList<>();
		for (int j = min_pow; j <= max_pow; j++) {
			for (int i = 0; i < 10; i++) {
				float value = (float) (Math.pow(10, j) * i);
				if (value >= min && value <= max)
					posBuilder.add((float) Math.log10(value));
			}
		}

		Log.e("sdhsdfhsdh", posBuilder.toString());
	}

	@Override public void renderGridLines(Canvas c) {

		if (!mXAxis.isDrawGridLinesEnabled() || !mXAxis.isEnabled())
			return;

		int clipRestoreCount = c.save();
		c.clipRect(getGridClippingRect());

		if (mRenderGridLinesBuffer.length != posBuilder.size() * 2) {
			mRenderGridLinesBuffer = new float[posBuilder.size() * 2];
		}
		float[] positions = mRenderGridLinesBuffer;

		for (int i = 0; i < positions.length; i += 2) {
			positions[i] = posBuilder.get(i / 2);
			positions[i + 1] = posBuilder.get(i / 2);
		}

		mTrans.pointValuesToPixel(positions);

		setupGridPaint();

		Path gridLinePath = mRenderGridLinesPath;
		gridLinePath.reset();

		for (int i = 0; i < positions.length; i += 2) {

			drawGridLine(c, positions[i], positions[i + 1], gridLinePath);
		}

		c.restoreToCount(clipRestoreCount);
	}

}
