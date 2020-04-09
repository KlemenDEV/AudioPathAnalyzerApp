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

	private final TextView tvContent;
	private final String format;

	public ShowValueMarkerView(Context context, int layoutResource, String format) {
		super(context, layoutResource);
		tvContent = findViewById(R.id.tvContent);
		this.format = format;
	}

	@SuppressLint({ "DefaultLocale", "SetTextI18n" }) @Override
	public void refreshContent(Entry e, Highlight highlight) {
		tvContent.setText(String.format(format, e.getY(), (int) Math.pow(10, e.getX())));
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
