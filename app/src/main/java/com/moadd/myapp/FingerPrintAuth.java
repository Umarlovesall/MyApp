package com.moadd.myapp;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;

/**
 * Created by moadd on 6/29/2017.
 */

public class FingerPrintAuth {
    private static final String KEY_NAME = "yourKey";
    static KeyStore keyStore;
    static Cipher cipher = null;

    static KeyGenerator keyGenerator;
    static FingerprintManager.CryptoObject cryptoObject;
    static FingerprintManager fingerprintManager;
    static KeyguardManager keyguardManager;
    public static void Finger(Context context)

    {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Get an instance of KeyguardManager and FingerprintManager//
            keyguardManager = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);
            fingerprintManager =
                    (FingerprintManager) context.getSystemService(FINGERPRINT_SERVICE);

            //textView = (TextView) findViewById(R.id.textview);

            //Check whether the device has a fingerprint sensor//
            if (!fingerprintManager.isHardwareDetected()) {
                // If a fingerprint sensor isn’t available, then inform the user that they’ll be unable to use your app’s fingerprint functionality//
                //textView.setText("Your device doesn't support fingerprint authentication");
                //Toast.makeText(context, "Your device doesn't support fingerprint authentication", Toast.LENGTH_SHORT).show();
            }
            else
            {
            //Check whether the user has granted your app the USE_FINGERPRINT permission//
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                // If your app doesn't have this permission, then display the following text//
                //textView.setText("Please enable the fingerprint permission");
                Toast.makeText(context, "Please enable the fingerprint permission", Toast.LENGTH_SHORT).show();
            }

            //Check that the user has registered at least one fingerprint//
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                // If the user hasn’t configured any fingerprints, then display the following message//
                //textView.setText("No fingerprint configured. Please register at least one fingerprint in your device's Settings");
                Toast.makeText(context, "No fingerprint configured. Please register at least one fingerprint in your device's Settings", Toast.LENGTH_SHORT).show();
            }

            //Check that the lockscreen is secured//
            if (!keyguardManager.isKeyguardSecure()) {
                // If the user hasn’t secured their lockscreen with a PIN password or pattern, then display the following text//
                //textView.setText("Please enable lockscreen security in your device's Settings");
                Toast.makeText(context, "Please enable lockscreen security in your device's Settings", Toast.LENGTH_SHORT).show();
            } else {
                try {    generateKey();
                } catch (FingerprintException e) {
                    e.printStackTrace();
                }

                if (initCipher()) {
                    //If the cipher is initialized successfully, then create a CryptoObject instance//
                    cryptoObject = new FingerprintManager.CryptoObject(cipher);

                    // Here, I’m referencing the FingerprintHandler class that we’ll create in the next section. This class will be responsible
                    // for starting the authentication process (via the startAuth method) and processing the authentication process events//
                    FingerprintHandler helper = new FingerprintHandler(context);
                    helper.startAuth(fingerprintManager, cryptoObject);
                }
            }}
        }
    }

//Create the generateKey method that we’ll use to gain access to the Android keystore and generate the encryption key//

    private static void generateKey() throws FingerprintException {
        try {
            // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
            keyStore = KeyStore.getInstance("AndroidKeyStore");

            //Generate the key//
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            //Initialize an empty KeyStore//
            keyStore.load(null);

            //Initialize the KeyGenerator//
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)

                    //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            //Generate the key//
            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }
    }

    //Create a new method that we’ll use to initialize our cipher//
    public static boolean initCipher() {
        try {
            //Obtain a cipher instance and configure it with the properties required for fingerprint authentication//
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //Return true if the cipher has been initialized successfully//
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {

            //Return false if cipher initialization failed//
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    private static class FingerprintException extends Exception {
        public FingerprintException(Exception e) {
            super(e);
        }
    }
}

