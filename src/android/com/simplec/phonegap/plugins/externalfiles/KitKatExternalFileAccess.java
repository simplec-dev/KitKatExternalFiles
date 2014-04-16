package com.simplec.phonegap.plugins.externalfiles;

import org.apache.cordova.CordovaInterface;
import android.util.Log;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaPlugin;

import java.io.File;

public class KitKatExternalFileAccess extends CordovaPlugin {
    private static final String ACTION_PACKAGE_NAME = "packageName";
    private static final String ACTION_EXTERNAL_PATHS = "externalPaths";
    private static final String LOG_TAG = "KitKatExternalFileAccess";
    
	private String packageName = null;
	private String[] externalPaths = null;


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

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
      try {
        if (ACTION_PACKAGE_NAME.equals(action)) {
          cordova.getActivity().runOnUiThread(
              new Runnable() {
                public void run() {
                    callbackContext.sendPluginResult(new PluginResult(packageName));
                }
              });
          return true;

        } else if (ACTION_EXTERNAL_PATHS.equals(action)) {
          cordova.getActivity().runOnUiThread(
              new Runnable() {
                public void run() {
                  callbackContext.sendPluginResult(new PluginResult(externalPaths));
                }
              });
          return true;

        } else {
          callbackContext.error(action + " is not a supported function. Did you mean '" + ACTION_EXTERNAL_PATHS + "'?");
          return false;
        }
      } catch (Exception e) {
        callbackContext.error(e.getMessage());
        return false;
      }
    }
}
