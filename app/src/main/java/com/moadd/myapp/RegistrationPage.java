package com.moadd.myapp;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.moadd.myapp.R;

public class RegistrationPage extends AppCompatActivity {
    WebView wv;
    ImageView wifi;
    Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);
        getSupportActionBar().hide();
        wv = (WebView) findViewById(R.id.web);
        wifi = (ImageView) findViewById(R.id.wifi);
        login = (Button) findViewById(R.id.login);
        wv.setWebViewClient(new WebViewClient());
        wv.loadUrl("http://moaddi.com/showreg.htm");
        wv.getSettings().setJavaScriptEnabled(true);// VULNERABILITY
        wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(getApplicationContext().WIFI_SERVICE);
                boolean wifiEnabled = wifiManager.isWifiEnabled();
                if (wifiEnabled)
                {
                    wifiManager.setWifiEnabled(false);
                    wifi.setImageResource(R.drawable.wifioff);
                    Toast.makeText(getApplicationContext(),"Wi fi (Off)", Toast.LENGTH_SHORT).show();
                }
                else if (!wifiEnabled)
                {
                    wifiManager.setWifiEnabled(true);
                    wifi.setImageResource(R.drawable.wifion);
                    Toast.makeText(getApplicationContext(),"Wi fi (On)", Toast.LENGTH_SHORT).show();
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(RegistrationPage.this,MainActivity.class);
                startActivity(in);
                finish();
            }
        });
    }
}
