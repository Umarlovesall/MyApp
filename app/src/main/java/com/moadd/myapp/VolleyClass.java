package com.moadd.myapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static com.moadd.myapp.WebViews.URLs;

/**
 * Created by moadd on 6/28/2017.
 */

public class VolleyClass {
    public static void nextpage(final Context c) {
        RequestQueue requestQueue = com.android.volley.toolbox.Volley.newRequestQueue(c);
        JSONObject jsonBody = new JSONObject();
                   /* jsonBody.put("username", "Android Volley Demo");
                    jsonBody.put("password", "BNK");*/
        final String requestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs, new Response.Listener<String>() {
            @Override
            public void onResponse(String responses) {
                //Log.i("VOLLEY", responses);
                //Toast.makeText(MainActivity.this,"Response is :"+responses,Toast.LENGTH_LONG).show();
                //  tv.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
                //Toast.makeText(c, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        requestQueue.add(stringRequest);
    }

    public static void disableSSLCertificateChecking() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        } };

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
               /* if (response != null)
                {
                    Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                    //LOGIN NOW and save login status
                    //startActivity(in);
                    SharedPreferences.Editor et = sp.edit();
                    et.putString("username",un);
                    et.putString("password",pw);
                    et.putInt("LoginStatus",1);
                    et.commit();
                }*/

