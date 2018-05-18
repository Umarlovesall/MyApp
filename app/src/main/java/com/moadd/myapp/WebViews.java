package com.moadd.myapp;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class WebViews extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {
 /*   private final static int CAPTURE_RESULTCODE = 1;
    private ValueCallback<Uri> mUploadMessage;
    private String filePath;*/
int x=0;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final String TAG = TestWebVIew.class.getSimpleName();
    private WebSettings webSettings;
    private ValueCallback<Uri[]> mUploadMessage;
    private String mCameraPhotoPath = null;
    private long size = 0;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;
    int j;
    ImageView  home,online,fingerprint,iv;
    String currentUrl;
    WebView mywebview;
    //Button apps;//logout
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
        //READ and WRITE are dangerous permissions for android M and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        online = (ImageView) findViewById(R.id.online);
        home = (ImageView) findViewById(R.id.home);
        mywebview = (WebView) findViewById(R.id.web);
        iv= (ImageView) findViewById(R.id.iv);
       // barcode = (ImageView) findViewById(R.id.bar);
        fingerprint= (ImageView) findViewById(R.id.finger);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentUrl.contains("login.htm"))
                {
                   mywebview.loadUrl("https://www.moaddi.com/login.htm");
                }
            }
        });
        /*       <Button
            android:id="@+id/apps"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_marginLeft="5dp"
            android:layout_marginBottom="5dp"
            android:layout_marginTop="5dp"
            android:layout_marginRight="5dp"
            android:text="Apps"
            android:textStyle="bold|italic"
            android:textColor="#fff"
            android:elevation="5dp"
            android:background="@drawable/shape"
            android:textSize="10sp"
            android:layout_weight="1"/>*/
       // apps = (Button) findViewById(R.id.apps);
       // logout = (Button) findViewById(R.id.logout);
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
        webSettings = mywebview.getSettings();
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(webSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setLoadWithOverviewMode(true);
        mywebview.setWebViewClient(new PQClient());
        mywebview.setWebChromeClient(new PQChromeClient());
        //if SDK version is greater of 19 then activate hardware acceleration otherwise activate software acceleration
        if (Build.VERSION.SDK_INT >= 19) {
            mywebview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 19) {
            mywebview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mywebview.getSettings().setJavaScriptEnabled(true);
        mywebview.getSettings().setAllowFileAccess(true);
        mywebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mywebview.getSettings().setAppCacheEnabled(false);
        mywebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //mywebview.clearCache(true);
        /*CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();*/
       /* {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        } */
        //mywebview.loadUrl("http://192.168.0.116:8081/Moaddi123/customer/buy.htm");
        //SOLUTION https://stackoverflow.com/questions/4920770/androidhow-to-add-support-the-javascript-alert-box-in-webviewclient

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
       /* apps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(WebViews.this, ListOfApps.class);
                startActivity(in);
            }
        });*/
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
       /* logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mywebview.loadUrl("https://www.moaddi.com/login.htm");
                    fingAuth=0;
                    Toast.makeText(getApplicationContext(), "Successfully Logged Out", Toast.LENGTH_LONG).show();
                }

        });*/
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
        //super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                contents = data.getStringExtra("result");
                //mywebview.loadUrl("http://192.168.0.104:8081/Moaddi3/customer/buy.htm");
                mywebview.loadUrl("javascript:barcodeScann('"+contents+"')");
            } else if (resultCode == RESULT_CANCELED) {
// Handle cancel
                //Log.d(TAG, "RESULT_CANCELED");
                Toast.makeText(this,"No Output",Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode != INPUT_FILE_REQUEST_CODE || mUploadMessage == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        try {
            String file_path = mCameraPhotoPath.replace("file:","");
            File file = new File(file_path);
            size = file.length();

        }catch (Exception e){
            Log.e("Error!", "Error while opening image file" + e.getLocalizedMessage());
        }

        if (data != null || mCameraPhotoPath != null) {
            Integer count = 1;
            ClipData images = null;
            try {
                images = data.getClipData();
            }catch (Exception e) {
                Log.e("Error!", e.getLocalizedMessage());
            }

            if (images == null && data != null && data.getDataString() != null) {
                count = data.getDataString().length();
            } else if (images != null) {
                count = images.getItemCount();
            }
            Uri[] results = new Uri[count];
            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (size != 0) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else if (data.getClipData() == null) {
                    results = new Uri[]{Uri.parse(data.getDataString())};
                } else {

                    for (int i = 0; i < images.getItemCount(); i++) {
                        results[i] = images.getItemAt(i).getUri();
                    }
                }
                mUploadMessage.onReceiveValue(results);
                mUploadMessage = null;
            }
            else {
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
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
        //moveTaskToBack(true);
        x++;
        if (x==1 && currentUrl.contains("login.htm"))
        {
            Toast.makeText(this,"Press again to exit", Toast.LENGTH_SHORT).show();
        }
        else if (x==2)
        {
            finish();
        }

    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    public class PQChromeClient extends WebChromeClient {

        // For Android 5.0+
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
            }
            mUploadMessage = filePath;
            Log.e("FileCooserParams => ", filePath.toString());

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e(TAG, "Unable to create Image File", ex);
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }

            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            contentSelectionIntent.setType("image/*");

            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[2];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
            startActivityForResult(Intent.createChooser(chooserIntent, "Select images"), 1);

            return true;

        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mywebview.canGoBack()) {
            mywebview.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)

        return super.onKeyDown(keyCode, event);
    }


    public class PQClient extends WebViewClient {
        ProgressDialog progressDialog;
        @Override
        public void onPageFinished(WebView view, String url) {
            mywebview.loadUrl("javascript:(function(){ " +
                    "document.getElementById('android-app').style.display='none';})()");

            try {
                // Close progressDialog
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
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
                intent.putExtra("SAVE_HISTORY", true);
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                // intent.putExtra("SCAN_FORMATS", "QR_CODE");
                startActivityForResult(intent, 0);
            }
            if (currentUrl.contains("nearest.htm"))
            {
                mywebview.loadUrl("javascript:success('')");
            }
            if (currentUrl.contains("login"))
            {

                iv.setImageResource(R.drawable.logou);
            }
            if (currentUrl.contains("customer/buy.htm"))
            {
                iv.setImageResource(R.drawable.logoooou);
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
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // If url contains mailto link then open Mail Intent
            if (url.contains("mailto:")) {

                // Could be cleverer and use a regex
                //Open links in new browser
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                // Here we can open new activity

                return true;

            } else {

                // Stay within this webview and load url
                view.loadUrl(url);
                return true;
            }
        }

        //Show loader on url load
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            // Then show progress  Dialog
            // in standard case YourActivity.this
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(WebViews.this);
                progressDialog.setMessage("Loading...");
                progressDialog.hide();
            }
        }
    }

}
