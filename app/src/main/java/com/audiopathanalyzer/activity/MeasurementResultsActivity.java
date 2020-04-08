package com.audiopathanalyzer.activity;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;
import com.audiopathanalyzer.R;
import com.audiopathanalyzer.data.Experiment;
import com.audiopathanalyzer.data.ExperimentIO;

public class MeasurementResultsActivity extends AppCompatActivity {

	public static final String EXPERIMENT_RESULT_EXTRA = "com.audiopathanalyzer.EXPERIMENT_RESULT_EXTRA";

	public static final int EXPORT_MEASUREMENT_REQUEST_CODE = 11;

	public static final int EXPORT_CSV_BODE_REQUEST_CODE = 21;
	public static final int EXPORT_CSV_THD_REQUEST_CODE = 22;

	private TabHost tabHost;

	private ToggleButton results_general;
	private ToggleButton results_bode;
	private ToggleButton results_thd;

	@SuppressWarnings("deprecated") private LocalActivityManager mlam;

	private Experiment result;

	@Override @SuppressWarnings("deprecated") protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurement_results);

		tabHost = findViewById(R.id.results_tabs);
		results_general = findViewById(R.id.results_general);
		results_bode = findViewById(R.id.results_bode);
		results_thd = findViewById(R.id.results_thd);

		mlam = new LocalActivityManager(this, false);
		mlam.dispatchCreate(savedInstanceState);

		tabHost.setup(mlam);

		results_general.setChecked(true);

		TabHost.TabSpec general = tabHost.newTabSpec("General");
		TabHost.TabSpec bode = tabHost.newTabSpec("Bode");
		TabHost.TabSpec thd = tabHost.newTabSpec("THD");

		general.setIndicator("General");
		bode.setIndicator("Bode");
		thd.setIndicator("THD");

		result = (Experiment) getIntent().getSerializableExtra(EXPERIMENT_RESULT_EXTRA);

		Intent general_intent = new Intent(this, MeasurementResultsGeneralActivity.class);
		general_intent.putExtra(EXPERIMENT_RESULT_EXTRA, result);

		Intent bode_intent = new Intent(this, MeasurementResultsBodeActivity.class);
		bode_intent.putExtra(EXPERIMENT_RESULT_EXTRA, result);

		Intent thd_intent = new Intent(this, MeasurementResultsTHDActivity.class);
		thd_intent.putExtra(EXPERIMENT_RESULT_EXTRA, result);

		general.setContent(general_intent);
		bode.setContent(bode_intent);
		thd.setContent(thd_intent);

		tabHost.addTab(general);
		tabHost.addTab(bode);
		tabHost.addTab(thd);
	}

	public void tabHandler(View target) {
		results_general.setChecked(false);
		results_bode.setChecked(false);
		results_thd.setChecked(false);
		if (target.getId() == R.id.results_general) {
			tabHost.setCurrentTab(0);
			results_general.setChecked(true);
		} else if (target.getId() == R.id.results_bode) {
			tabHost.setCurrentTab(1);
			results_bode.setChecked(true);
		} else if (target.getId() == R.id.results_thd) {
			tabHost.setCurrentTab(2);
			results_thd.setChecked(true);
		}

	}

	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK)
			return;

		if (requestCode == EXPORT_MEASUREMENT_REQUEST_CODE) {
			if (data != null) {
				Uri uri = data.getData();
				if (uri != null) {
					ExperimentIO.exportExperiment(uri, result, this);
				}
			}
		} else if (requestCode == EXPORT_CSV_BODE_REQUEST_CODE) {
			if (data != null) {
				Uri uri = data.getData();
				if (uri != null) {
					ExperimentIO.exportBodeCSV(uri, result, this);
				}
			}
		} else if (requestCode == EXPORT_CSV_THD_REQUEST_CODE) {
			if (data != null) {
				Uri uri = data.getData();
				if (uri != null) {
					ExperimentIO.exportTHDCSV(uri, result, this);
				}
			}
		}
	}

	public void back(View view) {
		this.onBackPressed();
	}

	@Override protected void onResume() {
		super.onResume();
		mlam.dispatchResume();
	}

	@Override protected void onPause() {
		super.onPause();
		mlam.dispatchPause(isFinishing());
	}

	@Override public void onBackPressed() {
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
}
