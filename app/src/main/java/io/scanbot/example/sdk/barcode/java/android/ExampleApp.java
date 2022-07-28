package io.scanbot.example.sdk.barcode.java.android;

import android.app.Application;

import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDKInitializer;

public class ExampleApp extends Application {

    // demo trial license key
    private final static String LICENSE_KEY =
        "nsH6JVQLEbx/qZQiVeX5xln1FpTfQj" +
        "6xDuHMbHfUm6HgVXj2I818vaB5lf1b" +
        "Lpnet6iNuWHwamH3EKe9lyFFE2gEJi" +
        "4DCDi7JGZDVkzVcYEJ+UeNOhtEvy4P" +
        "PXfVNq07A4SVNPQFRBfNjhszWZvh3n" +
        "dVxJhn6UEFrjdgT9CfF9c6l88XLR2t" +
        "uVAWAWE/A7QYD6sXMRCQBORwOildO1" +
        "9FK/Uj8GOEY0zzK08ge4Y2tHao3XiA" +
        "DClfPDrR4UDGIyuEqfW2y40NoD4Ol2" +
        "Fw7EyUYB/HGxIQoSesnUMgIw+WzhGM" +
        "9R43hs0H96hr7azYA2XPkrd7Fyk2pw" +
        "oNrm+HAKTczQ==\nU2NhbmJvdFNESw" +
        "ppby5zY2FuYm90LmV4YW1wbGUuc2Rr" +
        "LmJhcmNvZGUuamF2YS5hbmRyb2lkCj" +
        "E2NjE3MzExOTkKNTEyCjI=\n";

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize the Scanbot Barcode Scanner SDK
        new ScanbotBarcodeScannerSDKInitializer()
                .withLogging(true, true) // consider disabling logging for production build!
                .license(this, LICENSE_KEY)
                .initialize(this);
    }
}
