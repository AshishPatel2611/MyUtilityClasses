package com.theportal

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.google.android.gms.location.*
import com.theportal.network.WebServiceRetrofitUtil
import com.theportal.utils.*
import com.theportal.utils.ConnectionUtil.startNetworkConnectionChecking
import com.theportal.utils.ConnectionUtil.stopNetworkConnectionChecking
import com.theportal.utils.PermissionUtils.PermissionStatus.isCameraPermissionGranted
import com.theportal.utils.PermissionUtils.PermissionStatus.isLocationPermissionGranted
import java.io.File


open class BaseActivity : AppCompatActivity() {

    val ACTION_REQUEST_CAMERA_STORAGE_PERMISSION = 1101
    val ACTION_REQUEST_LOCATION_PERMISSION = 1102

    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 20000
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2

    private var mLocationRequest: LocationRequest? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null
    var mLocation: Location? = null

    fun isGPSenabled(): Boolean {

        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            && !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        ) {
            showLocationGPSEnableSettingDialog(this)
            return false
        } else {
            return true
        }


        /*val provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.contains("gps")) {
            showLocationGPSEnableSettingDialog(this)
            return false
        } else {
            return true
        }*/

    }

    var gpsSettingDialog: androidx.appcompat.app.AlertDialog? = null

    fun showLocationGPSEnableSettingDialog(activity: Activity) {
        if (gpsSettingDialog == null || !gpsSettingDialog!!.isShowing) {
            gpsSettingDialog = androidx.appcompat.app.AlertDialog.Builder(activity, R.style.DialogTheme)
                .setTitle("GPS disabled!")
                .setMessage("GPS required for searching Portals near you!")
                .setPositiveButton("Enable GPS") { dialogInterface, i ->
                    // Call your Alert message
                    dialogInterface.dismiss()
                    activity.startActivityForResult(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                        Constants.RC_ENABLE_GPS
                    )
                }
                .setCancelable(false)
                .create()

            gpsSettingDialog!!.show()
        }
    }

    fun initializeLocationProvider(onLocationChanged: (Location) -> Unit) {

        if (isLocationPermissionGranted(this)) {
            if (isGPSenabled()) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

                mLocationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        super.onLocationResult(locationResult)

                        if (locationResult != null) {
                            mLocation = locationResult.lastLocation
                            onLocationChanged(mLocation!!)

                            Log.d(
                                "BaseActivity",
                                "onLocationResult : $mLocation    accuracy: ${locationResult.lastLocation!!.accuracy}"
                            )
                        }
                    }
                }

                mLocationRequest = LocationRequest()
                mLocationRequest!!.interval = UPDATE_INTERVAL_IN_MILLISECONDS
                mLocationRequest!!.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
                mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                mLocationRequest!!.smallestDisplacement = 10f

                try {
                    mFusedLocationClient?.requestLocationUpdates(
                        mLocationRequest,
                        mLocationCallback!!, Looper.myLooper()
                    )

                    mFusedLocationClient!!.lastLocation
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful && task.result != null) {
                                mLocation = task.result
                            } else {
                                Log.w("BaseActivity", "getLastLocation : Failed to get location.")
                            }
                        }

                } catch (e: SecurityException) {
                    Log.e("BaseActivity", "initializeLocationProvider : ${e.printStackTrace()} ")
                }
            }
        } else {
            checkAndAskLocationPermission()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("BaseActivity", "onCreate: ")
        disableAutoFill()
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.i("BaseActivity", "onDestroy : ${this.localClassName} ")
        stopNetworkConnectionChecking()
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
    }

    fun showShortToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    fun destroyLoginSession(activity: Activity) {
        hideProgress()
        showShortToast(Constants.SESSION_EXPIRED)
        MyAppPreferenceUtils.clearLoginSession(activity)
        activity.finish()
        val intent = Intent(activity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)

    }

    fun animateEnterTransition() {
        overridePendingTransition(
            com.theportal.R.anim.enter,
            com.theportal.R.anim.exit
        )
    }

    fun animateExitTransition() {
        overridePendingTransition(
            com.theportal.R.anim.enter_return,
            com.theportal.R.anim.exit_return
        )
    }

    fun showSnackbar(view: View, msg: String, type: SnackbarUtils.SnackbarType) {
        when (type) {

            SnackbarUtils.SnackbarType.SUCCESS -> {
                SnackbarUtils.success(this, view, msg)
            }
            SnackbarUtils.SnackbarType.WARNING -> {
                SnackbarUtils.warning(this, view, msg)
            }
            SnackbarUtils.SnackbarType.ERROR -> {
                SnackbarUtils.error(this, view, msg)
            }
        }
    }

    var progressDialog: ProgressDialog? = null

    fun showProgress(msg: String, cancelable: Boolean) {

        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }

        progressDialog = ProgressDialogUtils.createProgressDialog(this, "", msg, cancelable)
        progressDialog!!.show()

    }

    fun hideProgress() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }

    }

    fun startNetworkConnectionChecker() {
        startNetworkConnectionChecking(this, object : ConnectionUtil.MyListener {
            override fun onConnected(subtypeName: String) {
                showSnackbar(
                    window.decorView, "onConnected with $subtypeName",
                    SnackbarUtils.SnackbarType.SUCCESS
                )
            }

            override fun onDisconnected() {
                showSnackbar(window.decorView, "onDisconnected", SnackbarUtils.SnackbarType.ERROR)

            }
        })
    }

    fun checkAndAskLocationPermission(): Boolean {
        if ((!isLocationPermissionGranted(this))) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                ACTION_REQUEST_LOCATION_PERMISSION
            )
            return false
        } else {
            return true
        }
    }


    fun checkAndAskStorageAndCameraPermission() {
        /*  if ((!isStoragePermissionGranted(this)) && (!isCameraPermissionGranted(this))) {
              ActivityCompat.requestPermissions(
                  this,
                  arrayOf(
                      Manifest.permission.WRITE_EXTERNAL_STORAGE,
                      Manifest.permission.READ_EXTERNAL_STORAGE,
                      Manifest.permission.CAMERA
                  ),
                  ACTION_REQUEST_CAMERA_STORAGE_PERMISSION
              )
          } else if (isStoragePermissionGranted(this) && (!isCameraPermissionGranted(this))) {
              ActivityCompat.requestPermissions(
                  this,
                  arrayOf(Manifest.permission.CAMERA),
                  ACTION_REQUEST_CAMERA_STORAGE_PERMISSION
              )
          } else if ((!isStoragePermissionGranted(this)) && isCameraPermissionGranted(this)) {
              ActivityCompat.requestPermissions(
                  this,
                  arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                  ACTION_REQUEST_CAMERA_STORAGE_PERMISSION
              )
          }*/

        if ((!isCameraPermissionGranted(this))) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                ACTION_REQUEST_CAMERA_STORAGE_PERMISSION
            )
        }
    }


    fun chooseImageSourceDialog() {

        val imageSourceDialog = AlertDialogUtil.showTwoButtonAlertDialog(this,
            "",
            "Choose Image Source",
            "Camera",
            "Gallery",
            object : AlertDialogUtil.OnTwoButtonAlertDialogClickListener {
                override fun onPositiveButtonClicked(alertDialog: AlertDialog) {
                    alertDialog.dismiss()
                    if (isCameraPermissionGranted(this@BaseActivity)) {
                        openCamera()
                    } else {
                        checkAndAskStorageAndCameraPermission()
                    }

                }

                override fun onNegativeButtonClicked(alertDialog: AlertDialog) {
                    alertDialog.dismiss()
                   /* if (isStoragePermissionGranted(this@BaseActivity)) {
                        openGallery()
                    } else {
                        checkAndAskStorageAndCameraPermission()
                    }*/
                }
            })
        imageSourceDialog.show()
    }


    private fun openGallery() {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        i.type = "image/*"
        startActivityForResult(i, Constants.ACTION_REQUEST_GALLERY)
    }

    var cameraImagePath: String = ""

    private fun openCamera() {
        val tempFile = getTempImageUri()
        cameraImagePath = tempFile.absolutePath
        val getCameraImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        getCameraImage.putExtra(
            MediaStore.EXTRA_OUTPUT,
            FileProvider.getUriForFile(this, getString(R.string.fileProviderAuthority), tempFile)
        )
        startActivityForResult(getCameraImage, Constants.ACTION_REQUEST_CAMERA)
    }


    private fun getTempImageUri(): File {
        val imageFileName = "IMG${System.currentTimeMillis()}.png"
        val path = cacheDir.path + "/" + imageFileName
        return File(path)
    }


    override fun onResume() {
        super.onResume()
        if (WebServiceRetrofitUtil.webService == null) {
            WebServiceRetrofitUtil.init(this)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {

            /*  ACTION_REQUEST_CAMERA_STORAGE_PERMISSION -> {
                  checkCameraStoragePermissionResult()
              }

              ACTION_REQUEST_LOCATION_PERMISSION -> {
                  checkLocationPermissionResult()
              }*/

        }

    }

    private fun checkLocationPermissionResult() {
        if (!isLocationPermissionGranted(this)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                AlertDialog.Builder(this, R.style.DialogTheme)
                    .setTitle("LOCATION PERMISSION")
                    .setMessage("Storage Permission required to access your location!")
                    .setPositiveButton("Ok") { dialogInterface, i ->
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            this@BaseActivity,
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ),
                            ACTION_REQUEST_CAMERA_STORAGE_PERMISSION
                        )
                    }
                    .create()
                    .show()
            } else {


                AlertDialog.Builder(this, R.style.DialogTheme)
                    .setTitle("LOCATION PERMISSION")
                    .setMessage("Storage Permission required to access your location!")
                    .setPositiveButton("Go To Settings") { dialogInterface, i ->
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivityForResult(intent, ACTION_REQUEST_LOCATION_PERMISSION)
                    }
                    .create()
                    .show()

            }

        }
    }


    /*private fun checkCameraStoragePermissionResult() {
        if (!isStoragePermissionGranted(this)) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this, R.style.DialogTheme)
                    .setTitle("STORAGE PERMISSION")
                    .setMessage("Storage Permission required to access your images from storage!")
                    .setPositiveButton("Ok") { dialogInterface, i ->
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            this@BaseActivity,
                            arrayOf(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ),
                            ACTION_REQUEST_CAMERA_STORAGE_PERMISSION
                        )
                    }
                    .create()
                    .show()
            } else {

                // Toast.makeText(this, "Enable STORAGE Permission in settings!", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder(this, R.style.DialogTheme)
                    .setTitle("STORAGE PERMISSION")
                    .setMessage("Storage Permission required to access your images from storage!")
                    .setPositiveButton("Go To Settings") { dialogInterface, i ->
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivityForResult(intent, ACTION_REQUEST_LOCATION_PERMISSION)
                    }
                    .create()
                    .show()

            }

        } else if ((!isCameraPermissionGranted(this))) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this, R.style.DialogTheme)
                    .setTitle("CAMERA PERMISSION")
                    .setMessage("Camera Permission required to capture images!")
                    .setPositiveButton("Ok") { dialogInterface, i ->
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            this@BaseActivity,
                            arrayOf(Manifest.permission.CAMERA),
                            ACTION_REQUEST_CAMERA_STORAGE_PERMISSION
                        )
                    }
                    .create()
                    .show()
            } else {

                //   Toast.makeText(this, "Enable STORAGE Permission in settings!", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder(this, R.style.DialogTheme)
                    .setTitle("CAMERA PERMISSION")
                    .setMessage("Camera Permission required to capture images!")
                    .setPositiveButton("Go To Settings") { dialogInterface, i ->
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivityForResult(intent, ACTION_REQUEST_LOCATION_PERMISSION)
                    }
                    .create()
                    .show()
            }
        }
    }*/


    @TargetApi(Build.VERSION_CODES.O)
    private fun disableAutoFill() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            window.decorView.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
    }


}