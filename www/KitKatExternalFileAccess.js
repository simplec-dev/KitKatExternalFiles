

var channel = require('cordova/channel');
var utils = require('cordova/utils');

channel.createSticky('onExternalPathsReady');
// Tell cordova channel to wait on the CordovaInfoReady event
channel.waitForInitialization('onExternalPathsReady');
  
var KitKatExternalFileAccess = function() {

    this.available = false;
    this.packageName = null;
    this.externalPaths = null;

    var me = this;

    channel.onCordovaReady.subscribe(function() {
        me.getExternalPaths(function(paths) {
            me.externalPaths = paths;
            console.log("initializing package name: " + paths);

            me.getPackageName(function(name) {
                me.available = true;
                me.packageName = name;
                
                channel.onExternalPathsReady.fire();
                console.log("initializing package name: " + name);
            },function(e) {
                me.available = false;
                console.log("[ERROR] Error initializing package name: " + e);
            });

        },function(e) {
            me.available = false;
            console.log("[ERROR] Error initializing external paths: " + e);
        });
    });
};

/**
 * Acquire a new wake-lock (keep device awake)
 * 
 * @param successCallback
 *            function to be called when the wake-lock was acquired successfully
 * @param errorCallback
 *            function to be called when there was a problem with acquiring the
 *            wake-lock
 */
KitKatExternalFileAccess.prototype.getPackageName = function(successCallback, failureCallback) {
	cordova.exec(successCallback, failureCallback, 'KitKatExternalFileAccess', 'packageName', []);
};

/**
 * Release the wake-lock
 * 
 * @param successCallback
 *            function to be called when the wake-lock was released successfully
 * @param errorCallback
 *            function to be called when there was a problem while releasing the
 *            wake-lock
 */
KitKatExternalFileAccess.prototype.getExternalPaths = function(successCallback, failureCallback) {
	cordova.exec(successCallback, failureCallback, 'KitKatExternalFileAccess', 'externalPaths', []);
};

module.exports = new KitKatExternalFileAccess();
