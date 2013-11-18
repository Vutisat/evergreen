package org.zdev.recall;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.text.InputType;

public class SettingsActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_general);

		// retrieve the delete limit preference and make sure it is set to numerical values only
		EditTextPreference preferenceToEdit = (EditTextPreference) findPreference("pref_key_auto_delete_limit");
		preferenceToEdit.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
	}

}
