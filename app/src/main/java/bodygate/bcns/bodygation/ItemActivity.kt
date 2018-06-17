package bodygate.bcns.bodygation

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Surface
import cn.gavinliu.android.lib.scale.config.ScaleConfig

val REQUEST_VIDEO_PERMISSIONS = 1
val VIDEO_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class ItemActivity : AppCompatActivity(), PlayFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {

    }

    var url = ""
    private val FRAGMENT_DIALOG = "dialog"
    val REQUEST_CAMERA = 1
    private val TAG = "ItemActivity"
    var tr_id: String? = null
    var item_word:String? = null
    var section:String? = null
    var video:String? = null
    var videoPath:String? = null
    var category: Int = 0
    var context: Context = this
    fun setAutoOrientationEnabled(context: Context, enabled: Boolean) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, if (enabled) 1 else 0)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScaleConfig.create(this,
                1080, // Design Width
                1920, // Design Height
                (3).toFloat(),    // Design Density
                (3).toFloat(),    // Design FontScale
                ScaleConfig.DIMENS_UNIT_DP);
        setContentView(R.layout.activity_item)
        if (!hasPermissionsGranted(VIDEO_PERMISSIONS)) {
            for(i:Int in 0..VIDEO_PERMISSIONS.size)
            requestVideoPermissions(VIDEO_PERMISSIONS[i]);
        }
        url = intent.getStringExtra("url")
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, PlayFragment.newInstance(url)).commit()
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestVideoPermissions(permission:String) {
        if (shouldShowRequestPermissionRationale(permission)) {
            ConfirmationDialog().show(this.getSupportFragmentManager(), FRAGMENT_DIALOG)
        } else {
            ActivityCompat.requestPermissions(this, VIDEO_PERMISSIONS, REQUEST_VIDEO_PERMISSIONS)
        }
    }


    private fun hasPermissionsGranted(permissions:Array<String>):Boolean {
        for (permission : String in permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.d(TAG, "onRequestPermissionsResult");
        if (requestCode == REQUEST_VIDEO_PERMISSIONS) {
            if (grantResults.size == VIDEO_PERMISSIONS.size) {
                for (result:Int in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        PlayFragment.ErrorDialog().newInstance(getString(R.string.permission_request_camera))
                                .show(supportFragmentManager, FRAGMENT_DIALOG)
                        break;
                    }
                }
            } else {
                PlayFragment.ErrorDialog().newInstance(getString(R.string.permission_request_camera))
                        .show(supportFragmentManager, FRAGMENT_DIALOG)
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
    fun setCameraDisplayOrientation(activity: Activity, cameraId: Int, camera: Camera) {
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        val rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation()
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        var result: Int
        if (info.facing === Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360
        }
        camera.setDisplayOrientation(result)
    }

    override fun onBackPressed() {
        // Otherwise defer to system default behavior.
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
class ConfirmationDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState:Bundle?): Dialog {
        val parent = getParentFragment();
        return AlertDialog.Builder(getActivity()!!)
                .setMessage(R.string.permission_request)
                .setPositiveButton(android.R.string.ok, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        requestPermissions(VIDEO_PERMISSIONS,
                                REQUEST_VIDEO_PERMISSIONS);
                    }
                })
                .setNegativeButton(android.R.string.cancel,
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                parent!!.getActivity()!!.finish();
                            }
                        })
                .create();
    }

}
