package com.audiopathanalyzer.data;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;
import com.google.gson.GsonBuilder;

import java.io.*;

public class ExperimentIO {

	public static void exportExperiment(Uri uri, Experiment experiment, Context context) {
		try (BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(context.getContentResolver().openOutputStream(uri)))) {
			bw.write(new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(experiment));
			bw.flush();
		} catch (IOException e) {
			Toast.makeText(context, "File write failed: " + e.toString(), Toast.LENGTH_LONG).show();
		}
	}

	public static Experiment importExperiment(Uri uri, Context context) {
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(context.getContentResolver().openInputStream(uri)))) {
			StringBuilder jsonString = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				jsonString.append(line).append("\n");
			}
			Experiment retval = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
					.fromJson(jsonString.toString(), Experiment.class);
			retval.setLoaded(true);
			return retval;
		} catch (Exception e) {
			Toast.makeText(context, "File read failed: " + e.toString(), Toast.LENGTH_LONG).show();
		}

		return null;
	}

	public static void exportBodeCSV(Uri uri, Experiment experiment, Context context) {
		try (BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(context.getContentResolver().openOutputStream(uri)))) {
			StringBuilder csvbuilder = new StringBuilder();
			for (Measurement m : experiment.getTakes()) {
				csvbuilder.append(m.getF()).append(",").append(m.getA()).append("\n");
			}

			bw.write(csvbuilder.toString());
			bw.flush();
		} catch (IOException e) {
			Toast.makeText(context, "File write failed: " + e.toString(), Toast.LENGTH_LONG).show();
		}
	}

	public static void exportTHDCSV(Uri uri, Experiment experiment, Context context) {
		try (BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(context.getContentResolver().openOutputStream(uri)))) {
			StringBuilder csvbuilder = new StringBuilder();
			for (Measurement m : experiment.getTakes()) {
				csvbuilder.append(m.getF()).append(",").append(m.getThd_f()).append("\n");
			}

			bw.write(csvbuilder.toString());
			bw.flush();
		} catch (IOException e) {
			Toast.makeText(context, "File write failed: " + e.toString(), Toast.LENGTH_LONG).show();
		}
	}
}
