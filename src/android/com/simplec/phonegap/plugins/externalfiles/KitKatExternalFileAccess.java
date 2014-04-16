package com.simplec.phonegap.plugins.externalfiles;

import org.apache.cordova.CordovaInterface;
import android.util.Log;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaPlugin;

import java.io.File;

public class KitKatExternalFileAccess extends CordovaPlugin {
	private String packageName = null;
	private String[] externalPaths = null;

    private static final String LOG_TAG = "KitKatExternalFileAccess";

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        packageName = cordova.getActivity().getApplicationContext().getPackageName();

		File[] files = cordova.getActivity().getApplicationContext().getExternalFilesDirs(null);
		externalPaths = new String[files.length];
		for (int i=0; i<files.length; i++) {
			File file = files[i];
			externalPaths[i] = file.getPath();
		}
		for (File file : files) {
	        Log.d(LOG_TAG, "External Path: "+file.getPath());
		}

        Log.d(LOG_TAG, "KitKatExternalFileAccess initialized");

    }
}
