

var channel = require('cordova/channel');
var utils = require('cordova/utils');

channel.createSticky('onPackageNameReady');
// Tell cordova channel to wait on the CordovaInfoReady event
channel.waitForInitialization('onExternalPathsReady');
  
var KitKatExternalFileAccess = function() {
    this.available = false;
    this.packageName = null;
    this.externalPaths = null;
    this.storageStats = null;

    var me = this;

    channel.onCordovaReady.subscribe(function() {
        me.getPackageName(function(name) {
            me.packageName = name;
            
            channel.onPackageNameReady.fire();
            console.log("initializing package name: " + name);
            
            me.refresh();
        },function(e) {
            me.available = false;
            console.log("[ERROR] Error initializing package name: " + e);
        });
        
    });
};

KitKatExternalFileAccess.prototype.refresh = function(callback) {
    this.available = false;
    this.getExternalPaths(function(paths) {
        this.externalPaths = paths;

        this.getStorageStats(function(stats) {
            this.storageStats = stats;
            this.available = true;
            callback();
        },function(e) {
            this.available = false;
            callback();
            console.log("[ERROR] Error initializing external paths: " + e);
        });
    },function(e) {
        this.available = false;
        callback();
        console.log("[ERROR] Error initializing external paths: " + e);
    });

}

KitKatExternalFileAccess.prototype.getPackageName = function(successCallback, failureCallback) {
	cordova.exec(successCallback, failureCallback, 'KitKatExternalFileAccess', 'packageName', []);
};

KitKatExternalFileAccess.prototype.getExternalPaths = function(successCallback, failureCallback) {
	cordova.exec(successCallback, failureCallback, 'KitKatExternalFileAccess', 'externalPaths', []);
};

KitKatExternalFileAccess.prototype.getStorageStats = function(successCallback, failureCallback) {
	cordova.exec(successCallback, failureCallback, 'KitKatExternalFileAccess', 'storageStats', []);
};

module.exports = new KitKatExternalFileAccess();
