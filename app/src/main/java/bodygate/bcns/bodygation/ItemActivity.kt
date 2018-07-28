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
import android.database.Cursor
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
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
import java.io.File


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class ItemActivity : AppCompatActivity(), OnFragmentInteractionListener {
    var url = ""
    private val TAG = "ItemActivity"
    var section:String? = null
    var category: Int = 0
    override val context: Context = this
    var playFragment:PlayFragment? = null
   override var youtubeprogress:Int = 0
    override var youtubePlaying:Boolean = false
    override var videoprogress:Int = 0
    override var videoPlaying:Boolean = false
    override var video_camera:Boolean = false //false = camera, true = video
    override var videoPath:String = ""
    lateinit var dataclass:DataClass

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
        Log.i(TAG, "onCreate")
        dataclass = (intent.getParcelableExtra("EXTRA_SESSION_ID"))
        if (savedInstanceState != null) {
            url = savedInstanceState.getString("url")
            youtubeprogress = savedInstanceState.getInt("progress")
            video_camera = savedInstanceState.getBoolean("playyoutube")
            if(video_camera){
                videoPath = savedInstanceState.getString("videoPath")
                videoPlaying = savedInstanceState.getBoolean("videoPlaying")
            }
            Log.i(TAG, "onCreate_" + "url :" + url + "\t progress :" + youtubeprogress.toString() + "\t playyoutube : " + youtubePlaying.toString())
        }else{
            url = intent.getStringExtra("url")
        }
            playFragment = supportFragmentManager.findFragmentByTag("play") as PlayFragment?
            if(playFragment == null) {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, PlayFragment.newInstance(url), "play").commit()
            }else{
            supportFragmentManager
                    .beginTransaction().replace(R.id.fragment_container, playFragment!!).commit()
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("url", url)
        outState.putInt("progress", youtubeprogress)
        outState.putBoolean("playyoutube", video_camera)
        outState.putString("videoPath", videoPath)
        outState.putBoolean("videoPlaying", videoPlaying)
    }
    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        Log.i(TAG, "onConfigurationChanged : " + newConfig!!.toString())
        Log.i(TAG, "onConfigurationChanged orientation: " + newConfig.orientation.toString())}
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
        intent.putExtra("EXTRA_SESSION_ID", dataclass)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
