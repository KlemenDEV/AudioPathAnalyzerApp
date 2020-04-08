package com.audiopathanalyzer.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;
import com.audiopathanalyzer.R;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

@SuppressLint("ViewConstructor") public class ShowValueMarkerView extends MarkerView implements IMarker {

	private TextView tvContent;

	public ShowValueMarkerView(Context context, int layoutResource) {
		super(context, layoutResource);
		tvContent = findViewById(R.id.tvContent);
	}

	@SuppressLint({ "DefaultLocale", "SetTextI18n" }) @Override
	public void refreshContent(Entry e, Highlight highlight) {
		tvContent.setText(String.format("%.2f", e.getY()) + " dB at " + (int) Math.pow(10, e.getX()) + " Hz");
		super.refreshContent(e, highlight);
	}

	private MPPointF mOffset;

	@Override public MPPointF getOffset() {
		if (mOffset == null) {
			mOffset = new MPPointF(-getWidth() - 10, -getHeight() - 10);
		}
		return mOffset;
	}

}
