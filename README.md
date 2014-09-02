Cordova KitKat External Files Plugin
=================================

This plugin calls the methods necessary for kitkat to setup external SD directories with the proper permissions.

If you create the directories on the sd using your computer, the app will not have write access to the files.

This will also log the external file directories during initialization.

As an example, on the Galaxy Tab, you will see this:
	External Path: /storage/emulated/0/Android/data/com.simplec.therapyplayer.dev/files
	External Path: /storage/extSdCard/Android/data/com.simplec.therapyplayer.dev/files

The first output is the internal storage.  The second is the external.  

Also, you can call the plugin to get the external paths in an array.

window.plugins.externalFileAccess.getExternalPaths(successCallback);

Where successCallback is a function(pathArray);

You can also access the externalPaths as a member variable of the plugin.

if (window.plugins.externalFileAccess.available==true) {
	//window.plugins.externalFileAccess.externalPaths is an array of paths
	//window.plugins.externalFileAccess.packageName is the app package name
}