package com.moadd.myapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import static com.moadd.myapp.FingerPrintAuth.fingerprintManager;
import static com.moadd.myapp.FingerPrintAuth.keyguardManager;

/**
 * Created by moadd on 6/30/2017.
 */

public class AvailabilityofHardwareAndRegisteredFingerprints {
    public static boolean fingerprintCompatibility(Context context)
    {
        int i= 0;
        if (!fingerprintManager.isHardwareDetected()) {
            // If a fingerprint sensor isn’t available, then inform the user that they’ll be unable to use your app’s fingerprint functionality//
            //textView.setText("Your device doesn't support fingerprint authentication");
            //Toast.makeText(context, "Your device doesn't support fingerprint authentication", Toast.LENGTH_SHORT).show();
            i=1;
        }
        else
        {
        //Check whether the user has granted your app the USE_FINGERPRINT permission//
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            // If your app doesn't have this permission, then display the following text//
            //textView.setText("Please enable the fingerprint permission");
            Toast.makeText(context, "Please enable the fingerprint permission", Toast.LENGTH_SHORT).show();
            i=1;
        }

        //Check that the user has registered at least one fingerprint//
        if (!fingerprintManager.hasEnrolledFingerprints()) {
            // If the user hasn’t configured any fingerprints, then display the following message//
            //textView.setText("No fingerprint configured. Please register at least one fingerprint in your device's Settings");
            Toast.makeText(context, "No fingerprint configured. Please register at least one fingerprint in your device's Settings", Toast.LENGTH_SHORT).show();
            i=1;
        }


        //Check that the lockscreen is secured//
        if (!keyguardManager.isKeyguardSecure()) {
            // If the user hasn’t secured their lockscreen with a PIN password or pattern, then display the following text//
            //textView.setText("Please enable lockscreen security in your device's Settings");
            Toast.makeText(context, "Please enable lockscreen security in your device's Settings", Toast.LENGTH_SHORT).show();
                i=1;
        }}
        if (i==1)
            return false;
        else
            return true;
    }
}
