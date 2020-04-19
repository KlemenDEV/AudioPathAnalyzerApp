package com.audiopathanalyzer.data;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class Measurement implements Serializable {

	@Expose final float f ;
	@Expose final float a;
	@Expose final float thd_f;
	@Expose final float thd_r;
	@Expose final float thd_n;
	@Expose final boolean valid;

	public Measurement(float f, float a, float thd_r, boolean valid) {
		this.f = f;
		this.a = a;
		this.valid = valid;

		this.thd_f = 0;
		this.thd_r = thd_r * 100f;
		this.thd_n = 0;
	}

	public Measurement(float f, float a, float thd_f, float thd_r, float thd_n, boolean valid) {
		this.f = f;
		this.a = a;
		this.thd_f = thd_f * 100f;
		this.thd_r = thd_r * 100f;
		this.thd_n = thd_n * 100f;
		this.valid = valid;
	}

	public float getF() {
		return f;
	}

	public float getA() {
		return a;
	}

	public float getThd_f() {
		return thd_f;
	}

	public float getThd_r() {
		return thd_r;
	}

	public float getThd_n() {
		return thd_n;
	}

	public boolean isValid() {
		return valid;
	}

}
