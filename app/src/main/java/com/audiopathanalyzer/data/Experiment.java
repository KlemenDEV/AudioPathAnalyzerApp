package com.audiopathanalyzer.data;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

public class Experiment implements Serializable {

	@Expose private final List<Measurement> takes;
	@Expose private final float dcOffset;
	@Expose private final int invalidCount;

	private boolean loaded = false;

	public Experiment(List<Measurement> takes, float dc_offset, int invalidCount) {
		this.takes = takes;
		this.dcOffset = dc_offset;
		this.invalidCount = invalidCount;
	}

	public List<Measurement> getTakes() {
		return takes;
	}

	public float getDCOffset() {
		return dcOffset;
	}

	public int getInvalidCount() {
		return invalidCount;
	}

	public float getTHD1khz() {
		float thd_l = 0, thd_h, f_l = 0, f_h;

		for (Measurement m : takes) {
			if (m.f == 1000) {
				return m.thd_r;
			} else if (m.f < 1000) {
				thd_l = m.thd_r;
				f_l = m.f;
			} else {
				thd_h = m.thd_r;
				f_h = m.f;

				float k = (thd_h - thd_l) / (f_h - f_l);
				float n = thd_l - k * f_l;

				return k * 1000 + n;
			}
		}

		return -1;
	}

	public float getMinDB() {
		float retval = Float.MAX_VALUE;
		for (Measurement m : takes) {
			if (m.a < retval)
				retval = m.a;
		}
		return retval;
	}

	public float getAvgDB() {
		float retval = 0;
		for (Measurement m : takes) {
			retval += m.a;
		}
		return retval / (float) takes.size();
	}

	public int getFancyScore() {
		float score = 100;

		score -= getTHD1khz() * 0.5;
		score -= Math.abs(getMinDB()) * 0.5;
		score -= Math.abs(getAvgDB()) * 3;
		score -= getInvalidCount();

		return Math.round(Math.max(score, 0));
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public boolean isLoaded() {
		return loaded;
	}
}
