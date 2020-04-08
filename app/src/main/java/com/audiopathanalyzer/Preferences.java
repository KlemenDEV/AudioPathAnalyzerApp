package com.audiopathanalyzer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

	private SharedPreferences preferences;

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

	public void writePreferences(int steps_count, int min_f, int max_f) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("steps_count", steps_count);
		editor.putInt("min_f", min_f);
		editor.putInt("max_f", max_f);
		editor.apply();
	}

}
