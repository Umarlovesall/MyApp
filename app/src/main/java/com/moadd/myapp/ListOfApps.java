package com.moadd.myapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.moadd.myapp.R;

public class ListOfApps extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_apps);
        getSupportActionBar().hide();
    }
}
