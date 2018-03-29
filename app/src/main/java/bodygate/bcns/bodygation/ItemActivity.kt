package bodygate.bcns.bodygation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.Surface


class ItemActivity : AppCompatActivity() {
    var url = ""
    val REQUEST_CAMERA = 1
    private val TAG = "ItemActivity"
    var tr_id: String? = null
    var item_word:String? = null
    var section:String? = null
    var video:String? = null
    var videoPath:String? = null
    var category: Int = 0
    var page_num:Int = 0
    var context: Context = this
    fun setAutoOrientationEnabled(context: Context, enabled: Boolean) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, if (enabled) 1 else 0)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        val intent = intent
        url = intent.getStringExtra("url")
        if (savedInstanceState == null) {
            follow.setArguments(getIntent().extras)
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, follow, "your_fragment").commit()
        } else {
            val follow = supportFragmentManager.findFragmentByTag("your_fragment_21") as PlayFragment

        }
        if (android.provider.Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) != 1){
           setAutoOrientationEnabled(this, true);
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
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
