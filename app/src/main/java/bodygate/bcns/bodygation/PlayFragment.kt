package bodygate.bcns.bodygation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import android.widget.SeekBar
import android.widget.VideoView
import cn.gavinliu.android.lib.scale.ScaleRelativeLayout
import cn.gavinliu.android.lib.scale.ScaleFrameLayout
import android.widget.ImageButton
import android.util.SparseIntArray
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import bodygate.bcns.bodygation.camerause.AutoFitTextureView
import android.view.*
import com.google.android.youtube.player.YouTubePlayerView
import android.media.MediaPlayer
import android.support.annotation.NonNull
import android.os.HandlerThread
import android.media.MediaRecorder
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.util.Size
import android.widget.Toast
import com.google.android.gms.common.ErrorDialogFragment
import kotlinx.android.synthetic.main.fragment_play.*
import java.io.IOException
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PlayFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [PlayFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class PlayFragment : YouTubePlayerSupportFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private val SENSOR_ORIENTATION_DEFAULT_DEGREES = 90
    private val SENSOR_ORIENTATION_INVERSE_DEGREES = 270
    private val DEFAULT_ORIENTATIONS = SparseIntArray()
    private val INVERSE_ORIENTATIONS = SparseIntArray()
    var play: ImageButton? = null
    var record:ImageButton? = null
    var load:ImageButton? = null
    var camerachange:ImageButton? = null
    var play_recordBtn:ImageButton? = null
    private val FURL = "<html><body><iframe width=\"1280\" height=\"720\" src=\""
    private val BURL = "\" frameborder=\"0\" allowfullscreen></iframe></html></body>"
    private val CHANGE = "https://www.youtube.com/embed"
    private val TAG = "Item_follow_fragment_21"
    private val REQUEST_VIDEO_PERMISSIONS = 1
    private val FRAGMENT_DIALOG = "dialog"
    //ScaleRelativeLayout bTnLayout;
    //ScaleFrameLayout cameraLayout;
    var main: ScaleRelativeLayout? = null
    var bTnLayout:ScaleRelativeLayout? = null
    var cameraLayout: ScaleFrameLayout? = null
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
    private val cameraId = CAMERA_FRONT
    var videoView: VideoView? = null
    var seekBar: SeekBar? = null
    private val VIDEO_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

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
    var mTextureView: AutoFitTextureView? = null
    var webView: YouTubePlayerView? = null
    var view: View? = null

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
    private val mPreviewSession: CameraCaptureSession? = null

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
    private val mMediaRecorder: MediaRecorder? = null
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
            if (null != mTextureView) {
                configureTransform(mTextureView.getWidth(), mTextureView.getHeight())
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
    private val mNextVideoAbsolutePath: String? = null
    private val mPreviewBuilder: CaptureRequest.Builder? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
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
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
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
        var bigEnough = ArrayList<Size>();
        var w = aspectRatio.getWidth();
        var h = aspectRatio.getHeight();
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
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            PortrainSet();
            if (mTextureView != null && mTextureView.isAvailable()) {
                configureTransform(mTextureView.getWidth(), mTextureView.getHeight());
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            LandSet();
            if (mTextureView != null && mTextureView.isAvailable()) {
                configureTransform(mTextureView.getHeight(), mTextureView.getWidth());
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
        LandButton.addRule(ScaleRelativeLayout.ALIGN_PARENT_TOP);
        //LandButton.addRule(ScaleRelativeLayout.ALIGN_PARENT_START);
        bTnLayout.setLayoutParams(LandButton);
        playlayout.addRule(ScaleRelativeLayout.ALIGN_PARENT_TOP);
        playlayout.setMargins(getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item));
        play.setLayoutParams(playlayout);
        recordlayout.setMargins(getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item));
        recordlayout.addRule(ScaleRelativeLayout.ALIGN_PARENT_BOTTOM);
        record.setLayoutParams(recordlayout);
        loadlayout.addRule(ScaleRelativeLayout.BELOW, R.id.play_Btn);
        loadlayout.setMargins(getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item));
        load.setLayoutParams(loadlayout);
        play_recordlayout.addRule(ScaleRelativeLayout.CENTER_VERTICAL);
        play_recordlayout.setMargins(getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item));
        play_recordBtn.setLayoutParams(play_recordlayout);
        switchlayout.addRule(ScaleRelativeLayout.ABOVE, R.id.record_Btn);
        switchlayout.setMargins(getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item));
        camerachange.setLayoutParams(switchlayout);
        LandCamera.addRule(ScaleRelativeLayout.END_OF, R.id.button_layout);
        LandCamera.addRule(ScaleRelativeLayout.ALIGN_PARENT_BOTTOM);
        cameraLayout.setLayoutParams(LandCamera);
        LandWebView.addRule(ScaleRelativeLayout.ALIGN_PARENT_END);
        LandWebView.addRule(ScaleRelativeLayout.BELOW, R.id.alpha_control);
        LandWebView.addRule(ScaleRelativeLayout.END_OF, R.id.button_layout);
        webView.setLayoutParams(LandWebView);
        seek.addRule(ScaleRelativeLayout.ALIGN_PARENT_END);
        seek.addRule(ScaleRelativeLayout.END_OF, R.id.button_layout);
        seekBar.setLayoutParams(seek);
        seekBar.setProgress(50);
        seekBar.setVisibility(View.VISIBLE);
        seekBar.setZ((float)2);
        webView.setAlpha((float)0.5);
        webView.setZ((float)2);
        cameraLayout.setZ((float)0);
        mTextureView.setZ((float)0);
        videoView.setLayoutParams(new ScaleFrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mTextureView.setLayoutParams(new ScaleFrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        videoView.setZ((float)1);
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
        LandWebView.addRule(ScaleRelativeLayout.ALIGN_PARENT_START);
        LandWebView.addRule(ScaleRelativeLayout.ALIGN_PARENT_END);
        LandWebView.addRule(ScaleRelativeLayout.ALIGN_PARENT_BOTTOM);
        webView.setLayoutParams(LandWebView);
        LandButton.addRule(ScaleRelativeLayout.ABOVE, R.id.web_movie);
        LandButton.addRule(ScaleRelativeLayout.ALIGN_PARENT_END);
        bTnLayout.setLayoutParams(LandButton);
        playlayout.addRule(ScaleRelativeLayout.ALIGN_PARENT_BOTTOM);
        playlayout.setMargins(getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item));
        play.setLayoutParams(playlayout);
        recordlayout.setMargins(getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item));
        recordlayout.addRule(ScaleRelativeLayout.ALIGN_PARENT_TOP);
        record.setLayoutParams(recordlayout);
        loadlayout.addRule(ScaleRelativeLayout.ABOVE, R.id.play_Btn);
        loadlayout.setMargins(getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item));
        load.setLayoutParams(loadlayout);
        play_recordlayout.addRule(ScaleRelativeLayout.CENTER_VERTICAL);
        play_recordlayout.setMargins(getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item));
        play_recordBtn.setLayoutParams(play_recordlayout);
        switchlayout.addRule(ScaleRelativeLayout.BELOW, R.id.record_Btn);
        switchlayout.setMargins(getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item), getResources().getDimensionPixelSize(R.dimen.imageBtnmargine_item));
        camerachange.setLayoutParams(switchlayout);
        LandCamera.addRule(ScaleRelativeLayout.ALIGN_PARENT_START);
        LandCamera.addRule(ScaleRelativeLayout.ALIGN_PARENT_TOP);
        LandCamera.addRule(ScaleRelativeLayout.START_OF, R.id.button_layout);
        LandCamera.addRule(ScaleRelativeLayout.ABOVE, R.id.web_movie);
        cameraLayout.setLayoutParams(LandCamera);
        seekBar.setVisibility(View.GONE);
        videoView.setLayoutParams(ScaleFrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mTextureView.setLayoutParams(ScaleFrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        webView.setAlpha((1).toFloat());
    }
    private fun viewSet(){
        mTextureView = AutoView
        webView = web_movie
        record = record_Btn
        play = play_Btn
        load = load_Btn
        videoView = videoView
        play_recordBtn = play_record_Btn
        camerachange = viewChange_Btn
        seekBar = alpha_control
        bTnLayout = button_layout
        cameraLayout = video_layout
        main = item_mainLayout
        record.setOnClickListener(this);
        load.setOnClickListener(this);
        play.setOnClickListener(this);
        play_recordBtn.setOnClickListener(this);
        camerachange.setOnClickListener(this);
        videoView.setOnCompletionListener(object : MediaPlayer.OnCompletionListener() {
            override fun onCompletion(mp: MediaPlayer?) {
                play.setImageResource(R.drawable.play);
            }
        });
    }
    private fun ButtonImageSetUp(){
        if(videoView.isPlaying()){
            play.setImageResource(R.drawable.pause);
        }else{
            play.setImageResource(R.drawable.play);
        }
        if(mIsRecordingVideo){
            record.setImageResource(R.drawable.stop);
        }else {
            record.setImageResource(R.drawable.record);
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
            videoviewSetup(mVideoURI);
        }
    }

    private fun videoviewSetup(path: Uri) {
        videoView.setVideoURI(path)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButtonImageSetUp();
    }
    
    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("CameraBackground");
        mBackgroundThread!!.start();
        mBackgroundHandler = Handler(mBackgroundThread!!.getLooper());
    }

    public fun switchCamera() {
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

    public fun reopenCamera() {
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }
    /**
     * Stops the background thread and its {@link Handler}.
     */
    private fun stopBackgroundThread() {
        if(mBackgroundThread !=null) {
            mBackgroundThread.quitSafely();
            try {
                mBackgroundThread.join();
                mBackgroundThread = null;
                mBackgroundHandler = null;
            } catch (e:InterruptedException) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets whether you should show UI with rationale for requesting permissions.
     *
     * @param permissions The permissions your app wants to request.
     * @return Whether you can show permission rationale UI.
     */
    private fun shouldShowRequestPermissionRationale(permissions: Array<String> ):Boolean {
        for (permission: String in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Requests permissions needed for recording video.
     */
    private fun requestVideoPermissions() {
        if (shouldShowRequestPermissionRationale(VIDEO_PERMISSIONS)) {
            ConfirmationDialog().show(getActivity().getSupportFragmentManager(), FRAGMENT_DIALOG);
        } else {
            ActivityCompat.requestPermissions(getActivity(), VIDEO_PERMISSIONS, REQUEST_VIDEO_PERMISSIONS);
        }
    }
    override fun onRequestPermissionsResult(requestCode:Int, @NonNull permissions: Array<String>,
                                           @NonNull grantResults:Array<Int>) {
        Log.d(TAG, "onRequestPermissionsResult");
        if (requestCode == REQUEST_VIDEO_PERMISSIONS) {
            if (grantResults.size == VIDEO_PERMISSIONS.size) {
                for (result:Int in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        ErrorDialogFragment.newInstance(getString(R.string.permission_request_camera))
                                .show(getChildFragmentManager(), FRAGMENT_DIALOG);
                        break;
                    }
                }
            } else {
                ErrorDialogFragment.newInstance(getString(R.string.permission_request_camera))
                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private fun hasPermissionsGranted(permissions:Array<String>):Boolean {
        for (permission : String in permissions) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tries to open a {@link CameraDevice}. The result is listened by `mStateCallback`.
     */
    private fun openCamera(width: Int, height:Int) {
        if (!hasPermissionsGranted(VIDEO_PERMISSIONS)) {
            requestVideoPermissions();
            return;
        }
        val activity = getActivity();
        if (null == activity || activity.isFinishing()) {
            return;
        }
        val manager: CameraManager = activity.getSystemService(Context.CAMERA_SERVICE)
        try {
            Log.d(TAG, "tryAcquire");
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }

            // Choose the sizes for camera preview and video recording
            val characteristics = manager.getCameraCharacteristics(cameraId);
            val map = characteristics
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class))
            mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                    width, height, mVideoSize)
            configureTransform(width, height)
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public fun onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            manager.openCamera(cameraId, mStateCallback, null);
        } catch (e:CameraAccessException) {
            Toast.makeText(activity, "Cannot access the camera.", Toast.LENGTH_SHORT).show();
            activity.finish();
        } catch (e:NullPointerException) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            ErrorDialog.newInstance(getString(R.string.camera_error))
                    .show(getActivity().getSupportFragmentManager(), FRAGMENT_DIALOG);
        } catch (e:InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera opening.");
        }
    }

    private fun closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            closePreviewSession();
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mMediaRecorder) {
                mMediaRecorder.release();
                mMediaRecorder = null;
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
        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }
        try {
            closePreviewSession();
            val texture = mTextureView.getSurfaceTexture();
            assert texture != null
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            val previewSurface = Surface(texture);
            mPreviewBuilder.addTarget(previewSurface);

            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigureFailed(session: CameraCaptureSession?) {
                    val activity = getActivity();
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
            setUpCaptureRequestBuilder(mPreviewBuilder);
            HandlerThread thread = new HandlerThread("CameraPreview");
            thread.start();
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, mBackgroundHandler);

        } catch (e:CameraAccessException) {
            e.printStackTrace();
        }
    }

    private fun setUpCaptureRequestBuilder(builder:CaptureRequest.Builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }

    /**
     * Configures the necessary {@link Matrix} transformation to `mTextureView`.
     * This method should not to be called until the camera preview size is determined in
     * openCamera, or until the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private fun configureTransform(viewWidth:Int, viewHeight:Int) {
        val activity = getActivity();
        if (null == mTextureView || null == mPreviewSize || null == activity) {
            return;
        }
        val rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        val matrix = Matrix();
        val display = getActivity().getWindowManager().getDefaultDisplay();
        val size = Point();
        display.getSize(size);
        val width = size.x;
        val height = size.y;
        val deviceRect = RectF(0, 0, width, height);
        val viewRect = RectF(0, 0, viewWidth, viewHeight);
        Log.d("viewRect :", String.valueOf(viewWidth) + "*" + String.valueOf(viewHeight));
        val landRect = RectF(0, 0, mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Log.d("bufferRect :", String.valueOf(mPreviewSize.getWidth()) + "*" + String.valueOf(mPreviewSize.getHeight()));
        val centerX = deviceRect.centerX();
        val centerY = deviceRect.centerY();
        Log.d("center :", String.valueOf(centerX) + "*" + String.valueOf(centerY));
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            Log.d("beforecenter :", String.valueOf(deviceRect.centerX()) + "*" + String.valueOf(deviceRect.centerY()));
            // deviceRect.offset(centerX - deviceRect.centerX(), centerY - deviceRect.centerY());
            Log.d("aftercenter :", String.valueOf(deviceRect.centerX()) + "*" + String.valueOf(deviceRect.centerX()));
            matrix.setRectToRect(viewRect, deviceRect, Matrix.ScaleToFit.CENTER);
            val scale = Math.max(
                    (viewHeight/height).toFloat(),
                    (viewWidth/width).toFloat());
            Log.d("scale :", String.valueOf(scale));
            matrix.postScale(scale, scale * 2, deviceRect.centerX(), deviceRect.centerY());
            Log.d("postScale :", String.valueOf(scale * 2) + ":" + String.valueOf(centerX) + ":" + String.valueOf(centerY));
            matrix.postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY);
            Log.d("postScale :", String.valueOf(scale * 2) + ":" + String.valueOf(centerX) + ":" + String.valueOf(centerY));
        }
        mTextureView.setTransform(matrix);
        Log.d("mTextureView :", String.valueOf(mTextureView.getWidth()) + "*" + String.valueOf(mTextureView.getHeight()));
    }

    private fun setUpMediaRecorder() {
        val activity = getActivity();
        if (null == activity) {
            return;
        }
        mMediaRecorder = MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (mNextVideoAbsolutePath == null || mNextVideoAbsolutePath.isEmpty()) {
            mNextVideoAbsolutePath = getVideoFilePath(getActivity());
        }
        mMediaRecorder.setOutputFile(mNextVideoAbsolutePath);
        mMediaRecorder.setVideoEncodingBitRate(10000000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        val rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        when (mSensorOrientation) {
            SENSOR_ORIENTATION_DEFAULT_DEGREES ->{
                mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));}
            SENSOR_ORIENTATION_INVERSE_DEGREES->{
                mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));}
        }
        try {
            mMediaRecorder.prepare();
        } catch (e: IOException) {
            Log.e(TAG, "prepare() failed = " + e.toString());
        }
        mMediaRecorder.start();
        mIsRecordingVideo = true;
    }
    @NonNull
    private fun getVideoFilePath(context:Context) :String{
        val dir = context.getExternalFilesDir( null);
        return ((dir == null ? "" : (Environment.getExternalStorageDirectory() + "/" +Environment.DIRECTORY_MOVIES + "/")) + "ViewBody_" +System.currentTimeMillis() + ".mp4")
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
        fun newInstance(param1: String, param2: String) =
                PlayFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
