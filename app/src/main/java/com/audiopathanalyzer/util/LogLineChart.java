package com.audiopathanalyzer.util;

import android.content.Context;
import android.util.AttributeSet;
import com.audiopathanalyzer.AudioPathAnalyzer;
import com.audiopathanalyzer.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.List;

public class LogLineChart extends LineChart {
	
	public LogLineChart(Context context) {
		super(context);
		initCustom(context);
	}

	public LogLineChart(Context context, AttributeSet attrs) {
		super(context, attrs);
		initCustom(context);
	}

	public LogLineChart(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initCustom(context);
	}

	public void setEntryList(List<Entry> entryList) {
		setXAxisRenderer(
				new LogXAxisRenderer(getViewPortHandler(), getXAxis(),
						getTransformer(YAxis.AxisDependency.LEFT), entryList));
		getXAxis().setLabelCount(entryList.size(), true);
	}
	
	private void initCustom(Context context) {
		setPinchZoom(true);
		setScaleYEnabled(true);
		setScaleXEnabled(true);
		getLegend().setEnabled(false);
		setDescription(null);

		setMarker(new ShowValueMarkerView(context.getApplicationContext(), R.layout.marker_layout));

		getXAxis().setAxisMinimum(
				(float) Math.log10(AudioPathAnalyzer.getApplication().getPreferences().getMeasurementMinF()));
		getXAxis().setAxisMaximum(
				(float) Math.log10(AudioPathAnalyzer.getApplication().getPreferences().getMeasurementMaxF()));

		getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

		getAxisRight().setDrawGridLines(false);
		getAxisRight().setDrawAxisLine(false);
		getAxisRight().setDrawLabels(false);

		getXAxis().setValueFormatter(new ValueFormatter() {
			@Override public String getFormattedValue(float value) {
				int f = (int) Math.pow(10, value);
				if (f % 10 == 0)
					return "" + f;
				else
					return "";
			}
		});
	}
	
}
