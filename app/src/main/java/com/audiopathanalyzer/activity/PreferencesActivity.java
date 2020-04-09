package com.audiopathanalyzer.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.audiopathanalyzer.AudioPathAnalyzer;
import com.audiopathanalyzer.R;

public class PreferencesActivity extends AppCompatActivity {

	private EditText preferences_start_f;
	private EditText preferences_end_f;
	private EditText preferences_steps;

	@SuppressLint("SetTextI18n") @Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);

		preferences_start_f = findViewById(R.id.preferences_start_f);
		preferences_end_f = findViewById(R.id.preferences_end_f);
		preferences_steps = findViewById(R.id.preferences_steps);

		preferences_start_f
				.setText(Integer.toString(AudioPathAnalyzer.getApplication().getPreferences().getMeasurementMinF()));
		preferences_end_f
				.setText(Integer.toString(AudioPathAnalyzer.getApplication().getPreferences().getMeasurementMaxF()));
		preferences_steps.setText(
				Integer.toString(AudioPathAnalyzer.getApplication().getPreferences().getMeasurementStepsCount()));

		if (AudioPathAnalyzer.getApplication().getSessionDirect() == null || !AudioPathAnalyzer.getApplication()
				.getSessionDirect().isConnected()) {
			findViewById(R.id.preferences_recalibrate).setEnabled(false);
		}
	}

	public void recalibrate(View view) {
		if (storePreferences())
			runCalibration(true);
	}

	public void saveChanges(View view) {
		if (storePreferences())
			runCalibration(false);
	}

	private boolean storePreferences() {
		try {
			int min_f = Integer.parseInt(preferences_start_f.getText().toString());
			int max_f = Integer.parseInt(preferences_end_f.getText().toString());
			int steps = Integer.parseInt(preferences_steps.getText().toString());

			if (min_f < 1 || max_f < 1 || steps < 1 || min_f >= max_f) {
				throw new Exception("Invalid parameters");
			}

			AudioPathAnalyzer.getApplication().getPreferences().writePreferences(steps, min_f, max_f);

			return true;
		} catch (Exception e) {
			Toast.makeText(this, "Invalid parameters detected", Toast.LENGTH_SHORT).show();
		}

		return false;
	}

	private void runCalibration(boolean forced) {
		if (AudioPathAnalyzer.getApplication().getSessionDirect() == null || !AudioPathAnalyzer.getApplication()
				.getSessionDirect().isConnected()) {
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			finish();
			return; // skip calibration if not connected
		}

		Intent intent = new Intent(this, MeasurementRunningActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.putExtra(MeasurementRunningActivity.CALIBRATION_MEASUREMENT_EXTRA, true);
		intent.putExtra(MeasurementRunningActivity.CALIBRATION_FORCED_EXTRA, forced);
		startActivity(intent);

		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		finish();
	}

	public void cancel(View view) {
		this.onBackPressed();
	}

	@Override public void onBackPressed() {
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
}
