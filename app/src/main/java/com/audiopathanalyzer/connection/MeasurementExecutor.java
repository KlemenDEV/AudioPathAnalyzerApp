package com.audiopathanalyzer.connection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.audiopathanalyzer.AudioPathAnalyzer;
import com.audiopathanalyzer.R;
import com.audiopathanalyzer.activity.MeasurementRunningActivity;
import com.audiopathanalyzer.activity.StartMeasurementActivity;
import com.audiopathanalyzer.activity.MeasurementResultsActivity;
import com.audiopathanalyzer.data.Experiment;
import com.audiopathanalyzer.data.ExperimentParser;
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

public class MeasurementExecutor extends AsyncTask<Void, MeasurementProgress, Experiment> {

	private ChannelExec channel;

	private final WeakReference<Activity> parent;

	private boolean canceled = false;

	public MeasurementExecutor(@Nullable Activity parent) {
		this.parent = new WeakReference<>(parent);
	}

	@Override protected Experiment doInBackground(Void... ignored) {
		Session session = AudioPathAnalyzer.getApplication().getSession();
		if (session != null) {
			try {
				channel = (ChannelExec) session.openChannel("exec");
				channel.setCommand(CommandBuilder.build());

				channel.setInputStream(null);
				channel.setErrStream(System.err);

				ExperimentParser experimentParser = new ExperimentParser();
				AtomicBoolean parseData = new AtomicBoolean(false);

				channel.setOutputStream(new OutputStreamEventHandler(line -> {
					if (line.startsWith("MEAS:")) {
						float p = Float.parseFloat(StringUtils.substringAfterLast(line, "p="));
						float f = Float.parseFloat(StringUtils.substringBetween(line, "f=", ", a="));
						float a = Float.parseFloat(StringUtils.substringBetween(line, "a=", ", p="));
						publishProgress(new MeasurementProgress(p, f, a));
					} else if (line.startsWith("#CSV")) {
						parseData.set(true);
					} else if (parseData.get()) {
						experimentParser.addData(line);
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
					channel = null;
				}

				return experimentParser.parse();
			} catch (JSchException e) {
				Log.e(MeasurementExecutor.class.getName(), "SSH error", e);
			}
		} else {
			Log.e(MeasurementExecutor.class.getName(), "Failed to get SSH session");
		}

		return null;
	}

	@Override protected void onProgressUpdate(MeasurementProgress... values) {
		super.onProgressUpdate(values);

		if (values.length > 0 && parent.get() != null) {
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

	@Override protected void onPostExecute(Experiment result) {
		if (parent.get() != null && !canceled) {
			if (result != null) {
				((Vibrator) parent.get().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);

				Intent intent = new Intent(parent.get(), MeasurementResultsActivity.class);
				intent.putExtra(MeasurementResultsActivity.EXPERIMENT_RESULT_EXTRA, result);
				parent.get().startActivity(intent);
				parent.get().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			} else {
				Intent intent = new Intent(parent.get(), StartMeasurementActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				parent.get().startActivity(intent);
				parent.get().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				Toast.makeText(parent.get(), "Failed to complete the measurement", Toast.LENGTH_LONG).show();
			}
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
					Log.e(MeasurementExecutor.class.getName(), "Failed to cancel measurement", e);
				} finally {
					channel.disconnect();
					channel = null;
				}
			}).start();
		}
	}

}
