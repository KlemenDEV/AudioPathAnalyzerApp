package com.audiopathanalyzer.data;

public class MeasurementProgress {

	private float percents;
	private float frequency;
	private float amplitude;

	public MeasurementProgress(float percents, float frequency, float amplitude) {
		this.percents = percents;
		this.frequency = frequency;
		this.amplitude = amplitude;
	}

	public float getPercents() {
		return percents;
	}

	public float getFrequency() {
		return frequency;
	}

	public float getAmplitude() {
		return amplitude;
	}
}
