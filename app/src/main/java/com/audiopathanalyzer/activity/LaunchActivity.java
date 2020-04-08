package com.audiopathanalyzer.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.audiopathanalyzer.R;
import com.audiopathanalyzer.connection.SSHConnector;

public class LaunchActivity extends AppCompatActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
    }

    @Override protected void onResume() {
        super.onResume();

        // try to connect
        new SSHConnector(this).execute();
    }
}
