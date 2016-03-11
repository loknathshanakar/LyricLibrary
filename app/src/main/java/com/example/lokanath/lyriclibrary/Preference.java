package com.example.lokanath.lyriclibrary;

import android.app.Activity;
import android.os.Bundle;

public class Preference extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
