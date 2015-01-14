package com.simplec.phonegap.plugins.externalfiles;

import java.io.File;
import java.lang.reflect.Method;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class KitKatExternalFileAccess extends CordovaPlugin {
	private static final String ACTION_PACKAGE_NAME = "packageName";
	private static final String ACTION_EXTERNAL_PATHS = "externalPaths";
	private static final String ACTION_STORAGE_STATS = "storageStats";
	private static final String LOG_TAG = "KitKatExternalFileAccess";

	private String packageName = null;

	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);

		packageName = cordova.getActivity().getApplicationContext()
				.getPackageName();

		Log.d(LOG_TAG, "KitKatExternalFileAccess initialized");

	}

	public String[] getExternalPaths() {
		File[] files = new File[0];
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			try {
				files = cordova.getActivity().getApplicationContext().getExternalFilesDirs(null);
			} catch (Throwable e2) {
				Log.e(LOG_TAG, "POST KitKat getExternalFilesDir unavailable. " + e2.getMessage(), e2);

				files = new File[] { Environment.getExternalStorageDirectory() };
				
				e2.printStackTrace();
			}
		} else {
			Log.d(LOG_TAG,
					"KitKat getExternalFilesDir unavailable.  Getting old version via reflection.");
			try {
				Object context = cordova.getActivity().getApplicationContext();
				Method m = context.getClass().getMethod("getExternalFilesDir",
						new Class[] { String.class });
				m.setAccessible(true);
				File file = (File) m.invoke(context, new Object[] { null });

				files = new File[] { file };
			} catch (Throwable e2) {
				Log.e(LOG_TAG, "PRE KitKat getExternalFilesDir unavailable. " + e2.getMessage(), e2);

				files = new File[] { Environment.getExternalStorageDirectory() };
				
				e2.printStackTrace();
			}
		}

		String[] externalPaths = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			externalPaths[i] = file.getPath();
		}

		return externalPaths;
	}

	@Override
	public boolean execute(String action, JSONArray args,
			final CallbackContext callbackContext) throws JSONException {
		try {
			if (ACTION_PACKAGE_NAME.equals(action)) {
				callbackContext.success(packageName);
				return true;

			} else if (ACTION_EXTERNAL_PATHS.equals(action)) {
				JSONArray r = new JSONArray();
				String[] externalPaths = getExternalPaths();
				for (String path : externalPaths) {
					r.put(path);
				}
				callbackContext.success(r);
				return true;

			} else if (ACTION_STORAGE_STATS.equals(action)) {
				JSONArray r = new JSONArray();
				String[] externalPaths = getExternalPaths();
				for (String path : externalPaths) {
					JSONObject obj = getStatsForPath(path);
					obj.put("path", path);
					r.put(obj);
				}
				callbackContext.success(r);
				return true;

			} else {
				callbackContext.error(action
						+ " is not a supported function. Did you mean '"
						+ ACTION_EXTERNAL_PATHS + "'?");
				return false;
			}
		} catch (Exception e) {
			callbackContext.error(e.getMessage());
			return false;
		}
	}

	private JSONObject getStatsForPath(String path) throws JSONException {
		JSONObject obj = new JSONObject();
		try {
			StatFs statFs = new StatFs(path);

			long availableSize = statFs.getAvailableBytes();
			long freeSize = statFs.getFreeBytes();
			long totalSize = statFs.getTotalBytes();

			obj.put("available", availableSize);
			obj.put("free", freeSize);
			obj.put("total", totalSize);
		} catch (Exception e) {
			obj.put("available", 0);
			obj.put("free", 0);
			obj.put("total", 0);
		}

		return obj;
	}
}