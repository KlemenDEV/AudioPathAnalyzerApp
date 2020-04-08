package com.audiopathanalyzer.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.audiopathanalyzer.R;
import com.audiopathanalyzer.data.Experiment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MeasurementResultsGeneralActivity extends AppCompatActivity {

	@SuppressLint({ "SetTextI18n", "DefaultLocale" }) @Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurement_results_general);

		Experiment result = (Experiment) getIntent()
				.getSerializableExtra(MeasurementResultsActivity.EXPERIMENT_RESULT_EXTRA);

		TextView invalid_takes = findViewById(R.id.invalid_takes);
		TextView thd_value = findViewById(R.id.thd_value);
		TextView max_db = findViewById(R.id.max_db);
		TextView dc_offset = findViewById(R.id.dc_offset);
		TextView results_general_score = findViewById(R.id.results_general_score);

		ImageButton export_measurement = findViewById(R.id.export_measurement);

		results_general_score.setText("" + result.getFancyScore());
		invalid_takes.setText("" + result.getInvalidCount());
		dc_offset.setText("" + String.format("%.3f", result.getDCOffset()) + " mV");
		thd_value.setText("" + String.format("%.3f", result.getTHD1khz()) + " %");
		max_db.setText("" + String.format("%.2f", result.getMinDB()) + " dB");

		if (result.isLoaded())
			export_measurement.setVisibility(View.INVISIBLE);
	}

	public void exportMeasurement(View view) {
		String date = new SimpleDateFormat("yyyy-MM-dd--HH-mm-ss", Locale.getDefault()).format(new Date());

		Intent exportIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
		exportIntent.addCategory(Intent.CATEGORY_OPENABLE);
		exportIntent.setType("application/json");
		exportIntent.putExtra(Intent.EXTRA_TITLE, "Experiment_" + date);

		this.getParent().startActivityForResult(exportIntent, MeasurementResultsActivity.EXPORT_MEASUREMENT_REQUEST_CODE);
		this.getParent().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	@Override public void onBackPressed() {
		this.getParent().onBackPressed();
	}

}
