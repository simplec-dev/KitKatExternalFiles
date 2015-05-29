

var channel = require('cordova/channel');
var utils = require('cordova/utils');

channel.createSticky('onPackageNameReady');
// Tell cordova channel to wait on the CordovaInfoReady event
channel.waitForInitialization('onPackageNameReady');
  
var KitKatExternalFileAccess = function() {
    this.available = false;
    this.packageName = null;
    this.externalPaths = null;
    this.storageStats = null;

    var me = this;

	 me.refresh = function(callback) {
			me.available = false;
		             
			 me.getExternalPaths(function(paths) {
				  me.externalPaths = paths;
				  me.available = true;
					if (callback) {
						callback(me);
					}          
		/*
				  me.getStorageStats(function(stats) {
						me.storageStats = stats;
						me.available = true;
		
						console.log(JSON.stringify(stats));
						if (callback) {
							callback(me);
						}          
				  },function(e) {
					  me.available = true;
						if (callback) {
							callback(me);
						}
						console.log("[ERROR] Error initializing external paths: " + e);
				  });*/
			 },function(e) {
				 me.available = false;
						if (callback) {
							callback(me);
						}
				  console.log("[ERROR] Error initializing external paths: " + e);
			 });
	 }	;

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

KitKatExternalFileAccess.prototype.getPackageName = function(successCallback, failureCallback) {
	cordova.exec(successCallback, failureCallback, 'KitKatExternalFileAccess', 'packageName', []);
};

KitKatExternalFileAccess.prototype.getExternalPaths = function(successCallback, failureCallback) {
	cordova.exec(successCallback, failureCallback, 'KitKatExternalFileAccess', 'externalPaths', []);
};

KitKatExternalFileAccess.prototype.getStorageStats = function(paths, successCallback, failureCallback) {
	paths = paths || [];
	cordova.exec(successCallback, failureCallback, 'KitKatExternalFileAccess', 'storageStats', paths);
};

KitKatExternalFileAccess.prototype.listAllFiles = function(root, successCallback, failureCallback) {
	cordova.exec(successCallback, failureCallback, 'KitKatExternalFileAccess', 'listAllFiles', [root]);
};

module.exports = new KitKatExternalFileAccess();

