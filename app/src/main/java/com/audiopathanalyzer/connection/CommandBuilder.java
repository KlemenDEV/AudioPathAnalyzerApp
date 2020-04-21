package com.audiopathanalyzer.connection;

import androidx.annotation.Nullable;
import com.audiopathanalyzer.AudioPathAnalyzer;
import com.audiopathanalyzer.R;

public class CommandBuilder {

	public static String build() {
		return build(null);
	}

	public static String build(@Nullable String opts) {
		StringBuilder sb = new StringBuilder();
		sb.append("cd ").append(AudioPathAnalyzer.getApplication().getString(R.string.pi_sw_root));
		sb.append(" && ");

		sb.append("./AudioPathAnalyzer");
		sb.append(" -f calib.csv");
		sb.append(" --frequency_low ").append(AudioPathAnalyzer.getApplication().getPreferences().getMeasurementMinF());
		sb.append(" --frequency_high ").append(AudioPathAnalyzer.getApplication().getPreferences().getMeasurementMaxF());
		sb.append(" --steps ").append(AudioPathAnalyzer.getApplication().getPreferences().getMeasurementStepsCount());

		if (opts != null) {
			sb.append(" ");
			sb.append(opts);
		}

		return sb.toString();
	}

}
