package com.audiopathanalyzer.connection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.audiopathanalyzer.AudioPathAnalyzer;
import com.audiopathanalyzer.R;
import com.audiopathanalyzer.activity.StartMeasurementActivity;
import com.audiopathanalyzer.data.Experiment;
import com.audiopathanalyzer.data.MeasurementProgress;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.lang3.StringUtils;

import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class CalibrationExecutor extends AsyncTask<Void, MeasurementProgress, Experiment> {

	private ChannelExec channel;

	private WeakReference<Activity> parent;

	private boolean canceled = false;

	private boolean forced = false;

	private boolean firstUpdate = true;

	public CalibrationExecutor(@Nullable Activity parent) {
		this.parent = new WeakReference<>(parent);
	}

	public AsyncTask<Void, MeasurementProgress, Experiment> setForced(boolean forced) {
		this.forced = forced;
		return this;
	}

	@Override protected Experiment doInBackground(Void... ignored) {
		Session session = AudioPathAnalyzer.getApplication().getSession();
		if (session != null) {
			try {
				AtomicBoolean calibrationNeeded = new AtomicBoolean(true);

				// check if calibration is even needed
				channel = (ChannelExec) session.openChannel("exec");
				channel.setCommand(CommandBuilder.build("--verifycalibration"));
				channel.setInputStream(null);
				channel.setErrStream(System.err);
				channel.setOutputStream(new OutputStreamEventHandler(line -> {
					if (line.startsWith("VERIFY: valid")) {
						calibrationNeeded.set(false);
					}
				}));
				channel.connect();
				while (channel != null && !channel.isClosed()) {
					try {
						Thread.sleep(500);
					} catch (Exception ignored1) {
					}
				}
				if (channel != null) {
					channel.disconnect();
				}

				// if calibration is not needed, we skip it, unless calibration was forced
				if(!calibrationNeeded.get() && !forced) {
					return null;
				}

				// we do the calibration if needed
				channel = (ChannelExec) session.openChannel("exec");
				channel.setCommand(CommandBuilder.build("--docalibration"));
				channel.setInputStream(null);
				channel.setErrStream(System.err);
				channel.setOutputStream(new OutputStreamEventHandler(line -> {
					if (line.startsWith("MEAS:")) {
						float p = Float.parseFloat(StringUtils.substringAfterLast(line, "p="));
						float f = Float.parseFloat(StringUtils.substringBetween(line, "f=", ", a="));
						float a = Float.parseFloat(StringUtils.substringBetween(line, "a=", ", p="));
						publishProgress(new MeasurementProgress(p, f, a));
					}
				}));
				channel.connect();
				while (channel != null && !channel.isClosed()) {
					try {
						Thread.sleep(500);
					} catch (Exception ignored1) {
					}
				}
				if (channel != null) {
					channel.disconnect();
					int retval = channel.getExitStatus();
					if (retval < 0) {
						Toast.makeText(parent.get(), "Failed to calibrate.", Toast.LENGTH_LONG).show();
						parent.get().finishAffinity();
					}
					channel = null;
				}
			} catch (JSchException e) {
				Log.e(CalibrationExecutor.class.getName(), "SSH error", e);
			}
		} else {
			Log.e(CalibrationExecutor.class.getName(), "Failed to get SSH session");
		}

		return null;
	}

	@Override protected void onProgressUpdate(MeasurementProgress... values) {
		super.onProgressUpdate(values);

		if (values.length > 0 && parent.get() != null) {
			if (firstUpdate) {
				parent.get().findViewById(R.id.measurement_progress_spinning).setVisibility(View.INVISIBLE);
				firstUpdate = false;
			}

			MeasurementProgress measurementProgress = values[0];

			ProgressBar measurement_progress = parent.get().findViewById(R.id.measurement_progress);
			measurement_progress.setProgress((int) (measurementProgress.getPercents() * 100));

			TextView measurement_frequency = parent.get().findViewById(R.id.measurement_frequency);
			measurement_frequency
					.setText(String.format(new Locale("pl", "PL"), "%,d Hz", (int) measurementProgress.getFrequency()));

			LineChart measurement_preview = parent.get().findViewById(R.id.measurement_preview);
			measurement_preview.getData().getDataSetByIndex(0).addEntry(
					new Entry((float) Math.log10(measurementProgress.getFrequency()),
							(float) Math.log10(measurementProgress.getAmplitude())));
			measurement_preview.getData().notifyDataChanged();
			measurement_preview.notifyDataSetChanged();
			measurement_preview.invalidate();
		}
	}

	@Override protected void onPostExecute(Experiment ignored) {
		if (parent.get() != null && !canceled) {
			Intent intent = new Intent(parent.get(), StartMeasurementActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			parent.get().startActivity(intent);
			parent.get().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			parent.get().finish();
		}
	}

	@Override protected void onCancelled() {
		super.onCancelled();

		if (channel != null) {
			canceled = true;
			new Thread(() -> {
				try {
					channel.sendSignal("2"); // CTRL + C - interrupt
					channel.sendSignal("9"); // KILL
					Thread.sleep(500);
				} catch (Exception e) {
					Log.e(CalibrationExecutor.class.getName(), "Failed to cancel calibration", e);
				} finally {
					channel.disconnect();
					channel = null;
				}
			}).start();
		}
	}

}
