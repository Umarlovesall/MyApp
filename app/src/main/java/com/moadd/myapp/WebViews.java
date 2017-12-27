package com.moadd.myapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.moadd.myapp.R;
import com.google.zxing.client.android.CaptureActivity;

import java.util.Calendar;


public class WebViews extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;
    int j;
    ImageView  home,online,fingerprint;
    String currentUrl;
    WebView mywebview;
    Button apps,logout;
    SharedPreferences sp;
    String contents;
    boolean statusOfGPS;
    DatePickerDialog datePickerDialog;
   // String visitedURLs;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION= 0;
    //static String URLs="http://192.168.0.108:8082/Moaddi123/login.htm";
    static String URLs ="https://www.moaddi.com/login.htm";
    //static  String URLs="http://192.168.0.113:8080/moaddi/login.htm";
    public static int fingAuth = 0,failAuth = 0;
    private NetworkStateReceiver networkStateReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        online = (ImageView) findViewById(R.id.online);
        home = (ImageView) findViewById(R.id.home);
        mywebview = (WebView) findViewById(R.id.web);
       // barcode = (ImageView) findViewById(R.id.bar);
        fingerprint= (ImageView) findViewById(R.id.finger);
        apps = (Button) findViewById(R.id.apps);
        logout = (Button) findViewById(R.id.logout);
        sp = getSharedPreferences("Credentials", MODE_PRIVATE);
        FingerPrintAuth.Finger(WebViews.this);
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
        //GPS usage permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        //GPS switch ON/OFF suggestion
        LocationManager manager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
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
        }
        mywebview.getSettings().setJavaScriptEnabled(true);
        mywebview.getSettings().setAllowFileAccess(true);
        mywebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mywebview.setWebChromeClient(new WebChromeClient()
                                     {
                                         @Override
                                         public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                                             callback.invoke(origin, true, false);
                                         }
                                     }
       /* {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        } */);
        //mywebview.loadUrl("http://192.168.0.116:8081/Moaddi123/customer/buy.htm");
        //SOLUTION https://stackoverflow.com/questions/4920770/androidhow-to-add-support-the-javascript-alert-box-in-webviewclient
        mywebview.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageFinished(WebView view, String url) {
                currentUrl = url;
                super.onPageFinished(view, url);
                //visitedURLs = visitedURLs+currentUrl+"@@@";
                /*if (currentUrl.contains("/customer/nearest"))
                {
                    mywebview.loadUrl("javascript:address()");
                }*/
               if (currentUrl.charAt(currentUrl.length()-1)=='#') {
                   Intent intent = new Intent(getApplicationContext(), CaptureActivity.class);
                   intent.setAction("com.google.zxing.client.android.SCAN");
                   intent.putExtra("SAVE_HISTORY", false);
                   startActivityForResult(intent, 0);
               }
               if (currentUrl.contains("nearest.htm"))
               {
                   //mywebview.loadUrl("javascript:success('')");
               }
               if (currentUrl.contains("#datePicker"))
               {
                   // calender class's instance and get current date , month and year from calender
                   final Calendar c = Calendar.getInstance();
                   int mYear = c.get(Calendar.YEAR); // current year
                   int mMonth = c.get(Calendar.MONTH); // current month
                   int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                   // date picker dialog
                   datePickerDialog = new DatePickerDialog(WebViews.this,
                           new DatePickerDialog.OnDateSetListener() {

                               @Override
                               public void onDateSet(DatePicker view, int year,
                                                     int monthOfYear, int dayOfMonth) {
                                   // set day of month , month and year value in the edit text

                                   mywebview.loadUrl("javascript:datePicker('"+dayOfMonth + "-"
                                           + (monthOfYear + 1) + "-" + year+"')");
                               }
                           }, mYear, mMonth, mDay);
                   datePickerDialog.show();
               }
            }
           /* @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                clickWithinWebviewUrl= url;
                return super.shouldOverrideUrlLoading(view, url);
            }*/
        }
        );

        //mywebview.loadUrl(bundle.getString("URL"));//RESPONSE link to be pasted here
            mywebview.loadUrl(URLs);

        //VULNERABLE but for button clicks inside webviews,it was needed.
        //For fitting website to screen :
        // fit the width of screen
        mywebview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // remove a weird white line on the right size
        mywebview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        //To handle barcode clicks on webpage via app
        //Check if GPS is OFF then give dialog to switch it ON.
    /*   if (!statusOfGPS) {
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
        }
*/
        this.getSupportActionBar().hide();
       /* wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(getApplicationContext().WIFI_SERVICE);
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
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent in = new Intent(WebViews.this, MainActivity.class);
                startActivity(in);
                finish();*/
               mywebview.loadUrl("https://www.moaddi.com/login.htm");

            }
        });
        apps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(WebViews.this, ListOfApps.class);
                startActivity(in);
            }
        });
        //Barcode xml :
            /* <ImageView
        android:id="@+id/bar"
        android:layout_width="0dp"
        android:layout_height="match_parent"
        android:layout_weight="1"
        android:layout_marginTop="16dp"
        android:layout_marginBottom="14dp"
        android:src="@drawable/barcode"/>
        barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CaptureActivity.class);
                intent.setAction("com.google.zxing.client.android.SCAN");
                intent.putExtra("SAVE_HISTORY", false);
                startActivityForResult(intent, 0);

            }
        });*/
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*mywebview.setWebViewClient(new WebViewClient());
                mywebview.loadUrl("https://www.moaddi.com");*/
                /*Toast.makeText(WebViews.this,"Successfully Logged Out",Toast.LENGTH_LONG).show();
                MainActivity.fingAuth = 0;
                MainActivity.failAuth = 0;
                Intent in = new Intent(WebViews.this, MainActivity.class);
                startActivity(in);
                finish();
                //SHARED PREFERENCES VALUE FOR THE LOGGED IN STATUS SHARED PREFERENCE SHALL BE CHANGED TO ZERO HERE.
                SharedPreferences sp = getSharedPreferences("Credentials", 1);
                SharedPreferences.Editor et = sp.edit();
                et.putInt("LoginStatus",0);
                et.commit();*/
                    mywebview.loadUrl("https://www.moaddi.com/login.htm");
                    fingAuth=0;
                    Toast.makeText(getApplicationContext(), "Successfully Logged Out", Toast.LENGTH_LONG).show();
                }

        });
        fingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AvailabilityofHardwareAndRegisteredFingerprints.fingerprintCompatibility(WebViews.this))
                {
                    FingerPrintAuth.Finger(WebViews.this);
                    if (fingAuth == 1)
                    {
                        //LOGIN
                        Bundle b = new Bundle();
                        String s = sp.getString("usernameForFingerprint",null);
                        String t = sp.getString("passwordForFingerprint",null);
                        if (s == null || t == null)
                        {
                            Toast.makeText(getApplicationContext(), "Use Registered Username and Password for the first time to link it with the fingerprints registered on this device", Toast.LENGTH_LONG).show();
                            Intent in = new Intent(WebViews.this,MainActivity.class);
                            startActivity(in);
                            finish();
                        }
                        else {
                            //b.putString("URL", "https://www.moaddi.com/login.htm?userId=" + "1000000011" + "&password=" + "123456");
                            SharedPreferences.Editor et = sp.edit();
                            et.putInt("LoginStatus", 1);
                            et.commit();
                           /* Intent in = new Intent(MainActivity.this, WebViews.class);
                            in.putExtras(b);
                            startActivity(in);
                            finish();*/
                           mywebview.loadUrl("https://www.moaddi.com/login.htm?userId=" + s + "&password=" + t);
                            VolleyClass.nextpage(WebViews.this);
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
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                contents = data.getStringExtra("result");
                //mywebview.loadUrl("http://192.168.0.104:8081/Moaddi3/customer/buy.htm");
                mywebview.loadUrl("javascript:barcodeScann('"+contents+"')");
             // Toast.makeText(this,currentUrl,Toast.LENGTH_LONG).show();
              /*  if (currentUrl.contains("/customer/buy.htm")) {
                    mywebview.loadUrl("http://192.168.0.116:8082/moaddi3/customer/buy.htm?barcode=" + contents);
                }
               else  if (currentUrl.contains("/customer/item.htm")) {
                    mywebview.loadUrl("http://192.168.0.116:8082/moaddi3/customer/item.htm?barcode=" + contents);
                }*/
                //Log.d(TAG, "contents: " + contents);
                //Toast.makeText(this,contents,Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
// Handle cancel
                //Log.d(TAG, "RESULT_CANCELED");
                Toast.makeText(this,"No Output",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void networkAvailable() {
        online.setImageResource(R.drawable.on);
        //wifi.setImageResource(R.drawable.wifion);
    /* TODO: Your connection-oriented stuff here */
        if (j==1)
        {//Dialog for internet connection need closes as soon as a network is connected
            alertDialog.dismiss();
            j=0;
        }
            mywebview.loadUrl("https://www.moaddi.com/login.htm");
      //  Toast.makeText(WebViews.this,"Session finished,Login Again !",Toast.LENGTH_LONG).show();
    }

    @Override
    public void networkUnavailable() {
        online.setImageResource(R.drawable.offline);
        //wifi.setImageResource(R.drawable.wifioff);
        Toast.makeText(getApplicationContext(), "Internet Needed", Toast.LENGTH_SHORT).show();
    /* TODO: Your disconnection-oriented stuff here */
        alertDialogBuilder= new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Internet Connection Needed");
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        //alertDialog.setCanceledOnTouchOutside(false);
        //alertDialog.setCancelable(false);
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //If one tries to cancel the dialog box,app closes
                finish();
            }
        });
        j=1;
    }
    /*@Override
    public void onBackPressed() {
        // Write your code here
        WebBackForwardList mWebBackForwardList = mywebview.copyBackForwardList();
        //If nothing was there before this link
        if (mWebBackForwardList.getCurrentIndex() ==0)
        {
            Intent in = new Intent(WebViews.this, MainActivity.class);
            startActivity(in);d
            finish();
        }
        else {
            String historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex() - 1).getUrl();
            mywebview.loadUrl(historyUrl);
        }
            super.onBackPressed();
    }*/
    @Override
    public void onBackPressed() {
        /*String arr[]=visitedURLs.split("@@@");
        int l = arr.length;
        if (l>1)
        {
            mywebview.loadUrl(arr[l-2]);
            l--;
        }
        else {
            moveTaskToBack(true);
        }*/
        moveTaskToBack(true);
    }

}
