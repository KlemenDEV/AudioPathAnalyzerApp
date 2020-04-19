package com.audiopathanalyzer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.audiopathanalyzer.R;
import com.audiopathanalyzer.data.Experiment;
import com.audiopathanalyzer.data.Measurement;
import com.audiopathanalyzer.util.LogLineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MeasurementResultsTHDActivity extends AppCompatActivity {

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurement_results_thd);

		Experiment result = (Experiment) getIntent()
				.getSerializableExtra(MeasurementResultsActivity.EXPERIMENT_RESULT_EXTRA);

		if (result != null) {
			List<Entry> entryList = new ArrayList<>();
			List<Integer> entryListColors = new ArrayList<>();
			for (Measurement m : result.getTakes()) {
				entryList.add(new Entry((float) Math.log10(m.getF()), m.getThd_r()));
				entryListColors.add(m.isValid() ? R.color.colorPrimary : R.color.error);
			}

			LogLineChart results_bode_graph = findViewById(R.id.results_thd_graph);
			results_bode_graph.setEntryListAndFormat(entryList, "%.4f %% at %d Hz");

			LineDataSet dataset = new LineDataSet(entryList, "thd");
			dataset.setColors(entryListColors);
			dataset.setDrawCircles(false);
			dataset.setDrawCircleHole(false);
			dataset.setDrawValues(false);
			dataset.setDrawHighlightIndicators(false);

			results_bode_graph.setData(new LineData(dataset));

			results_bode_graph.getData().notifyDataChanged();
			results_bode_graph.notifyDataSetChanged();
			results_bode_graph.invalidate();
		}
	}

	public void exportToCSV(View view) {
		String date = new SimpleDateFormat("yyyy-MM-dd--HH-mm-ss", Locale.getDefault()).format(new Date());

		Intent exportIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
		exportIntent.addCategory(Intent.CATEGORY_OPENABLE);
		exportIntent.setType("text/csv");
		exportIntent.putExtra(Intent.EXTRA_TITLE, "THD_" + date);

		this.getParent().startActivityForResult(exportIntent, MeasurementResultsActivity.EXPORT_CSV_THD_REQUEST_CODE);
		this.getParent().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	@Override public void onBackPressed() {
		this.getParent().onBackPressed();
	}
}
