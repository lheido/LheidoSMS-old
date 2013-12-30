package com.lheido.sms;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class LheidoSmsPreferenceActivity extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preference);
	}
}
