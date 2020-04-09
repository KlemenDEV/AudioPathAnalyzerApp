package com.audiopathanalyzer.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.audiopathanalyzer.AudioPathAnalyzer;
import com.audiopathanalyzer.R;
import com.audiopathanalyzer.data.Experiment;
import com.audiopathanalyzer.data.ExperimentIO;

public class StartMeasurementActivity extends AppCompatActivity {

	private static final int FILE_OPEN_REQUEST_CODE = 21;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_measurement);
	}

	public void startMeasurement(View view) {
		if (AudioPathAnalyzer.getApplication().getSessionDirect() == null || !AudioPathAnalyzer.getApplication()
				.getSessionDirect().isConnected()) {
			((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(500);

			Toast.makeText(this, "Measurement system is not connected", Toast.LENGTH_LONG).show();
		} else {
			((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);

			startActivity(new Intent(this, MeasurementRunningActivity.class));
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		}
	}

	public void openPreferences(View view) {
		startActivity(new Intent(this, PreferencesActivity.class));
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	public void openMeasurement(View view) {
		Intent exportIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		exportIntent.addCategory(Intent.CATEGORY_OPENABLE);
		exportIntent.setType("application/json");
		startActivityForResult(exportIntent, FILE_OPEN_REQUEST_CODE);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK)
			return;

		if (requestCode == FILE_OPEN_REQUEST_CODE) {
			if (data != null) {
				Uri uri = data.getData();
				if (uri != null) {
					Experiment experiment = ExperimentIO.importExperiment(uri, this);
					if (experiment != null) {
						Intent intent = new Intent(this, MeasurementResultsActivity.class);
						intent.putExtra(MeasurementResultsActivity.EXPERIMENT_RESULT_EXTRA, experiment);
						startActivity(intent);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					}
				}
			}
		}
	}

	@Override public void onBackPressed() {
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
}
