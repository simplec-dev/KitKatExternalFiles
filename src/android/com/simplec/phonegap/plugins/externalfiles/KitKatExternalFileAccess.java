package com.simplec.phonegap.plugins.externalfiles;

import org.apache.cordova.CordovaInterface;
                             
import org.json.JSONObject; 
import org.json.JSONArray;
import org.json.JSONException;


import android.util.Log;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaPlugin; 
import org.apache.cordova.CallbackContext;

import java.io.File;
import java.lang.reflect.Method;

public class KitKatExternalFileAccess extends CordovaPlugin {
    private static final String ACTION_PACKAGE_NAME = "packageName";
    private static final String ACTION_EXTERNAL_PATHS = "externalPaths";
    private static final String LOG_TAG = "KitKatExternalFileAccess";
    
	private String packageName = null;
	private String[] externalPaths = null;


    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        packageName = cordova.getActivity().getApplicationContext().getPackageName();

        File[] files = new File[0];
        try {
        	files = cordova.getActivity().getApplicationContext().getExternalFilesDirs(null);
        } catch (Throwable e) {
	        Log.d(LOG_TAG, "KitKat getExternalFilesDir unavailable.  Getting old version via reflection.");
        	try {
				Object context = cordova.getActivity().getApplicationContext();
				Method m = context.getClass().getMethod("getExternalFilesDir", new Class[]{String.class});
				m.setAccessible(true);
				File file = (File)m.invoke(context, new Object[]{null});
				
				files = new File[] {file};
        	} catch (Throwable e2) {
    	        Log.d(LOG_TAG, "PRE KitKat getExternalFilesDir unavailable. "+e2.getMessage());
        	}
        }
        
		externalPaths = new String[files.length];
		for (int i=0; i<files.length; i++) {
			File file = files[i];
			externalPaths[i] = file.getPath();
	        Log.d(LOG_TAG, "External Path: "+externalPaths[i]);
		}

        Log.d(LOG_TAG, "KitKatExternalFileAccess initialized");

    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
      try {
        if (ACTION_PACKAGE_NAME.equals(action)) {
                    callbackContext.success(packageName);
          return true;

        } else if (ACTION_EXTERNAL_PATHS.equals(action)) {        
                    JSONArray r = new JSONArray();
					for (String path : externalPaths) {
					  r.put(path);
					}
                    callbackContext.success(r);
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