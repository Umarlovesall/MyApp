package com.moadd.myapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moadd.myapp.R;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import static com.moadd.myapp.WebViews.URLs;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    public static int fingAuth = 0,failAuth = 0;
    EditText password,username;
    TextView finger;
    Button login;
    ImageView fingerprint;
    public static String un, pw;
    SharedPreferences sp;
    boolean statusOfGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*sp = getSharedPreferences("Credentials", 1);
        String uname = sp.getString("username", null);
        String passw = sp.getString("password", null);*/
        //HERE,IF login status(which is saved in shared preferences) is positive,we directly open the account,not login page.
        /*if (uname !=null && passw != null)
        {         DIRECT LOGIN CODE HERE
            response = "http://192.168.0.105:8080/android/login.htm?userId="+uname+"&password="+passw;
            Intent in = new Intent(MainActivity.this,WebViews.class);
            startActivity(in);
        }*/
        sp = getSharedPreferences("Credentials", MODE_PRIVATE);
        if (sp.getInt("LoginStatus",5) == 1 &&  sp.getString("username",null)!= null &&  sp.getString("password",null)!= null)
        {
            un = sp.getString("username", null);
            pw = sp.getString("password", null);
            URLs = "https://www.moaddi.com/login.htm?userId=" + un + "&password=" + pw;
            //URLs="http://192.168.0.113:8080/moaddi/login.htm?userId=" + un + "&password=" + pw;
            Bundle b = new Bundle();
            b.putString("URL", URLs);
            Intent in = new Intent(MainActivity.this, WebViews.class);
            in.putExtras(b);
            startActivity(in);
            finish();
            VolleyClass.nextpage(MainActivity.this);
        }

        //CAMERA PERMISSION NECESSARY FOR BARCODE
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
       /* LocationManager manager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!statusOfGPS) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to switch On the GPS?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }*/
        getSupportActionBar().hide();
        username = (EditText) findViewById(R.id.userId);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.email_sign_in_button);
       // register = (TextView) findViewById(R.id.Register);
        finger = (TextView) findViewById(R.id.finger);
      //  wifi = (ImageView) findViewById(R.id.wifi);
        fingerprint = (ImageView) findViewById(R.id.fingerprint);
        FingerPrintAuth.Finger(MainActivity.this);
      /*  wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(MainActivity.WIFI_SERVICE);
                boolean wifiEnabled = wifiManager.isWifiEnabled();
                if (wifiEnabled) {
                    wifiManager.setWifiEnabled(false);
                    wifi.setImageResource(R.drawable.wifioff);
                    Toast.makeText(getApplicationContext(), "Wi fi (Off)", Toast.LENGTH_SHORT).show();
                } else if (!wifiEnabled) {
                    wifiManager.setWifiEnabled(true);
                    wifi.setImageResource(R.drawable.wifion);
                    Toast.makeText(getApplicationContext(), "Wi fi (On)", Toast.LENGTH_SHORT).show();

                }
            }
        });*/
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                            //HERE EDITING NEEDED
                un = username.getText().toString().trim();
                pw = password.getText().toString().trim();
                if (un == null || un.equals("") || pw.equals("") || pw == null)
                {
                    Toast.makeText(getApplicationContext(), "Enter a Valid Combination of Username & Password", Toast.LENGTH_SHORT).show();
                }
                else {
                    //URLs="http://192.168.0.113:8080/moaddi/login.htm?userId=" + un + "&password=" + pw;
                    URLs = "https://www.moaddi.com/login.htm?userId=" + un + "&password=" + pw;
                    Bundle b = new Bundle();
                    b.putString("URL", URLs);
                    SharedPreferences.Editor et = sp.edit();
                    et.putString("username", un);
                    et.putString("password", pw);
                    et.putInt("LoginStatus", 1);
                    et.commit();
                    Intent in = new Intent(MainActivity.this, WebViews.class);
                    in.putExtras(b);
                    if (sp.getString("usernameForFingerprint",null)== null &&
                            sp.getString("passwordForFingerprint",null)== null &&
                                    //checking whether device has fingerprint features and is all set to use it,before throwing
                                    // in the dialog for fingerprint adding authentication
                            AvailabilityofHardwareAndRegisteredFingerprints.fingerprintCompatibility(MainActivity.this))  {
                        openDialog(sp, in);
                    }
                    else
                    {
                        startActivity(in);
                    }
                    //Connection Request made here
                    VolleyClass.nextpage(MainActivity.this);
                }
            }
    });
/*register.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent in = new Intent(MainActivity.this,RegistrationPage.class);
        startActivity(in);
        finish();
    }
});*/
       /* fingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AvailabilityofHardwareAndRegisteredFingerprints.fingerprintCompatibility(MainActivity.this))
                {
                FingerPrintAuth.Finger(MainActivity.this);
                if (fingAuth == 1)
                {
                    //LOGIN
                    Bundle b = new Bundle();
                    String s = sp.getString("usernameForFingerprint",null);
                    String t = sp.getString("passwordForFingerprint",null);
                    if (s == null || t == null)
                    {
                        Toast.makeText(getApplicationContext(), "Use Registered Username and Password for the first time to link it with the fingerprints registered on this device", Toast.LENGTH_LONG).show();

                    }
                    else {
                        b.putString("URL", "https://www.moaddi.com/login.htm?userId=" + s + "&password=" + t);
                        SharedPreferences.Editor et = sp.edit();
                        et.putInt("LoginStatus", 1);
                        et.commit();
                        Intent in = new Intent(MainActivity.this, WebViews.class);
                        in.putExtras(b);
                        startActivity(in);
                        finish();
                        VolleyClass.nextpage(MainActivity.this);
                        Toast.makeText(getApplicationContext(), "Logging In", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (failAuth == 1)
                {
                    Toast.makeText(getApplicationContext(), "Fingerprints don't match", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Keep your finger on the Fingerprint Sensor", Toast.LENGTH_SHORT).show();
                }
            }
            else
                {
                    Toast.makeText(getApplicationContext(), "This service is available only with the Fingerprint compatible devices.", Toast.LENGTH_LONG).show();
                }
            }
        });*/

}

    @Override
    protected void onResume() {
        password.setText("");
        username.setText("");
        FingerPrintAuth.Finger(MainActivity.this);
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    public void openDialog(final SharedPreferences sp,final Intent in){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Do you want to link your account with the fingerprint Authentication ? You can Login using any fingerprint registered on this device");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Toast.makeText(MainActivity.this,"Your account is linked with all the fingerprints registered on this device.",Toast.LENGTH_LONG).show();
                                FingerPrintNeededorNot.needed(sp);
                                startActivity(in);
                                finish();
                            }
                        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(in);
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    //Was trying to login as sson as the user fingerprint is authenticated without him having to click the fingerprint icon
    /*public class AuthenticationSuccessfull extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            //LOGIN
            Bundle b = new Bundle();
            String s = sp.getString("usernameForFingerprint",null);
            String t = sp.getString("passwordForFingerprint",null);
            if (s == null || t == null)
            {
                Toast.makeText(getApplicationContext(), "Use Registered Username and Password for the first time to link it with the fingerprints registered on this device", Toast.LENGTH_LONG).show();
            }
            else {
                b.putString("URL", "https://www.moaddi.com/login.htm?userId=" + s + "&password=" + t);
                SharedPreferences.Editor et = sp.edit();
                et.putInt("LoginStatus", 1);
                et.commit();
                Intent in = new Intent(MainActivity.this, WebViews.class);
                in.putExtras(b);
                startActivity(in);
                finish();
                VolleyClass.nextpage(MainActivity.this);
                Toast.makeText(getApplicationContext(), "Logging In", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void openLoginAfterFingerprintSuccessful()
    {
     Intent in = new Intent(this,AuthenticationSuccessfull.class) ;
        sendBroadcast(in);
    }*/

   /* private class HttpRequestTask extends AsyncTask<Void, Void, LoginForm> {
        @Override
        protected LoginForm doInBackground(Void... params) {
            try {
                //The link on which we have to POST data and in return it will return some data
                String URL = "http://192.168.0.114:8083/Client/countries123.htm";
                //Create and set object 'l' of bean class LoginForm,which we will POST then
                LoginForm l = new LoginForm();
                l.setUsername("Umar");
                l.setPassword("Farooq007");
                //Use RestTemplate to POST(within Asynctask)
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                //postforobject method POSTs data to server and brings back LoginForm object format data.
                LoginForm lf = restTemplate.postForObject(URL, l, LoginForm.class);
                return lf;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(LoginForm lf) {
            //The returned object of LoginForm that we recieve from postforobject in doInBackground is displayed here.
            //tv.setText(lf.getUsername()+lf.getPassword());

        }

    }*/
}




