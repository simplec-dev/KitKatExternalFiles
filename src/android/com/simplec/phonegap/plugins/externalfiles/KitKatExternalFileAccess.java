package com.simplec.phonegap.plugins.externalfiles;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

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
	private static final String ACTION_LIST_ALL_FILES = "listAllFiles";
	private static final String LOG_TAG = "KitKatExternalFileAccess";

	private String packageName = null;

	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);

		packageName = cordova.getActivity().getApplicationContext()
				.getPackageName();

		Log.d(LOG_TAG, "KitKatExternalFileAccess initialized");

	}
	
	public File getSpecialStorageDirectory() {		
		File test = new File("/mnt/usb_storage/USB_DISK0");
		if (test.exists()) {
			for (File f : test.listFiles()) {
				if (f.isDirectory()) {
					return f;
				}
			}
		}
		
		return null;
	}

	public String[] getExternalPaths() {
		Log.d(LOG_TAG, "KitKatExternalFileAccess getExternalPaths");
		File[] files = new File[0];
		

		Log.d(LOG_TAG, "KitKatExternalFileAccess Testing for Special Directories");
		File f = getSpecialStorageDirectory();
		if (f!=null) {
			Log.d(LOG_TAG, "KitKatExternalFileAccess SPECIAL DIR: "+f.getAbsolutePath());
			
			// need to return 2 for player to recognize this as an override
			files = new File[] { Environment.getExternalStorageDirectory(), f };
		} else {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				Log.d(LOG_TAG, "KitKatExternalFileAccess POST");
				try {
					Log.d(LOG_TAG, "KitKatExternalFileAccess POST 2");
					files = cordova.getActivity().getApplicationContext().getExternalFilesDirs(null);
					Log.d(LOG_TAG, "KitKatExternalFileAccess POST 3");
				} catch (Throwable e2) {
					Log.d(LOG_TAG, "KitKatExternalFileAccess POST 4");
					Log.e(LOG_TAG, "POST KitKat getExternalFilesDir unavailable. " + e2.getMessage(), e2);

					files = new File[] { Environment.getExternalStorageDirectory() };
					Log.d(LOG_TAG, "KitKatExternalFileAccess POST 5");
					
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
		}

		Log.d(LOG_TAG, "KitKatExternalFileAccess getExternalPaths 2 - count = "+files.length);
		
		List<String> externalPaths = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file!=null && file.exists()) {
				externalPaths.add(file.getPath());
				Log.d(LOG_TAG, "KitKatExternalFileAccess getExternalPaths 2 files[i] = "+file.getPath());
			}
		}
		Log.d(LOG_TAG, "KitKatExternalFileAccess getExternalPaths 3");

		return externalPaths.toArray(new String[0]);
	}

	@Override
	public boolean execute(String action, JSONArray args,
			final CallbackContext callbackContext) throws JSONException {
		try {
			if (ACTION_PACKAGE_NAME.equals(action)) {
				callbackContext.success(packageName);
				return true;

			} else if (ACTION_EXTERNAL_PATHS.equals(action)) {
				Log.d(LOG_TAG, "KitKatExternalFileAccess ACTION_EXTERNAL_PATHS 1");
				JSONArray r = new JSONArray();
				String[] externalPaths = getExternalPaths();
				Log.d(LOG_TAG, "KitKatExternalFileAccess ACTION_EXTERNAL_PATHS 2 - "+externalPaths.length);
				for (String path : externalPaths) {
					r.put(path);
				}
				Log.d(LOG_TAG, "KitKatExternalFileAccess ACTION_EXTERNAL_PATHS 3");
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

			} else if (ACTION_LIST_ALL_FILES.equals(action)) {
				String root = args.getString(0);
				Collection<String> files = getRecursiveFiles(root);

				JSONArray r = new JSONArray();
				for (String file : files) {
					r.put(file);
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
			Log.d(LOG_TAG, "KitKatExternalFileAccess EXECUTE ERROR");
			e.printStackTrace();
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


	private Collection<String> getRecursiveFiles(String srcPath) {
		Collection<String> files = new TreeSet<String>();
		
		File f = new File(srcPath);
		if (f.isDirectory()) {
			String children[] = f.list();

			for (String child : children) {
				File cf = new File(srcPath+"/"+child);
				
				if (cf.isDirectory()) {
					Collection<String> subFiles = getRecursiveFiles(srcPath+"/"+child);
					for (String subFile : subFiles) {
						files.add((child + "/" + subFile).toLowerCase());
					}
					// do something
				} else if (!cf.isHidden()){
					files.add(child);
				}		
			}
		}
		
		return files;
	}
}