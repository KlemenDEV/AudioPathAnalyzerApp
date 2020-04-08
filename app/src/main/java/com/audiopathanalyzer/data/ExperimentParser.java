package com.audiopathanalyzer.data;

import androidx.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ExperimentParser {

	private List<String> lines = new ArrayList<>();

	public void addData(String line) {
		if (StringUtils.countMatches(line, ',') == 3)
			lines.add(line);
	}

	@Nullable public Experiment parse() {
		try {
			String initLine = lines.get(0);

			String[] initData = initLine.split(",");

			int invalidCount = Integer.parseInt(initData[1]);
			float dcOffset = Float.parseFloat(initData[2]);

			lines.remove(0);

			List<Measurement> measurements = new ArrayList<>();
			for (String line : lines) {
				String[] data = line.split(",");
				measurements.add(new Measurement(Float.parseFloat(data[0]), Float.parseFloat(data[1]),
						Float.parseFloat(data[2]), data[3].equals("1")));
			}

			return new Experiment(measurements, dcOffset, invalidCount);
		} catch (Exception e) {
			return null;
		}
	}
}
