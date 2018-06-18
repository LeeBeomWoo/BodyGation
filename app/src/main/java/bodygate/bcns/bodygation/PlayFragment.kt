package bodygate.bcns.bodygation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.RectF
import android.net.Uri
import android.support.v4.app.Fragment
import android.widget.SeekBar
import cn.gavinliu.android.lib.scale.ScaleRelativeLayout
import cn.gavinliu.android.lib.scale.ScaleFrameLayout
import android.widget.ImageButton
import android.util.SparseIntArray
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.camera2.*
import bodygate.bcns.bodygation.camerause.AutoFitTextureView
import android.view.*
import android.media.MediaPlayer
import android.support.annotation.NonNull
import android.media.MediaRecorder
import android.os.*
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.Toast
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
class PlayFragment : Fragment(), View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }

    private val ARG_PARAM1 = "url"
    private var param1: String? = null
    var viewalpha:Int = 0
    private var listener: PlayFragment.OnFragmentInteractionListener? = null
    private val SENSOR_ORIENTATION_DEFAULT_DEGREES = 90
    private val SENSOR_ORIENTATION_INVERSE_DEGREES = 270
    private val DEFAULT_ORIENTATIONS = SparseIntArray()
    private val INVERSE_ORIENTATIONS = SparseIntArray()
    private val FURL = "<html><body><iframe width=\"1280\" height=\"720\" src=\""
    private val BURL = "\" frameborder=\"0\" allowfullscreen></iframe></html></body>"
    private val CHANGE = "https://www.youtube.com/embed/"
    private val TAG = "Item_follow_fragment_21"
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
    private var cameraId = CAMERA_FRONT


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

    /**
     * An [AutoFitTextureView] for camera preview.
     */

    /**
     * Button to record video
     */

    /**
     * A refernce to the opened [CameraDevice].
     */
    private var mCameraDevice: CameraDevice? = null

    var tr_id: String? = null
    var imageUrl:String? = null
    var tr_password:String? = null
    var URL:String? = null
    /**
     * A reference to the current [CameraCaptureSession] for
     * preview.
     */
    private var mPreviewSession: CameraCaptureSession? = null

    /**
     * [TextureView.SurfaceTextureListener] handles several lifecycle events on a
     * [TextureView].
     */
    private val mSurfaceTextureListener = object : TextureView.SurfaceTextureListener {

        override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture,
                                               width: Int, height: Int) {
            openCamera(width, height)
        }

        override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture,
                                                 width: Int, height: Int) {
            configureTransform(width, height)
        }

        override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
            return true
        }

        override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {

        }

    }
    /**
     * The [Size] of camera preview.
     */
    private var mPreviewSize: Size? = null

    /**
     * The [Size] of video recording.
     */
    private var mVideoSize: Size? = null

    /**
     * MediaRecorder
     */
    var mMediaRecorder: MediaRecorder? = null
    /**
     * Whether the app is recording video now
     */
    var mIsRecordingVideo: Boolean = false

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private var mBackgroundThread: HandlerThread? = null

    /**
     * A [Handler] for running tasks in the background.
     */
    private var mBackgroundHandler: Handler? = null

    /**
     * A [Semaphore] to prevent the app from exiting before closing the camera.
     */
    private val mCameraOpenCloseLock = Semaphore(1)

    /**
     * [CameraDevice.StateCallback] is called when [CameraDevice] changes its status.
     */
    private val mStateCallback = object : CameraDevice.StateCallback() {

        override fun onOpened(cameraDevice: CameraDevice) {
            mCameraDevice = cameraDevice
            startPreview()
            mCameraOpenCloseLock.release()
            if (null != AutoView) {
                configureTransform(AutoView.getWidth(), AutoView.getHeight())
            }
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            mCameraOpenCloseLock.release()
            cameraDevice.close()
            mCameraDevice = null
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            mCameraOpenCloseLock.release()
            cameraDevice.close()
            mCameraDevice = null
            val activity = activity
            activity?.finish()
        }

    }
    private var mSensorOrientation: Int? = null
    private var mNextVideoAbsolutePath: String? = null
    private var mPreviewBuilder: CaptureRequest.Builder? = null
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
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        startBackgroundThread()
        reopenCamera()
        youtube_layout.resumeTimers()
    }
   override fun onPause() {
        Log.d(TAG, "onPause")
        closeCamera()
        stopBackgroundThread()
        super.onPause()
        youtube_layout.pauseTimers()
    }
    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.i(TAG, "onActivityCreated")
        onConfigurationChanged(getActivity()!!.getResources().getConfiguration())
        startBackgroundThread()
        ButtonImageSetUp()
        viewSet()
        startPreview()
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
        fun requestVideoPermissions(permission:String)
        fun onFragmentInteraction(uri: Uri)
        fun hasPermissionsGranted(permissions:Array<String>):Boolean
    }
/**
     * In this sample, we choose a video size with 3x4 aspect ratio. Also, we don't use sizes
     * larger than 1080p, since MediaRecorder cannot handle such a high-resolution video.
     * 이 샘플에서는 가로 세로 비율이 3x4 인 비디오 크기를 선택합니다. 또한 크기를 사용하지 않습니다.
     * MediaRecorder가 이러한 고해상도 비디오를 처리 할 수 없기 때문에 1080p보다 큽니다.
     * @param choices The list of available sizes
     * @return The video size
     */
    private fun chooseVideoSize(choices:Array<Size>):Size {
        for (size:Size in choices) {
            if (size.getWidth() == size.getHeight() * 16 / 10 && size.getWidth() <= 1080) {
                return size;
            }
        }
        Log.e(TAG, "Couldn't find any suitable video size");

        return choices[choices.size - 1];
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, chooses the smallest one whose
     * width and height are at least as large as the respective requested values, and whose aspect
     * ratio matches with the specified value.
     *
     * 녹화가 가능한 사이즈들 중에서 프리뷰로 보여줄 수 있는 사이즈를 선택한다.
     * @param choices     The list of sizes that the camera supports for the intended output class
     * @param width       The minimum desired width
     * @param height      The minimum desired height
     * @param aspectRatio The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private fun chooseOptimalSize(choices:Array<Size>, width:Int, height:Int, aspectRatio:Size):Size {
        // Collect the supported resolutions that are at least as big as the preview Surface
        val bigEnough = ArrayList<Size>();
        val w = aspectRatio.getWidth();
        val h = aspectRatio.getHeight();
        for (option : Size in choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size > 0) {
            return Collections.max(bigEnough, CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
            super.onConfigurationChanged(newConfig);
        Log.i(TAG, "onConfigurationChanged")
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            PortrainSet();
            if (AutoView != null && AutoView!!.isAvailable()) {
                configureTransform(AutoView!!.getWidth(), AutoView!!.getHeight());
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            LandSet();
            if (AutoView != null && AutoView!!.isAvailable()) {
                configureTransform(AutoView!!.getHeight(), AutoView!!.getWidth());
            }
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
        if(mIsRecordingVideo){
            record_Btn.setImageResource(R.drawable.stop);
        }else {
            record_Btn.setImageResource(R.drawable.record);
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    
    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("CameraBackground");
        mBackgroundThread!!.start();
        mBackgroundHandler = Handler(mBackgroundThread!!.getLooper());
    }

    fun switchCamera() {
        if (cameraId.equals(CAMERA_FRONT)) {
            cameraId = CAMERA_BACK;
            closeCamera();
            reopenCamera();

        } else if (cameraId.equals(CAMERA_BACK)) {
            cameraId = CAMERA_FRONT;
            closeCamera();
            reopenCamera();
        }
    }

    fun reopenCamera() {
        if (AutoView.isAvailable()) {
            openCamera(AutoView.getWidth(), AutoView.getHeight());
        } else {
            AutoView.setSurfaceTextureListener(mSurfaceTextureListener)
        }
    }
    /**
     * Stops the background thread and its {@link Handler}.
     */
    private fun stopBackgroundThread() {
        if(mBackgroundThread !=null) {
            mBackgroundThread!!.quitSafely();
            try {
                mBackgroundThread!!.join();
                mBackgroundThread = null;
                mBackgroundHandler = null;
            } catch (e:InterruptedException) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Requests permissions needed for recording video.
     */

    /**
     * Tries to open a {@link CameraDevice}. The result is listened by `mStateCallback`.
     */
    private fun openCamera(width: Int, height:Int) {

        val activity = getActivity();
        if (null == activity || activity.isFinishing()) {
            return;
        }
        val manager: CameraManager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            Log.d(TAG, "tryAcquire");
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock camera opening.");
            }

            // Choose the sizes for camera preview and video recording
            val characteristics = manager.getCameraCharacteristics(cameraId);
            val map = characteristics
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder::class.java))
            mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture::class.java),
                    width, height, mVideoSize!!)
            configureTransform(width, height)
            if (ActivityCompat.checkSelfPermission(getActivity()!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public fun onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                if (!listener!!.hasPermissionsGranted(VIDEO_PERMISSIONS)) {
                    for(permission: String in VIDEO_PERMISSIONS)
                        listener!!.requestVideoPermissions(permission)
                }
                return
            }
            manager.openCamera(cameraId, mStateCallback, null);
        } catch (e:CameraAccessException) {
            Toast.makeText(activity, "Cannot access the camera.", Toast.LENGTH_SHORT).show();
            activity.finish();
        } catch (e:NullPointerException) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            ErrorDialog().newInstance(getString(R.string.camera_error))
                    .show(getActivity()!!.getSupportFragmentManager(), TAG)
        } catch (e:InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera opening.");
        }
    }

    private fun closeCamera() {
        try {
            mCameraOpenCloseLock.acquire()
            closePreviewSession();
            if (null != mCameraDevice) {
                mCameraDevice!!.close()
                mCameraDevice = null;
            }
            if(mMediaRecorder != null){
                mMediaRecorder!!.release()
            }

        } catch ( e:InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera closing.");
        } finally {
            mCameraOpenCloseLock.release();
        }
    }
 /**
     * Start the camera preview.
     */
    private fun startPreview() {
        if (null == mCameraDevice || !AutoView.isAvailable() || null == mPreviewSize) {
            return
        }
        try {
            closePreviewSession()
            val texture = AutoView.getSurfaceTexture()
            assert(texture != null)
            texture.setDefaultBufferSize(mPreviewSize!!.getWidth(), mPreviewSize!!.getHeight());
            mPreviewBuilder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            val previewSurface = Surface(texture)
            mPreviewBuilder!!.addTarget(previewSurface)

            mCameraDevice!!.createCaptureSession(Arrays.asList(previewSurface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigureFailed(session: CameraCaptureSession?) {
                    val activity = getActivity()
                    if (null != activity) {
                        Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }

                override fun onConfigured(session: CameraCaptureSession?) {
                    mPreviewSession = session;
                    updatePreview();
                }

            }, mBackgroundHandler);
        } catch ( e:CameraAccessException) {
            e.printStackTrace();
        }
    }

    /**
     * Update the camera preview. {@link #startPreview()} needs to be called in advance.
     */
    private fun updatePreview() {
        if (null == mCameraDevice) {
            return;
        }
        try {
            setUpCaptureRequestBuilder(this.mPreviewBuilder!!);
            val thread = HandlerThread("CameraPreview");
            thread.start();
            mPreviewSession!!.setRepeatingRequest(mPreviewBuilder!!.build(), null, mBackgroundHandler);

        } catch (e:CameraAccessException) {
            e.printStackTrace();
        }
    }

    private fun setUpCaptureRequestBuilder(builder:CaptureRequest.Builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }

    /**
     * Configures the necessary {@link Matrix} transformation to `AutoView`.
     * This method should not to be called until the camera preview size is determined in
     * openCamera, or until the size of `AutoView` is fixed.
     *
     * @param viewWidth  The width of `AutoView`
     * @param viewHeight The height of `AutoView`
     */
    private fun configureTransform(viewWidth:Int, viewHeight:Int) {
        val activity = getActivity();
        if (null == AutoView || null == mPreviewSize || null == activity) {
            return;
        }
        val rotation = activity.getWindowManager().getDefaultDisplay().getRotation()
        /*
        val matrix = Matrix();
        val display = getActivity()!!.getWindowManager().getDefaultDisplay();
        val size = Point();
        display.getSize(size);
        val width = size.x;
        val height = size.y;
        val deviceRect = RectF(0.toFloat(), 0.toFloat(), width.toFloat(), height.toFloat());
        val viewRect = RectF(0.toFloat(), 0.toFloat(), viewWidth.toFloat(), viewHeight.toFloat());
        Log.d("viewRect :", (viewWidth).toString() + "*" + (viewHeight).toString());
        val landRect = RectF(0.toFloat(), 0.toFloat(), mPreviewSize!!.getWidth().toFloat(), mPreviewSize!!.getHeight().toFloat());
        Log.d("bufferRect :", (mPreviewSize!!.getWidth()).toString() + "*" + (mPreviewSize!!.getHeight()).toString());
        val centerX = deviceRect.centerX();
        val centerY = deviceRect.centerY();
        Log.d("center :", (centerX).toString() + "*" + (centerY).toString());
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            Log.d("beforecenter :", (deviceRect.centerX()).toString() + "*" + (deviceRect.centerY()).toString());
            // deviceRect.offset(centerX - deviceRect.centerX(), centerY - deviceRect.centerY());
            Log.d("aftercenter :", (deviceRect.centerX()).toString() + "*" + (deviceRect.centerX()).toString());
            matrix.setRectToRect(viewRect, deviceRect, Matrix.ScaleToFit.CENTER);
            val scale = Math.max(
                    (viewHeight/height).toFloat(),
                    (viewWidth/width).toFloat())
            Log.d("scale :", scale.toString());
            matrix.postScale(scale, scale * 2, deviceRect.centerX(), deviceRect.centerY());
            Log.d("postScale :", (scale * 2).toString() + ":" + (centerX).toString() + ":" + (centerY).toString());
            matrix.postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY);
            Log.d("postScale :", (scale * 2).toString() + ":" + (centerX).toString() + ":" + (centerY).toString());
        }
        AutoView.setTransform(matrix);
        Log.d("AutoView :", (AutoView.getWidth()).toString() + "*" + (AutoView.getHeight()).toString());
        */
        if(Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation)
        {
            AutoView.setAspectRatio(4, 3)
        }else{
            AutoView.setAspectRatio(10, 16)
        }
    }

    private fun setUpMediaRecorder() {
        val activity = getActivity();
        if (null == activity) {
            return;
        }
        try {
        mMediaRecorder = MediaRecorder()
        mMediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder!!.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (mNextVideoAbsolutePath == null || mNextVideoAbsolutePath!!.isEmpty()) {
            mNextVideoAbsolutePath = getVideoFilePath();
        }
        mMediaRecorder!!.setOutputFile(mNextVideoAbsolutePath)
        mMediaRecorder!!.setVideoSize(mVideoSize!!.getWidth(), mVideoSize!!.getHeight())
        mMediaRecorder!!.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mMediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        val rotation = activity.getWindowManager().getDefaultDisplay().getRotation()
        when (mSensorOrientation) {
            SENSOR_ORIENTATION_DEFAULT_DEGREES ->{
                mMediaRecorder!!.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));}
            SENSOR_ORIENTATION_INVERSE_DEGREES->{
                mMediaRecorder!!.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));}
        }
            mMediaRecorder!!.prepare()
        } catch (e: IOException) {
            Log.e(TAG, "prepare() failed = " + e.toString());
        }
        mMediaRecorder!!.start()
        mIsRecordingVideo = true;
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewalpha = alpha_control.progress
        outState.putString("url", param1)
        outState.putInt("alpha", viewalpha)
        super.onSaveInstanceState(outState)
    }

    @NonNull
    private fun getVideoFilePath() :String{
        val dir = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        var myDir = ""
        if(dir == null){
            myDir = dir.toString() +  "/BodyGation/"
        }else{
            val dire = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI
            myDir = dire.toString() +  "/BodyGation/"
        }
        val sciezka = File(myDir)
        sciezka.mkdirs()
        return myDir + "BodyGation_" + System.currentTimeMillis() + ".mp4"
    }

    fun startRecordingVideo() {
        if (null == mCameraDevice || !AutoView.isAvailable() || null == mPreviewSize) {
            return;
        }
        try {
            closePreviewSession()
            setUpMediaRecorder()
            val texture = AutoView.getSurfaceTexture();
            assert(texture != null)
            texture.setDefaultBufferSize(mPreviewSize!!.getWidth(), mPreviewSize!!.getHeight());
            mPreviewBuilder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            val surfaces :MutableList<Surface> = ArrayList()

            // Set up Surface for the camera preview
            val previewSurface = Surface(texture);
            surfaces.add(previewSurface);
            mPreviewBuilder!!.addTarget(previewSurface);

            // Set up Surface for the MediaRecorder
            val recorderSurface = mMediaRecorder!!.getSurface()
            surfaces.add(recorderSurface);
            mPreviewBuilder!!.addTarget(recorderSurface);

            // Start a capture session
            // Once the session starts, we can update the UI and start recording
            mCameraDevice!!.createCaptureSession(surfaces, object : CameraCaptureSession.StateCallback() {
                override fun onConfigureFailed(session: CameraCaptureSession?) {
                    val activity = getActivity();
                    if (null != activity) {
                        Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }

                override fun onConfigured(session: CameraCaptureSession?) {
                    mPreviewSession = session;
                    updatePreview();
                    getActivity()!!.runOnUiThread(object:Runnable {
                        override fun run() {
                            // UI
                            Log.d("Video_", "Start")
                            record_Btn.setImageResource(R.drawable.stop)
                            mIsRecordingVideo = true
                            // Start recording
                            mMediaRecorder!!.start()
                        }
                    })
                }
            }, mBackgroundHandler)
        } catch ( e:IOException) {
            e.printStackTrace();
        }

    }

    private fun closePreviewSession() {
        if (mPreviewSession != null) {
            mPreviewSession!!.close()
            mPreviewSession = null;
        }
    }

    fun stopRecordingVideo() {
        // UI
        mIsRecordingVideo = false;
        // Stop recording
        record_Btn.setImageResource(R.drawable.record);
        mMediaRecorder!!.stop();
        mMediaRecorder!!.reset();
        // CameraHelper.getOutputMediaFile(2);

        val activity = getActivity();
        if (null != activity) {
            Toast.makeText(activity, "Video saved: " + mNextVideoAbsolutePath,
                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Video saved: " + mNextVideoAbsolutePath);
        }
        Log.d("Video_", "Stop");
        mNextVideoAbsolutePath = null;
        startPreview();
    }

    override fun onClick(v:View) {
        when (v.getId()) {
            R.id.record_Btn ->//녹화
            {
                Log.d(TAG, "record_Btn thouch");
            if (AutoView.getVisibility() == View.INVISIBLE) {
                AutoView.setVisibility(View.VISIBLE);
            }
                if (AutoView != null) {
                if (mIsRecordingVideo) {
                    stopRecordingVideo();
                } else {
                    video_View.setVisibility(View.INVISIBLE);
                    AutoView.setVisibility(View.VISIBLE);
                    startRecordingVideo()
                }
            }
        }
            R.id.play_Btn//재생
                ->{
                Log.d(TAG, "play_Btn thouch");
                if(video_View.isPlaying()){
                    video_View.pause();
                    play_record_Btn.setImageResource(R.drawable.play);
                }else {
                    video_View.start();
                    play_record_Btn.setImageResource(R.drawable.pause);
                    AutoView.setVisibility(View.INVISIBLE);
                    video_View.setVisibility(View.VISIBLE);
                }
                }

            R.id.load_Btn//파일불러오기
                    -> {
                videoString = null;
                videopath = null;
                val intent = Intent(Intent.ACTION_GET_CONTENT);
                val uri = Uri . parse (Environment.getExternalStorageDirectory().getPath()
                        + File.separator + Environment.DIRECTORY_MOVIES + File.separator);
                intent.setType("video/mp4");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivityForResult(Intent.createChooser(intent, "Select Video"), 2);
            }
            R.id.play_record_Btn//파일과 카메라간 변환
                -> {
                if (play_record!!) {
                    if (mIsRecordingVideo) {
                        stopRecordingVideo();
                    }
                    closeCamera();
                    AutoView.setVisibility(View.INVISIBLE);
                    video_View.setVisibility(View.VISIBLE);
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
                    startPreview();
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

    /**
     * Compares two {@code Size}s based on their areas.
     */
    class CompareSizesByArea : Comparator<Size> {

        override fun compare(lhs:Size, rhs:Size):Int {
            // We cast here to ensure the multiplications won't overflow
            return java.lang.Long.signum(((lhs.getWidth() * lhs.getHeight()).toLong() - (rhs.getWidth() * rhs.getHeight()).toLong()))
        }

    }

     class ErrorDialog : DialogFragment() {

        private val ARG_MESSAGE = "message";

        fun newInstance(message:String):ErrorDialog {
            val dialog = ErrorDialog();
            val args = Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val activity = getActivity();
            return AlertDialog.Builder(activity!!)
                    .setMessage(getArguments()!!.getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            activity.finish();
                        }
                    })
                    .create();
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

    }
}
