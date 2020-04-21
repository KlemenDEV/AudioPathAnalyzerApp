package com.audiopathanalyzer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

	private final SharedPreferences preferences;

	public Preferences(Context context) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public int getMeasurementStepsCount() {
		return preferences.getInt("steps_count", 200);
	}

	public int getMeasurementMinF() {
		return preferences.getInt("min_f", 20);
	}

	public int getMeasurementMaxF() {
		return preferences.getInt("max_f", 20000);
	}

	public boolean isSmoothingEnabled() {
		return preferences.getBoolean("enable_smoothing", true);
	}

	public void writePreferences(int steps_count, int min_f, int max_f, boolean enable_smoothing) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("steps_count", steps_count);
		editor.putInt("min_f", min_f);
		editor.putInt("max_f", max_f);
		editor.putBoolean("enable_smoothing", enable_smoothing);
		editor.apply();
	}

}
