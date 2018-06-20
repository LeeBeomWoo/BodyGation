package bodygate.bcns.bodygation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.*
import android.net.Uri
import android.support.v4.app.Fragment
import android.widget.SeekBar
import cn.gavinliu.android.lib.scale.ScaleRelativeLayout
import cn.gavinliu.android.lib.scale.ScaleFrameLayout
import android.widget.ImageButton
import android.util.SparseIntArray
import android.hardware.Camera
import android.hardware.camera2.*
import android.hardware.camera2.CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
import android.hardware.camera2.CameraCharacteristics.SENSOR_ORIENTATION
import android.hardware.camera2.CameraDevice.TEMPLATE_PREVIEW
import android.hardware.camera2.CameraDevice.TEMPLATE_RECORD
import android.media.ImageReader
import android.view.*
import android.media.MediaPlayer
import android.support.annotation.NonNull
import android.media.MediaRecorder
import android.os.*
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v4.content.ContextCompat.getDrawable
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import bodygate.bcns.bodygation.camerause.*
import kotlinx.android.synthetic.main.fragment_play.*
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PlayFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [PlayFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class PlayFragment : Fragment(), View.OnClickListener, SeekBar.OnSeekBarChangeListener, ActivityCompat.OnRequestPermissionsResultCallback {
    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }

    val REQUEST_VIDEO_PERMISSIONS = 1
    val VIDEO_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val ARG_PARAM1 = "url"
    private var param1: String? = null
    var viewalpha:Int = 0
    private var listener: PlayFragment.OnFragmentInteractionListener? = null
    private val SENSOR_ORIENTATION_DEFAULT_DEGREES = 90
    private val SENSOR_ORIENTATION_INVERSE_DEGREES = 270
    private val FURL = "<html><body><iframe width=\"1280\" height=\"720\" src=\""
    private val BURL = "\" frameborder=\"0\" allowfullscreen></iframe></html></body>"
    private val CHANGE = "https://www.youtube.com/embed/"
    private val TAG = "Item_follow_fragment_21"
    var baseDir = ""
    //ScaleRelativeLayout button_layout;
//ScaleFrameLayout cameraLayout;
    var page_num: Int = 0
    var LandButton: ScaleRelativeLayout.LayoutParams? = null
    var LandCamera:ScaleRelativeLayout.LayoutParams? = null
    var LandWebView:ScaleRelativeLayout.LayoutParams? = null
    var playlayout:ScaleRelativeLayout.LayoutParams? = null
    var recordlayout:ScaleRelativeLayout.LayoutParams? = null
    var switchlayout:ScaleRelativeLayout.LayoutParams? = null
    var loadlayout:ScaleRelativeLayout.LayoutParams? = null
    var play_recordlayout:ScaleRelativeLayout.LayoutParams? = null
    var play_record: Boolean? = true //true 가 촬영모드, false 가 재생모드
    val CAMERA_FRONT = "1"
    val CAMERA_BACK = "0"
    var change: String? = null
    var temp:String? = null
    var videoString:String? = null
    var videopath: Uri? = null

    private lateinit var cameraId: String

    /**
     * An [AutoFitTextureView] for camera preview.
     */
    /**
     * A [CameraCaptureSession] for camera preview.
     */
    private var captureSession: CameraCaptureSession? = null

    /**
     * A reference to the opened [CameraDevice].
     */
    private var cameraDevice: CameraDevice? = null

    /**
     * The [android.util.Size] of camera preview.
     */
    private lateinit var previewSize: Size
    /**
     * The [android.util.Size] of video recording.
     */
    private lateinit var videoSize: Size
    /**
     * Whether the app is recording video now
     */
    private var isRecordingVideo = false



    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private var backgroundThread: HandlerThread? = null

    /**
     * A [Handler] for running tasks in the background.
     */
    private var backgroundHandler: Handler? = null

    /**
     * An [ImageReader] that handles still image capture.
     */
    private var imageReader: ImageReader? = null

    /**
     * This is the output file for our picture.
     */
    private lateinit var file: File
    private var nextVideoAbsolutePath: String? = null

    private var mediaRecorder: MediaRecorder? = null

    var tr_id: String? = null
    var imageUrl:String? = null
    var tr_password:String? = null
    var URL:String? = null
    private var mSensorOrientation: Int? = null
    private var mNextVideoAbsolutePath: String? = null
    private var mPreviewBuilder: CaptureRequest.Builder? = null
    private val FRAGMENT_DIALOG = "dialog"
    private val DEFAULT_ORIENTATIONS = SparseIntArray().apply {
        append(Surface.ROTATION_0, 90)
        append(Surface.ROTATION_90, 0)
        append(Surface.ROTATION_180, 270)
        append(Surface.ROTATION_270, 180)
    }
    private val INVERSE_ORIENTATIONS = SparseIntArray().apply {
        append(Surface.ROTATION_0, 270)
        append(Surface.ROTATION_90, 180)
        append(Surface.ROTATION_180, 90)
        append(Surface.ROTATION_270, 0)
    }
    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
            openCamera(width, height)
        }

        override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture, width: Int, height: Int) {
            configureTransform(width, height)
        }

        override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture) = true

        override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) = Unit

    }


    private val stateCallback = object : CameraDevice.StateCallback() {

        override fun onOpened(cameraDevice: CameraDevice) {
            cameraOpenCloseLock.release()
            this@PlayFragment.cameraDevice = cameraDevice
            startPreview()
            configureTransform(AutoView.width, AutoView.height)
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            cameraOpenCloseLock.release()
            cameraDevice.close()
            this@PlayFragment.cameraDevice = null
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            cameraOpenCloseLock.release()
            cameraDevice.close()
            this@PlayFragment.cameraDevice = null
            activity?.finish()
        }


    }
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        Thread(object:Runnable {
            override fun run() {
                // 현재 UI 스레드가 아니기 때문에 메시지 큐에 Runnable을 등록 함
                getActivity()!!.runOnUiThread(object:Runnable {
                    override fun run() {
                        // 메시지 큐에 저장될 메시지의 내용;
                        val a = progress / 100.0
                        val b = a.toFloat()
                        youtube_layout.setAlpha(b)
                    }
                })
            }
        }).start()

    }

    fun defaultOrientation()
    {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    fun inverseOrientation()
    {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    private val onImageAvailableListener = ImageReader.OnImageAvailableListener {
        backgroundHandler?.post(ImageSaver(it.acquireNextImage(), file))
    }

    /**
     * [CaptureRequest.Builder] for the camera preview
     */
    private lateinit var previewRequestBuilder: CaptureRequest.Builder

    /**
     * [CaptureRequest] generated by [.previewRequestBuilder]
     */
    private lateinit var previewRequest: CaptureRequest

    /**
     * The current state of camera state for taking pictures.
     *
     * @see .captureCallback
     */
    private var state = STATE_PREVIEW

    /**
     * A [Semaphore] to prevent the app from exiting before closing the camera.
     */
    private val cameraOpenCloseLock = Semaphore(1)

    /**
     * Whether the current camera device supports Flash or not.
     */
    private var flashSupported = false

    /**
     * Orientation of the camera sensor
     */
    private var sensorOrientation = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        if(savedInstanceState != null){
            param1 = savedInstanceState.getString("url")
            alpha_control.progress = savedInstanceState.getInt("alpha")
        }else {
            arguments?.let {
                param1 = it.getString(ARG_PARAM1)
            }
        }

        val state = Environment.getExternalStorageState()
        if ( Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) ) {  // we can read the External Storage...
            //Retrieve the primary External Storage:
            baseDir = Environment.getExternalStoragePublicDirectory("DIRECTORY_MOVIES").path
        }else{
            baseDir = Environment.DIRECTORY_MOVIES
        }
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        startBackgroundThread()

        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (AutoView.isAvailable) {
            openCamera(AutoView.width, AutoView.height)
        } else {
            AutoView.surfaceTextureListener = surfaceTextureListener
        }
        youtube_layout.resumeTimers()
    }
   override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
       closeCamera()
       stopBackgroundThread()
        youtube_layout.pauseTimers()
    }
    private fun requestCameraPermission() {
        for(s:String in VIDEO_PERMISSIONS) {
            if (shouldShowRequestPermissionRationale(s)) {
                ConfirmationDialog().show(childFragmentManager, FRAGMENT_DIALOG)
            }
        }
        requestPermissions(VIDEO_PERMISSIONS, REQUEST_VIDEO_PERMISSIONS)
    }
    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.i(TAG, "onActivityCreated")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onConfigurationChanged(getActivity()!!.getResources().getConfiguration())
        startBackgroundThread()
        ButtonImageSetUp()
        viewSet()
        alpha_control.setMax(100)
        youtube_layout.setWebChromeClient(WebChromeClient())
        youtube_layout.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND)
        youtube_layout.setWebViewClient(WebViewClient())
        val settings = youtube_layout.getSettings()
        settings.setJavaScriptEnabled(true)
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN)
        settings.setJavaScriptCanOpenWindowsAutomatically(true)
        settings.setPluginState(WebSettings.PluginState.ON)
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK)
        youtube_layout.getSettings().setSupportMultipleWindows(true)
        settings.setLoadWithOverviewMode(true)
        settings.setUseWideViewPort(true)
        URL = FURL + CHANGE + param1 + BURL;
        Log.d(TAG, "temp : " + temp + "," + "tr_id : " + tr_id )
        youtube_layout.loadData(URL, "text/html", "charset=utf-8")
        alpha_control.setOnSeekBarChangeListener(this)
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_VIDEO_PERMISSIONS) {
            if (grantResults.size == VIDEO_PERMISSIONS.size) {
                for (result in grantResults) {
                    if (result != PERMISSION_GRANTED) {
                        ErrorDialog.newInstance(getString(R.string.permission_request))
                                .show(childFragmentManager, FRAGMENT_DIALOG)
                        break
                    }
                }
            } else {
                ErrorDialog.newInstance(getString(R.string.permission_request))
                        .show(childFragmentManager, FRAGMENT_DIALOG)
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
    private fun hasPermissionsGranted(permissions: Array<String>) =
            permissions.none {
                checkSelfPermission((activity as FragmentActivity), it) != PERMISSION_GRANTED
            }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.i(TAG, "onCreateView")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play, container, false)
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        Log.i(TAG, "onAttach")
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach")
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        fun setCameraDisplayOrientation(activity: Activity, cameraId: Int, camera: Camera)
        fun onFragmentInteraction(uri: Uri)
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
            super.onConfigurationChanged(newConfig);
        Log.i(TAG, "onConfigurationChanged")
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            PortrainSet()
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            LandSet()
        }
        if (AutoView != null && AutoView!!.isAvailable()) {
            configureTransform(AutoView!!.getWidth(), AutoView!!.getHeight())
        }
    }
    private fun LandSet(){
        LandWebView = ScaleRelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LandButton = ScaleRelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.btnlayoutSiz_item), ViewGroup.LayoutParams.MATCH_PARENT);
        LandCamera = ScaleRelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        playlayout = ScaleRelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.imageBtnsize_item), getResources().getDimensionPixelSize(R.dimen.imageBtnsize_item));
        recordlayout = ScaleRelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.imageBtnsize_item), getResources().getDimensionPixelSize(R.dimen.imageBtnsize_item));
        switchlayout = ScaleRelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.imageBtnsize_item), getResources().getDimensionPixelSize(R.dimen.imageBtnsize_item));
        play_recordlayout = ScaleRelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.imageBtnsize_item), getResources().getDimensionPixelSize(R.dimen.imageBtnsize_item));
        loadlayout = ScaleRelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.imageBtnsize_item), getResources().getDimensionPixelSize(R.dimen.imageBtnsize_item));
        val seek = ScaleRelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LandButton!!.addRule(ScaleRelativeLayout.ALIGN_PARENT_TOP);
        //LandButton.addRule(ScaleRelativeLayout.ALIGN_PARENT_START);
        button_layout.setLayoutParams(LandButton);
        playlayout!!.addRule(ScaleRelativeLayout.ALIGN_PARENT_TOP);
        playlayout!!.setMargins(getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item));
        play_Btn.setLayoutParams(playlayout);
        recordlayout!!.setMargins(getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item));
        recordlayout!!.addRule(ScaleRelativeLayout.ALIGN_PARENT_BOTTOM);
        record_Btn.setLayoutParams(recordlayout);
        loadlayout!!.addRule(ScaleRelativeLayout.BELOW, R.id.play_Btn);
        loadlayout!!.setMargins(getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item));
        load_Btn.setLayoutParams(loadlayout);
        play_recordlayout!!.addRule(ScaleRelativeLayout.CENTER_VERTICAL);
        play_recordlayout!!.setMargins(getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item));
        play_record_Btn.setLayoutParams(play_recordlayout);
        switchlayout!!.addRule(ScaleRelativeLayout.ABOVE, R.id.record_Btn);
        switchlayout!!.setMargins(getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item));
        viewChange_Btn.setLayoutParams(switchlayout);
        LandCamera!!.addRule(ScaleRelativeLayout.END_OF, R.id.button_layout);
        LandCamera!!.addRule(ScaleRelativeLayout.ALIGN_PARENT_BOTTOM);
        video_layout.setLayoutParams(LandCamera);
        LandWebView!!.addRule(ScaleRelativeLayout.ALIGN_PARENT_END);
        LandWebView!!.addRule(ScaleRelativeLayout.BELOW, R.id.alpha_control);
        LandWebView!!.addRule(ScaleRelativeLayout.END_OF, R.id.button_layout);
        youtube_layout.setLayoutParams(LandWebView);
        seek.addRule(ScaleRelativeLayout.ALIGN_PARENT_END);
        seek.addRule(ScaleRelativeLayout.END_OF, R.id.button_layout);
        alpha_control.setLayoutParams(seek);
        alpha_control.setProgress(50);
        alpha_control.setVisibility(View.VISIBLE);
        alpha_control.setZ(2.toFloat());
        youtube_layout.setAlpha((0.5).toFloat());
        youtube_layout.setZ(2.toFloat());
        video_layout.setZ(0.toFloat());
        AutoView.setZ(0.toFloat());
        video_View.setLayoutParams(ScaleFrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        AutoView.setLayoutParams(ScaleRelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        video_View.setZ(1.toFloat());
    }
    private fun PortrainSet(){
        LandWebView = ScaleRelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.videoviewSiz_item));
        LandButton = ScaleRelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LandCamera = ScaleRelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        playlayout = ScaleRelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.portlaneimageBtnsize_item), getResources().getDimensionPixelSize(R.dimen.portlaneimageBtnsize_item));
        recordlayout = ScaleRelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.portlaneimageBtnsize_item), getResources().getDimensionPixelSize(R.dimen.portlaneimageBtnsize_item));
        switchlayout = ScaleRelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.portlaneimageBtnsize_item), getResources().getDimensionPixelSize(R.dimen.portlaneimageBtnsize_item));
        play_recordlayout = ScaleRelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.portlaneimageBtnsize_item), getResources().getDimensionPixelSize(R.dimen.portlaneimageBtnsize_item));
        loadlayout = ScaleRelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.portlaneimageBtnsize_item), getResources().getDimensionPixelSize(R.dimen.portlaneimageBtnsize_item));
        LandWebView!!.addRule(ScaleRelativeLayout.ALIGN_PARENT_START);
        LandWebView!!.addRule(ScaleRelativeLayout.ALIGN_PARENT_END);
        LandWebView!!.addRule(ScaleRelativeLayout.ALIGN_PARENT_BOTTOM);
        youtube_layout.setLayoutParams(LandWebView);
        LandButton!!.addRule(ScaleRelativeLayout.ABOVE, R.id.youtube_layout);
        LandButton!!.addRule(ScaleRelativeLayout.ALIGN_PARENT_END);
        button_layout.setLayoutParams(LandButton);
        playlayout!!.addRule(ScaleRelativeLayout.ALIGN_PARENT_BOTTOM);
        playlayout!!.setMargins(getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item));
        play_Btn.setLayoutParams(playlayout);
        recordlayout!!.setMargins(getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item));
        recordlayout!!.addRule(ScaleRelativeLayout.ALIGN_PARENT_TOP);
        record_Btn.setLayoutParams(recordlayout);
        loadlayout!!.addRule(ScaleRelativeLayout.ABOVE, R.id.play_Btn);
        loadlayout!!.setMargins(getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item));
        load_Btn.setLayoutParams(loadlayout);
        play_recordlayout!!.addRule(ScaleRelativeLayout.CENTER_VERTICAL);
        play_recordlayout!!.setMargins(getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item));
        play_record_Btn.setLayoutParams(play_recordlayout);
        switchlayout!!.addRule(ScaleRelativeLayout.BELOW, R.id.record_Btn);
        switchlayout!!.setMargins(getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item));
        viewChange_Btn.setLayoutParams(switchlayout);
        LandCamera!!.addRule(ScaleRelativeLayout.ALIGN_PARENT_START);
        LandCamera!!.addRule(ScaleRelativeLayout.ALIGN_PARENT_TOP);
        LandCamera!!.addRule(ScaleRelativeLayout.START_OF, R.id.button_layout);
        LandCamera!!.addRule(ScaleRelativeLayout.ABOVE, R.id.youtube_layout);
        video_layout.setLayoutParams(LandCamera);
        alpha_control.setVisibility(View.GONE);
        video_View.setLayoutParams(ScaleFrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        AutoView.setLayoutParams(ScaleRelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        youtube_layout.setAlpha((1).toFloat());
    }
    private fun viewSet(){
        record_Btn.setOnClickListener(this);
        load_Btn.setOnClickListener(this);
        play_Btn.setOnClickListener(this);
        play_record_Btn.setOnClickListener(this);
        viewChange_Btn.setOnClickListener(this);
        video_View.setOnCompletionListener(object : MediaPlayer.OnCompletionListener {
            override fun onCompletion(mp: MediaPlayer?) {
                play_Btn.setImageResource(R.drawable.play);
            }
        });
         if(getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
            openCamera(AutoView.getWidth(), AutoView.getHeight());
            youtube_layout.setAlpha((1).toFloat());
        }else {
            openCamera(AutoView.getHeight(), AutoView.getWidth());
             alpha_control.setProgress(50);
             youtube_layout.setAlpha((0.5).toFloat());
        }

    }
    private fun ButtonImageSetUp(){
        if(video_View.isPlaying()){
            play_Btn.setImageResource(R.drawable.pause);
        }else{
            play_Btn.setImageResource(R.drawable.play);
        }
        if(isRecordingVideo){
            record_Btn.setImageResource(R.drawable.stop);
        }else {
            record_Btn.setImageResource(R.drawable.record);
        }
    }

    @SuppressLint("MissingPermission")
    private fun openCamera(width: Int, height: Int) {
        if (!hasPermissionsGranted(VIDEO_PERMISSIONS)) {
            requestCameraPermission()
            return
        }

        val cameraActivity = activity
        if (cameraActivity == null || cameraActivity.isFinishing) return

        val manager = cameraActivity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock camera opening.")
            }
            val cameraId = manager.cameraIdList[0]

            // Choose the sizes for camera preview and video recording
            val characteristics = manager.getCameraCharacteristics(cameraId)
            val map = characteristics.get(SCALER_STREAM_CONFIGURATION_MAP) ?:
            throw RuntimeException("Cannot get available preview/video sizes")
            sensorOrientation = characteristics.get(SENSOR_ORIENTATION)
            videoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder::class.java))
            previewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture::class.java),
                    width, height, videoSize)

            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                AutoView.setAspectRatio(previewSize.width, previewSize.height)
            } else {
                AutoView.setAspectRatio(previewSize.height, previewSize.width)
            }
            configureTransform(width, height)
            mediaRecorder = MediaRecorder()
            manager.openCamera(cameraId, stateCallback, null)
        } catch (e: CameraAccessException) {
            showToast("Cannot access the camera.")
            cameraActivity.finish()
        } catch (e: NullPointerException) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            ErrorDialog.newInstance(getString(R.string.camera_error))
                    .show(childFragmentManager, FRAGMENT_DIALOG)
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera opening.")
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult");
        Log.d("requestCode", requestCode.toString());
        Log.d("resultCode", resultCode.toString());
        //if (resultCode != RESULT_OK)
        if (requestCode == 2 && data != null) {
            val mVideoURI = data.getData();
            videopath = mVideoURI;
            videoString = videopath.toString();
            Log.d("onActivityResult", mVideoURI.toString());
            Log.d("Result videoString", videoString);
            //Log.d("getRealPathFromURI", getRealPathFromURI(getContext(), mVideoURI));
            video_ViewSetup(mVideoURI);
        }
    }

    private fun video_ViewSetup(path: Uri) {
        video_View.setVideoURI(path)
    }

    fun switchCamera() {
        if (cameraId.equals(CAMERA_FRONT)) {
            cameraId = CAMERA_BACK;
            closeCamera()
            openCamera(AutoView.width, AutoView.height)

        } else if (cameraId.equals(CAMERA_BACK)) {
            cameraId = CAMERA_FRONT;
            closeCamera()
            openCamera(AutoView.width, AutoView.height)
        }
    }
    /**
     * Closes the current [CameraDevice].
     */
    private fun closeCamera() {
        try {
            cameraOpenCloseLock.acquire()
            captureSession?.close()
            captureSession = null
            cameraDevice?.close()
            cameraDevice = null
            mediaRecorder?.release()
            mediaRecorder = null
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera closing.", e)
        } finally {
            cameraOpenCloseLock.release()
        }
    }
    private fun startPreview() {
        if (cameraDevice == null || !AutoView.isAvailable) return

        try {
            closePreviewSession()
            val texture = AutoView.surfaceTexture
            texture.setDefaultBufferSize(previewSize.width, previewSize.height)
            previewRequestBuilder = cameraDevice!!.createCaptureRequest(TEMPLATE_PREVIEW)

            val previewSurface = Surface(texture)
            previewRequestBuilder.addTarget(previewSurface)

            cameraDevice?.createCaptureSession(listOf(previewSurface),
                    object : CameraCaptureSession.StateCallback() {

                        override fun onConfigured(session: CameraCaptureSession) {
                            captureSession = session
                            updatePreview()
                        }

                        override fun onConfigureFailed(session: CameraCaptureSession) {
                            if (activity != null) showToast("Failed")
                        }
                    }, backgroundHandler)
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        }

    }
    /**
     * Update the camera preview. [startPreview] needs to be called in advance.
     */
    private fun updatePreview() {
        if (cameraDevice == null) return

        try {
            setUpCaptureRequestBuilder(previewRequestBuilder)
            HandlerThread("CameraPreview").start()
            captureSession?.setRepeatingRequest(previewRequestBuilder.build(),
                    null, backgroundHandler)
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        }

    }

    private fun setUpCaptureRequestBuilder(builder: CaptureRequest.Builder?) {
        builder?.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
    }

    /**
     * Starts a background thread and its [Handler].
     */
    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground").also { it.start() }
        backgroundHandler = Handler(backgroundThread?.looper)
    }

    /**
     * Stops the background thread and its [Handler].
     */
    private fun stopBackgroundThread() {
        backgroundThread?.quitSafely()
        try {
            backgroundThread?.join()
            backgroundThread = null
            backgroundHandler = null
        } catch (e: InterruptedException) {
            Log.e(TAG, e.toString())
        }

    }
    @Throws(IOException::class)
    private fun setUpMediaRecorder() {
        val cameraActivity = activity ?: return

        if (nextVideoAbsolutePath.isNullOrEmpty()) {
            nextVideoAbsolutePath = getVideoFilePath()
        }

        val rotation = cameraActivity.windowManager.defaultDisplay.rotation
        when (sensorOrientation) {
            SENSOR_ORIENTATION_DEFAULT_DEGREES ->
                mediaRecorder?.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation))
            SENSOR_ORIENTATION_INVERSE_DEGREES ->
                mediaRecorder?.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation))
        }

        mediaRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(nextVideoAbsolutePath)
            setVideoEncodingBitRate(10000000)
            setVideoFrameRate(30)
            setVideoSize(videoSize.width, videoSize.height)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            prepare()
        }
    }
    private fun startRecordingVideo() {
        if (cameraDevice == null || !AutoView.isAvailable) return

        try {
            closePreviewSession()
            setUpMediaRecorder()
            val texture = AutoView.surfaceTexture.apply {
                setDefaultBufferSize(previewSize.width, previewSize.height)
            }

            // Set up Surface for camera preview and MediaRecorder
            val previewSurface = Surface(texture)
            val recorderSurface = mediaRecorder!!.surface
            val surfaces = ArrayList<Surface>().apply {
                add(previewSurface)
                add(recorderSurface)
            }
            previewRequestBuilder = cameraDevice!!.createCaptureRequest(TEMPLATE_RECORD).apply {
                addTarget(previewSurface)
                addTarget(recorderSurface)
            }

            // Start a capture session
            // Once the session starts, we can update the UI and start recording
            cameraDevice?.createCaptureSession(surfaces,
                    object : CameraCaptureSession.StateCallback() {

                        override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                            captureSession = cameraCaptureSession
                            updatePreview()
                            activity?.runOnUiThread {
                                record_Btn.setImageDrawable(getDrawable(this@PlayFragment.requireActivity(), R.drawable.stop))
                                isRecordingVideo = true
                                mediaRecorder?.start()
                            }
                        }

                        override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                            if (activity != null) showToast("Failed")
                        }
                    }, backgroundHandler)
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
        }

    }
    private fun showToast(message : String) = Toast.makeText(activity, message, LENGTH_SHORT).show()

    private fun closePreviewSession() {
        captureSession?.close()
        captureSession = null
    }

    private fun stopRecordingVideo() {
        isRecordingVideo = false
        record_Btn.setImageDrawable(getDrawable(this.requireActivity(), R.drawable.record))
        mediaRecorder?.apply {
            stop()
            reset()
        }

        if (activity != null) showToast("Video saved: $nextVideoAbsolutePath")
        nextVideoAbsolutePath = null
        startPreview()
    }
    /**
     * In this sample, we choose a video size with 3x4 aspect ratio. Also, we don't use sizes
     * larger than 1080p, since MediaRecorder cannot handle such a high-resolution video.
     *
     * @param choices The list of available sizes
     * @return The video size
     */
    private fun chooseVideoSize(choices: Array<Size>) = choices.firstOrNull {
        it.width == it.height * 4 / 3 && it.width <= 1080 } ?: choices[choices.size - 1]
    /**
     * Given [choices] of [Size]s supported by a camera, chooses the smallest one whose
     * width and height are at least as large as the respective requested values, and whose aspect
     * ratio matches with the specified value.
     *
     * @param choices     The list of sizes that the camera supports for the intended output class
     * @param width       The minimum desired width
     * @param height      The minimum desired height
     * @param aspectRatio The aspect ratio
     * @return The optimal [Size], or an arbitrary one if none were big enough
     */
    private fun chooseOptimalSize(
            choices: Array<Size>,
            width: Int,
            height: Int,
            aspectRatio: Size
    ): Size {

        // Collect the supported resolutions that are at least as big as the preview Surface
        val w = aspectRatio.width
        val h = aspectRatio.height
        val bigEnough = choices.filter {
            it.height == it.width * h / w && it.width >= width && it.height >= height }

        // Pick the smallest of those, assuming we found any
        return if (bigEnough.isNotEmpty()) {
            Collections.min(bigEnough, CompareSizesByArea())
        } else {
            choices[0]
        }
    }
    /**
     * Configures the necessary [android.graphics.Matrix] transformation to `AutoView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `AutoView` is fixed.
     *
     * @param viewWidth  The width of `AutoView`
     * @param viewHeight The height of `AutoView`
     */
    private fun configureTransform(viewWidth: Int, viewHeight: Int) {
        activity ?: return
        val rotation = activity!!.windowManager.defaultDisplay.rotation
        val matrix = Matrix()
        val viewRect = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect = RectF(0f, 0f, previewSize.height.toFloat(), previewSize.width.toFloat())
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()

        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            val scale = Math.max(
                    viewHeight.toFloat() / previewSize.height,
                    viewWidth.toFloat() / previewSize.width)
            with(matrix) {
                setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
                postScale(scale, scale, centerX, centerY)
                postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
            }
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180f, centerX, centerY)
        }
        AutoView.setTransform(matrix)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        viewalpha = alpha_control.progress
        outState.putString("url", param1)
        outState.putInt("alpha", viewalpha)
        super.onSaveInstanceState(outState)
    }

    @NonNull
    private fun getVideoFilePath() :String{
        val state = Environment.getExternalStorageState()
        var myDir = ""
        if (ContextCompat.checkSelfPermission(this.requireActivity(), // request permission when it is not granted.
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("myAppName", "permission:WRITE_EXTERNAL_STORAGE: NOT granted!")
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this.requireActivity(),
                        VIDEO_PERMISSIONS,1
                        )
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        if ( Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) ) {  // we can read the External Storage...

        myDir = baseDir + File.separator + "bodygation" + File.separator
        if(!File(myDir).exists()) {
            val sciezka = File(myDir)
            sciezka.mkdirs()
        }
}else {
            myDir = baseDir
            //Retrieve the External Storages root directory:

            myDir += File.separator + "bodygation" + File.separator
            if(!File(myDir).exists()) {
                val sciezka = File(myDir)
                sciezka.mkdirs()
            }
        }
        Log.i("Path", myDir + "bodygation_" + System.currentTimeMillis() + ".mp4")
        return myDir + "bodygation_" + System.currentTimeMillis() + ".mp4"
    }

    override fun onClick(v:View) {
        when (v.getId()) {
            R.id.record_Btn ->//녹화
            {
                Log.d(TAG, "record_Btn thouch");
            if (AutoView.getVisibility() == View.INVISIBLE) {
                AutoView.setVisibility(View.VISIBLE)
            }
                if (AutoView != null) {
                if (isRecordingVideo) {
                    stopRecordingVideo()
                } else {
                    video_View.setVisibility(View.INVISIBLE)
                    AutoView.setVisibility(View.VISIBLE)
                    startRecordingVideo()
                }
            }
        }
            R.id.play_Btn//재생
                ->{
                Log.d(TAG, "play_Btn thouch");
                if(video_View.isPlaying()){
                    video_View.pause();
                    play_record_Btn.setImageResource(R.drawable.play)
                }else {
                    video_View.start();
                    play_record_Btn.setImageResource(R.drawable.pause)
                    AutoView.setVisibility(View.INVISIBLE)
                    video_View.setVisibility(View.VISIBLE)
                }
                }

            R.id.load_Btn//파일불러오기
                    -> {
                videoString = null;
                videopath = null;
                val intent = Intent(Intent.ACTION_GET_CONTENT);
                val uri = Uri . parse (Environment.getExternalStoragePublicDirectory("DIRECTORY_MOVIES").getPath()+ File.separator + "bodygation" + File.separator);
                intent.setType("video/mp4");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivityForResult(Intent.createChooser(intent, "Select Video"), 2)
            }
            R.id.play_record_Btn//파일과 카메라간 변환
                -> {
                if (play_record!!) {
                    if (isRecordingVideo) {
                        stopRecordingVideo()
                    }
                    closeCamera();
                    AutoView.setVisibility(View.INVISIBLE)
                    video_View.setVisibility(View.VISIBLE)
                    play_record = false;
                } else {
                    if (video_View.isPlaying()) {
                        video_View.stopPlayback();
                    }
                    if (getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
                        openCamera(AutoView.getWidth(), AutoView.getHeight());
                    } else {
                        openCamera(AutoView.getHeight(), AutoView.getWidth());
                    }
                    AutoView.setVisibility(View.VISIBLE);
                    video_View.setVisibility(View.INVISIBLE);
                    play_record = true;
                }
                ButtonImageSetUp();
            }
             R.id.viewChange_Btn//전후면 카메라변환
               -> {
                   Log.d(TAG, "viewChange_Btn thouch");
                   switchCamera();
               }
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlayFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
                PlayFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                    }
                }


        /**
         * Conversion from screen rotation to JPEG orientation.
         */
        private val ORIENTATIONS = SparseIntArray()
        private val FRAGMENT_DIALOG = "dialog"

        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }

        /**
         * Tag for the [Log].
         */
        private val TAG = "Camera2BasicFragment"

        /**
         * Camera state: Showing camera preview.
         */
        private val STATE_PREVIEW = 0

        /**
         * Camera state: Waiting for the focus to be locked.
         */
        private val STATE_WAITING_LOCK = 1

        /**
         * Camera state: Waiting for the exposure to be precapture state.
         */
        private val STATE_WAITING_PRECAPTURE = 2

        /**
         * Camera state: Waiting for the exposure state to be something other than precapture.
         */
        private val STATE_WAITING_NON_PRECAPTURE = 3

        /**
         * Camera state: Picture was taken.
         */
        private val STATE_PICTURE_TAKEN = 4

        /**
         * Max preview width that is guaranteed by Camera2 API
         */
        private val MAX_PREVIEW_WIDTH = 1920

        /**
         * Max preview height that is guaranteed by Camera2 API
         */
        private val MAX_PREVIEW_HEIGHT = 1080

        /**
         * Given `choices` of `Size`s supported by a camera, choose the smallest one that
         * is at least as large as the respective texture view size, and that is at most as large as
         * the respective max size, and whose aspect ratio matches with the specified value. If such
         * size doesn't exist, choose the largest one that is at most as large as the respective max
         * size, and whose aspect ratio matches with the specified value.
         *
         * @param choices           The list of sizes that the camera supports for the intended
         *                          output class
         * @param AutoViewWidth  The width of the texture view relative to sensor coordinate
         * @param AutoViewHeight The height of the texture view relative to sensor coordinate
         * @param maxWidth          The maximum width that can be chosen
         * @param maxHeight         The maximum height that can be chosen
         * @param aspectRatio       The aspect ratio
         * @return The optimal `Size`, or an arbitrary one if none were big enough
         */
        @JvmStatic private fun chooseOptimalSize(
                choices: Array<Size>,
                AutoViewWidth: Int,
                AutoViewHeight: Int,
                maxWidth: Int,
                maxHeight: Int,
                aspectRatio: Size
        ): Size {

            // Collect the supported resolutions that are at least as big as the preview Surface
            val bigEnough = ArrayList<Size>()
            // Collect the supported resolutions that are smaller than the preview Surface
            val notBigEnough = ArrayList<Size>()
            val w = aspectRatio.width
            val h = aspectRatio.height
            for (option in choices) {
                if (option.width <= maxWidth && option.height <= maxHeight &&
                        option.height == option.width * h / w) {
                    if (option.width >= AutoViewWidth && option.height >= AutoViewHeight) {
                        bigEnough.add(option)
                    } else {
                        notBigEnough.add(option)
                    }
                }
            }

            // Pick the smallest of those big enough. If there is no one big enough, pick the
            // largest of those not big enough.
            if (bigEnough.size > 0) {
                return Collections.min(bigEnough, CompareSizesByArea())
            } else if (notBigEnough.size > 0) {
                return Collections.max(notBigEnough, CompareSizesByArea())
            } else {
                Log.e(TAG, "Couldn't find any suitable preview size")
                return choices[0]
            }
        }
    }
}
