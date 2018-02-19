package com.example.shagufta.medicalert_v3;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Shagufta on 11/02/2018.
 */

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content, new Settings()).commit();
    }
}
