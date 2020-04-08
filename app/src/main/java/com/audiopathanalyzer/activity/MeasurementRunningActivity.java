package com.audiopathanalyzer.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.audiopathanalyzer.AudioPathAnalyzer;
import com.audiopathanalyzer.R;
import com.audiopathanalyzer.connection.CalibrationExecutor;
import com.audiopathanalyzer.connection.MeasurementExecutor;
import com.audiopathanalyzer.data.Experiment;
import com.audiopathanalyzer.data.MeasurementProgress;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

public class MeasurementRunningActivity extends AppCompatActivity {

	public static final String CALIBRATION_MEASUREMENT_EXTRA = "com.audiopathanalyzer.CALIBRATION_MEASUREMENT_EXTRA";
	public static final String CALIBRATION_FORCED_EXTRA = "com.audiopathanalyzer.CALIBRATION_FORCED_EXTRA";

	private AsyncTask<Void, MeasurementProgress, Experiment> executor;

	private TextView measurement_info;
	private LineChart measurement_preview;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurement_running);

		measurement_preview = findViewById(R.id.measurement_preview);
		measurement_preview.getAxisLeft().setDrawGridLines(false);
		measurement_preview.getAxisRight().setDrawGridLines(false);
		measurement_preview.getXAxis().setDrawGridLines(false);
		measurement_preview.getXAxis().setDrawAxisLine(false);
		measurement_preview.getAxisLeft().setDrawAxisLine(false);
		measurement_preview.getAxisRight().setDrawAxisLine(false);
		measurement_preview.setDescription(null);
		measurement_preview.getAxisLeft().setDrawLabels(false);
		measurement_preview.getAxisRight().setDrawLabels(false);
		measurement_preview.getXAxis().setDrawLabels(false);
		measurement_preview.getLegend().setEnabled(false);
		measurement_preview.getAxisLeft().setDrawAxisLine(false);
		measurement_preview.setDrawBorders(false);
		measurement_preview.setDrawBorders(false);
		measurement_preview.getXAxis().setAxisMinimum(
				(float) Math.log10(AudioPathAnalyzer.getApplication().getPreferences().getMeasurementMinF()));
		measurement_preview.getXAxis().setAxisMaximum(
				(float) Math.log10(AudioPathAnalyzer.getApplication().getPreferences().getMeasurementMaxF()));
		measurement_preview.animateX(1500, Easing.Linear);
		measurement_preview.animateY(1500, Easing.Linear);
		measurement_preview.setTouchEnabled(false);

		measurement_info = findViewById(R.id.measurement_info);
		measurement_info.setAnimation(AnimationUtils.loadAnimation(this, R.anim.blink));
	}

	@Override protected void onResume() {
		super.onResume();

		measurement_preview.setData(new LineData());

		LineDataSet dataset = new LineDataSet(null, "amplitude");
		dataset.setDrawValues(false);
		dataset.setColor(R.color.colorPrimary);
		dataset.setDrawCircles(false);
		dataset.setDrawCircleHole(false);
		dataset.setDrawVerticalHighlightIndicator(false);
		dataset.setDrawHorizontalHighlightIndicator(false);

		measurement_preview.getData().addDataSet(dataset);

		if (getIntent().getBooleanExtra(CALIBRATION_MEASUREMENT_EXTRA, false)) {
			executor = new CalibrationExecutor(this)
					.setForced(getIntent().getBooleanExtra(CALIBRATION_FORCED_EXTRA, false));
			measurement_info.setText(getString(R.string.calibration_in_progress_please_wait));

			findViewById(R.id.measurement_progress_spinning).setVisibility(View.VISIBLE);
		} else {
			executor = new MeasurementExecutor(this);
			measurement_info.setText(getString(R.string.measurement_in_progress_please_wait));
		}

		executor.execute();
	}

	@Override protected void onPause() {
		super.onPause();

		executor.cancel(true);
		executor = null;
	}

	@Override protected void onDestroy() {
		super.onDestroy();
		executor = null;
	}
}
