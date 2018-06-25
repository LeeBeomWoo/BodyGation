package bodygate.bcns.bodygation

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
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
import bodygate.bcns.bodygation.PlayFragment.OnFragmentInteractionListener
import bodygate.bcns.bodygation.support.PlayModel
import bodygate.bcns.bodygation.support.PlayModelFactory
import cn.gavinliu.android.lib.scale.config.ScaleConfig
import kotlinx.android.synthetic.main.fragment_play.*


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class ItemActivity : AppCompatActivity(), OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {

    }
    companion object {
        private const val SELECTED_LOCATION_INDEX = "selectedLocationIndex"
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
        if (savedInstanceState != null) {
            val index = savedInstanceState.getString("url")
            url = index
        }else{
            url = intent.getStringExtra("url")
        }
      //  val factory = PlayModelFactory(url)
      //  val model = ViewModelProviders.of(PlayFragment.newInstance(url), factory).get(PlayModel::class.java)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, PlayFragment.newInstance(url)).commit()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("url", url)
    }
    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        Log.i(TAG, "onConfigurationChanged : " + newConfig!!.orientation.toString())}
    override fun setCameraDisplayOrientation(activity: Activity, cameraId: Int, camera: Camera) {
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
        Log.i(TAG, "onConfigurationChanged_setCameraDisplayOrientation : " + result.toString())
    }

    override fun onBackPressed() {
        // Otherwise defer to system default behavior.
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
