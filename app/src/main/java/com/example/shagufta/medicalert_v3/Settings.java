package com.example.shagufta.medicalert_v3;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Shagufta on 11/02/2018.
 */

public class Settings extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}
