package com.moadd.myapp;

import android.content.SharedPreferences;

import static com.moadd.myapp.MainActivity.pw;
import static com.moadd.myapp.MainActivity.un;

/**
 * Created by moadd on 6/30/2017.
 */

public class FingerPrintNeededorNot {
    public static void needed(SharedPreferences sp)
    {
        SharedPreferences.Editor et = sp.edit();
        et.putString("usernameForFingerprint", un);
        et.putString("passwordForFingerprint", pw);
        et.commit();
    }
}
