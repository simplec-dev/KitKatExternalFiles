
var KitKatExternalFileAccess = function() {
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
KitKatExternalFileAccess.prototype.acquire = function(successCallback, failureCallback) {
	cordova.exec(successCallback, failureCallback, 'KitKatExternalFileAccess', 'acquire', []);
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
KitKatExternalFileAccess.prototype.release = function(successCallback, failureCallback) {
	cordova.exec(successCallback, failureCallback, 'KitKatExternalFileAccess', 'release', []);
};

module.exports = new KitKatExternalFileAccess();