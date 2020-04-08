package com.audiopathanalyzer.connection;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.audiopathanalyzer.AudioPathAnalyzer;
import com.audiopathanalyzer.StaticConfiguration;
import com.audiopathanalyzer.activity.MeasurementRunningActivity;
import com.audiopathanalyzer.activity.StartMeasurementActivity;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.lang.ref.WeakReference;
import java.util.Properties;

public class SSHConnector extends AsyncTask<Void, Void, Session> {

	private WeakReference<Activity> parent;

	public SSHConnector(@Nullable Activity parent) {
		this.parent = new WeakReference<>(parent);
	}

	@Override protected Session doInBackground(Void... ignored) {
		if (AudioPathAnalyzer.getApplication().getSessionDirect() != null && AudioPathAnalyzer.getApplication()
				.getSessionDirect().isConnected()) {
			return AudioPathAnalyzer.getApplication().getSessionDirect();
		}

		JSch jsch = new JSch();

		try {
			Session session = jsch.getSession(StaticConfiguration.PI_USERNAME, StaticConfiguration.PI_IP, 22);
			session.setPassword(StaticConfiguration.PI_PASSWORD);

			// skip host key check
			Properties prop = new Properties();
			prop.put("StrictHostKeyChecking", "no");
			session.setConfig(prop);

			session.connect();

			return session;
		} catch (JSchException e) {
			Log.e(SSHConnector.class.getName(), "Failed to connect to the PI", e);
		}

		return null;
	}

	@Override protected void onPostExecute(Session result) {
		if (result != null && parent.get() != null) {
			AudioPathAnalyzer.getApplication().setSession(result);

			Intent intent = new Intent(parent.get(), MeasurementRunningActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.putExtra(MeasurementRunningActivity.CALIBRATION_MEASUREMENT_EXTRA, true);
			parent.get().startActivity(intent);
			parent.get().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			parent.get().finish();
		} else if (parent.get() != null) { // failed to connect
			Intent intent = new Intent(parent.get(), StartMeasurementActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			parent.get().startActivity(intent);
			parent.get().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			parent.get().finish();

			Toast.makeText(parent.get(), "Failed to connect. Check your connection.", Toast.LENGTH_LONG).show();
		}
	}
}
