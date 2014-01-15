package com.lheido.sms;


import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class LheidoSmsPreferenceActivity extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preference);
		
		findPreference("hold_message_num").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                return Integer.parseInt((String)newValue) >= 10;
            }

        });
	}
	
}
