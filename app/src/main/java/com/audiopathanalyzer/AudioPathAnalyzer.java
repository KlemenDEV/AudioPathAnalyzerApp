package com.audiopathanalyzer;

import android.app.Application;
import android.content.Intent;
import androidx.annotation.Nullable;
import com.audiopathanalyzer.activity.LaunchActivity;
import com.jcraft.jsch.Session;

public class AudioPathAnalyzer extends Application {

	private static AudioPathAnalyzer instance;

	private Session session;

	private Preferences preferences;

	public AudioPathAnalyzer() {
		instance = this;
	}

	@Override public void onCreate() {
		super.onCreate();
		this.preferences = new Preferences(getApplicationContext());
	}

	public void setSession(Session session) {
		this.session = session;
	}

	@Nullable public Session getSession() {
		if (session == null || !session.isConnected()) {
			Intent intent = new Intent(this, LaunchActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent); // try to reconnect
			return null;
		}
		return session;
	}

	@Nullable public Session getSessionDirect() {
		return session;
	}

	public static AudioPathAnalyzer getApplication() {
		return instance;
	}

	public Preferences getPreferences() {
		return preferences;
	}
}
