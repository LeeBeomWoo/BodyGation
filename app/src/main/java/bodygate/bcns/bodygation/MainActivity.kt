package bodygate.bcns.bodygation

import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.*
import android.support.annotation.NonNull
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import bodygate.bcns.bodygation.R.id.navigation_follow
import bodygate.bcns.bodygation.dummy.DummyContent
import bodygate.bcns.bodygation.navigationitem.*
import bodygate.bcns.bodygation.youtube.YoutubeApi
import bodygate.bcns.bodygation.youtube.YoutubeResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.Fitness.ConfigApi
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.FitnessStatusCodes
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.DataTypeCreateRequest
import com.google.android.gms.fitness.request.OnDataPointListener
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.fitness.result.DataTypeResult
import com.google.android.gms.tasks.*
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.google.android.youtube.player.YouTubeThumbnailLoader
import com.google.android.youtube.player.YouTubeThumbnailView
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.youtube.YouTubeScopes
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_follow.*
import kotlinx.android.synthetic.main.fragment_goal.*
import kotlinx.android.synthetic.main.fragment_movie.*
import kotlinx.android.synthetic.main.google_login.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.progressDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.URLEncoder
import java.text.DateFormat.getDateInstance
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


@Suppress("DUPLICATE_LABEL_IN_WHEN")
class MainActivity() : AppCompatActivity(), GoalFragment.OnGoalInteractionListener, FollowFragment.OnFollowInteraction,
        ForMeFragment.OnForMeInteraction, MovieFragment.OnMovieInteraction, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnDataPointListener, Parcelable, YouTubeResult.OnYoutubeResultInteraction {

    private val PREF_ACCOUNT_NAME = "accountName"
    private var mOutputText: TextView? = null;
    val REQUEST_ACCOUNT_PICKER = 1000
    val REQUEST_AUTHORIZATION = 1001
    val REQUEST_GOOGLE_PLAY_SERVICES = 1002
    val REQUEST_PERMISSION_GET_ACCOUNTS = 1003
    private var authInProgress = false
    lateinit var mFitnessClient: GoogleApiClient
    private val REQUEST_OAUTH = 1001
    val ID: String? = null
    val PW: String? = null
    val TAG: String = "MainActivity_"
    var preData:MutableList<YoutubeResponse.Items> = ArrayList()
    var popData:MutableList<YoutubeResponse.Items> = ArrayList()
    var newData:MutableList<YoutubeResponse.Items> = ArrayList()
    var myData:MutableList<YoutubeResponse.Items> = ArrayList()
    private val AUTH_PENDING = "auth_state_pending"
    private val RC_SIGN_IN = 111//google sign in request code
    private val REQUEST_OAUTH_REQUEST_CODE = 1
    var weight_data: DataSet? = null
    var bfp_data: DataSet? = null
    var walk_data: DataSet? = null
    var calore_data: DataSet? = null
    var muscle_data: DataSet? = null
    var bmi_data: DataSet? = null
    var fat_data: DataSet? = null
    var page = ""
    private var mGoogleSignInClient: GoogleSignInClient? = null//google sign in client
    var mCredential: GoogleAccountCredential? = null
    var SCOPES = YouTubeScopes.YOUTUBE_READONLY
    var mPb:ProgressDialog? = null
    var pPb:ProgressDialog? = null
    var nPb:ProgressDialog? = null
    var cPb:ProgressDialog? = null
    private var doubleBackToExitPressedOnce: Boolean = false
    override val context:Context = this
    override var data: MutableList<YoutubeResponse.Items>
        get() = preData
        set(value) {}
    override var walk_dateSET: DataSet?
        get() = walk_data
        set(value) {}
    override var calole_dateSET: DataSet?
        get() = calore_data
        set(value) {}
    override var muscle_dateSET: DataSet?
        get() = muscle_data
        set(value) {}
    override var bmi_dateSET: DataSet?
        get() = bmi_data
        set(value) {}

    fun stopProgress(i:Int) {
        when(i) {
           3-> if (mPb!!.isShowing) {
                Log.i(TAG, "stopProgress")
                mPb!!.dismiss()
            }
            0->    if(cPb!!.isShowing) {
                Log.i(TAG, "stopProgress")
                cPb!!.dismiss()
            }
            2-> if (pPb!!.isShowing) {
                Log.i(TAG, "stopProgress")
                pPb!!.dismiss()
            }
             1->   if(nPb!!.isShowing) {
                Log.i(TAG, "stopProgress")
                nPb!!.dismiss()
            }
        }
    }
    fun startProgress(i:Int) {
        when(i) {
            3-> if(!mPb!!.isShowing) {
                Log.i(TAG, "startProgress")
                mPb!!.setTitle("데이터 받기")
                mPb!!.setMessage("유튜브 데이터를 받아오는 중입니다")
                mPb!!.setCancelable(false)
                mPb!!.show()
            }
            0->  if(!cPb!!.isShowing) {
                Log.i(TAG, "startProgress")
                cPb!!.setTitle("데이터 받기")
                cPb!!.setMessage("유튜브 데이터를 받아오는 중입니다")
                cPb!!.setCancelable(false)
                cPb!!.show()

            }
            2-> if(!pPb!!.isShowing) {
                Log.i(TAG, "startProgress")
                pPb!!.setTitle("데이터 받기")
                pPb!!.setMessage("유튜브 데이터를 받아오는 중입니다")
                pPb!!.setCancelable(false)
                pPb!!.show()
            }
            1->   if(!nPb!!.isShowing) {
                Log.i(TAG, "startProgress")
                nPb!!.setTitle("데이터 받기")
                nPb!!.setMessage("유튜브 데이터를 받아오는 중입니다")
                nPb!!.setCancelable(false)
                nPb!!.show()
            }
        }
}

   fun getData(response: Response<YoutubeResponse>, section:Int) {
        val body = response.body()
        Log.i(TAG, "getData")
        if (body != null) {
            val items = body.items
                when (section) {
                    0 -> {//선택형
                        Log.i("getData", "선택형")
                        preData.addAll(items)
                        if(preData.size >0 ) {
                                val radapter = YoutubeResultListViewAdapter(preData, this@MainActivity)
                                result_list.setAdapter(radapter)
                        }
                    }
                    1 -> {//새로 올라온 영상
                        Log.i("getData", "새로 올라온 영상")
                        newData.addAll(items)
                        if(newData.size >0 ) {
                                val nadapter = YoutubeResultListViewAdapter(newData, this@MainActivity)
                                new_list.setAdapter(nadapter)
                        }
                    }
                    2 -> {//인기많은 영상
                        Log.i("getData", "인기많은 영상")
                        popData.addAll(items)
                        if(popData.size >0 ) {
                                val padapter = YoutubeResultListViewAdapter(popData, this@MainActivity)
                                pop_list.setAdapter(padapter)
                        }
                    }
                    3 -> {//내가 본 영상
                        Log.i("getData", "내가 본 영상")
                        myData.addAll(items)
                        if(myData.size >0 ) {
                                val madapter = YoutubeResultListViewAdapter(myData, this@MainActivity)
                                my_list.setAdapter(madapter)
                        }
                    }
                }
            stopProgress(section)
            }
    }
    suspend fun addData(response: Response<YoutubeResponse>, section:Int, q: String, api_Key: String, max_result: Int, searchType:String, order:String) {
        Log.i(TAG, "addData")
        val body = response.body()
        Log.i(TAG + "request", response.raw().request().url().toString())
        Log.i(TAG + "response", response.body().toString())
        if (body != null) {
            val items = body.items
            when (section) {
                0 -> {//선택형
                    preData.addAll(items)
                }
                1 -> {//새로 올라온 영상
                    newData.addAll(items)
                }
                2 -> {//인기많은 영상
                    popData.addAll(items)
                }
                3 -> {//내가 본 영상
                    myData.addAll(items)
                }
            }
                if(body.nextPageToken != null) {
                    val apiService = YoutubeApi.create()
                    page = body.nextPageToken
                    val youtubeResponseCall = apiService.nextVideo("snippet", max_result, q, "KR",  searchType, page, order, api_Key,"2d")
                    launch { addData( youtubeResponseCall.execute(), section, q, api_Key,  max_result, searchType, order)
                    }

                }else{
                    launch(UI){getData(response, section)}
                }
            }
    }
    override fun moveBack(q:Fragment) {
        when(q){
            YouTubeResult.newInstance() -> {
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.root_layout, FollowFragment.newInstance(ID, PW), "rageComicList")
                        .commit()
            }
        }
    }
    override fun describeContents(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun OnYoutubeResultInteraction(){

    }

    override var bfp_dateSET: DataSet?
        get() = bfp_data
        set(value) {}

    override var bfp_list: Array<com.jjoe64.graphview.series.DataPoint>?
        set(value) {}
        get() = arrayOf()

    override var weight_dateSET: DataSet?
        get() = weight_data
        set(value) {}

    override var weight_list: Array<com.jjoe64.graphview.series.DataPoint>?
        set(value) {}
        get() = arrayOf()

    override fun writeToParcel(p0: Parcel?, p1: Int) {
    }

    override fun OnGoalInteractionListener(uri: Uri) {
    }

    suspend override fun getDatas(part: String, q: String, api_Key: String, max_result: Int, more:Boolean, section:Int) {
        Log.i(TAG, "getDatas")
        val searchType = "video"
        val a = q.replace("[", "");
        val b = a.replace("]", "")
        val apiService = YoutubeApi.create()
        var order = ""
        when(section){
            0->{//선택형
                order = "relevance"
            }
            1 -> {//새로 올라온 영상
                order = "date"
            }
            2 ->{//인기많은 영상
                order = "rating"
            }
            3->{//내가 본 영상
                order = "relevance"
            }
        }
        launch(UI) { startProgress(section) }
        Log.i("getData", order)
            val youtubeResponseCall = apiService.searchVideo("snippet", max_result, b, "KR",  searchType, order, api_Key, "2d")
        launch {addData( youtubeResponseCall.execute(), section, q, api_Key,  max_result, searchType, order)}
    }
    override fun OnFollowInteraction() {
        Log.i(TAG, "OnFollowInteraction")
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.root_layout, YouTubeResult.newInstance(), "rageComicList")
                .commit()
    }

    override fun OnForMeInteraction() {
    }

    override fun OnMovieInteraction(item: DummyContent.DummyItem) {
    }

    override fun getpage(): String {
        return nextpage()
    }
    fun nextpage():String{
        return page
    }
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
        //해당 페이지로 이동
            R.id.navigation_goal -> {
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.root_layout, GoalFragment.newInstance(ID, PW), "rageComicList")
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
            /**R.id.navigation_home -> {
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.root_layout, MovieFragment.newInstance(), "rageComicList")
                        .commit()
                return@OnNavigationItemSelectedListener true
            }*/
            R.id.navigation_follow -> {
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.root_layout, FollowFragment.newInstance(ID, PW), "rageComicList")
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_infome -> {
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.root_layout, ForMeFragment.newInstance(ID), "rageComicList")
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onBackPressed() {
        Log.i(TAG,"onBackPressed")
        if (doubleBackToExitPressedOnce) {
            moveTaskToBack(true)
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(1)
            return
        }else{
            doubleBackToExitPressedOnce = true
        }
        // Do something
        if (navigation.selectedItemId != navigation_follow) {
            navigation.selectedItemId = navigation_follow
            Log.i(TAG,"onBackPressed 1")
        } else {
            Log.i(TAG,"onBackPressed 2")
            val builder = AlertDialog.Builder(this)
            if (title != null) builder.setTitle(title)
            builder.setMessage("프로그램을 종료하시겠습니까?")
            builder.setPositiveButton("OK", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    android.os.Process.killProcess(android.os.Process.myPid())
                }
            })
            builder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.dismiss()
                }

            }
            )
            builder.show()
        }
    }
    constructor(parcel: Parcel) : this() {
        authInProgress = parcel.readByte() != 0.toByte()
    }

    @SuppressLint("RestrictedApi")
    private fun configureGoogleSignIn() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()//request email id
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    /**
     * custom sign in button click event
     *
     * @param view custom button
     */
    fun customGoogleSignIn(view: View) {
        doSignInSignOut()
    }

    /**
     * method to do Sign In or Sign Out on the basis of account exist or not
     */
    @SuppressLint("RestrictedApi")
    private fun doSignInSignOut() {

        //get the last sign in account
        val account = GoogleSignIn.getLastSignedInAccount(this)

        //if account doesn't exist do login else do sign out
        if (account == null)
            doGoogleSignIn()
        else
            doGoogleSignOut()
    }

    /**
     * do google sign in
     */
    @SuppressLint("RestrictedApi")
    private fun doGoogleSignIn() {
        val signInIntent = mGoogleSignInClient?.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)//pass the declared request code here
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG + "_", "onCreate")
        configureGoogleSignIn()
        // mPb = ProgressDialog(this)
       // pPb = ProgressDialog(this)
       // nPb = ProgressDialog(this)
        cPb = ProgressDialog(this)
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(ExponentialBackOff());
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        } else {
            supportFragmentManager.beginTransaction()
                    .add(R.id.root_layout, FollowFragment.newInstance(ID, PW), "rageComicList")
                    .commit()
            navigation.selectedItemId = navigation_follow
        }
        val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_WEIGHT)
                .addDataType(DataType.TYPE_BODY_FAT_PERCENTAGE)
                .addDataType(DataType.TYPE_BASAL_METABOLIC_RATE)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED)
                .addDataType(DataType.TYPE_STEP_COUNT_CADENCE)
                .build()
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
           GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions)
        } else {
            registerFitnessDataListener();
        }
        getResultsFromApi()
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        mFitnessClient = GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.CONFIG_API)
                .addScope(Scope(Scopes.FITNESS_LOCATION_READ))
                .addScope(Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addScope(Scope(Scopes.FITNESS_NUTRITION_READ_WRITE))
                .addScope(Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addScope(Scope(Scopes.FITNESS_BODY_READ))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
        mFitnessClient.connect()
    }

    @SuppressLint("RestrictedApi")
    override fun onStart() {
        super.onStart()
        // Connect to the Fitness API
        Log.i(TAG, "Connecting...")
        val account = GoogleSignIn.getLastSignedInAccount(this)
        //update the UI if user has already sign in with the google for this app
        mFitnessClient.connect()
        //getProfileInformation(account)
    }

    override fun onStop() {
        super.onStop()
        if (mFitnessClient.isConnected()) {
            mFitnessClient.disconnect()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            REQUEST_GOOGLE_PLAY_SERVICES -> {
                if (resultCode != RESULT_OK) {
                    mOutputText!!.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
            }
            REQUEST_ACCOUNT_PICKER -> {
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    val accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        val settings =
                                getPreferences(Context.MODE_PRIVATE);
                        val editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential!!.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
            }
            REQUEST_AUTHORIZATION -> {
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
            }
            REQUEST_OAUTH -> {
                authInProgress = false;
                if (resultCode == RESULT_OK) {
                    // Make sure the app is not already connected or attempting to connect
                    if (!mFitnessClient.isConnecting() && !mFitnessClient.isConnected()) {
                        mFitnessClient.connect();
                    }
                }
            }
            RC_SIGN_IN ->{
                val task = GoogleSignIn.getSignedInAccountFromIntent(data);
                getProfileInformation(task.result);
                val dialog = CustomDialogList(this)
                dialog.setTitle("계정 로그인")
                dialog.show()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
    }

    fun printData(dataReadResult: DataReadResponse) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getDataSets().size > 0) {
            Log.i(TAG, "Number of returned DataSets is: " + dataReadResult.getDataSets().size);
            for (dataSet in dataReadResult.getDataSets()) {
                weight_dumpDataSet(dataSet)
            }
        }
        // [END parse_read_data_result]
    }

    fun weight_dumpDataSet(dataSet: DataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        val dateFormat = getDateInstance()
        when(dataSet.dataType){
            DataType.TYPE_WEIGHT->{
                weight_data = dataSet
            }
        }
        for (dp: DataPoint in dataSet.getDataPoints()) {
            Log.i(TAG, "Data point:" + dp.toString())
            Log.i(TAG, "Type: " + dp.getDataType().getName());
            Log.i(TAG, "Start: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "End: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "TimeStemp: " + dateFormat.format(dp.getTimestamp(TimeUnit.MILLISECONDS)) + "type: " + dp.getTimestamp(TimeUnit.MILLISECONDS).javaClass)
            Log.i(TAG, " Value: " + dp.getValue(Field.FIELD_WEIGHT) + "type: " + dp.getValue(Field.FIELD_WEIGHT).javaClass)
        }
        Log.i(TAG, "list point:" + weight_list.toString())
    }
   override fun makePersonalData() = launch(CommonPool) {
       val cal = Calendar.getInstance()
       val now = Date()
       cal.time = now
       val nowTime = cal.timeInMillis
        val request = DataTypeCreateRequest.Builder()
// The prefix of your data type name must match your app's package name
                .setName("bodygate.bcns.bodygation.data_type")
// Add some custom fields, both int and float
                .addField("muscle", Field.FORMAT_FLOAT)
                .addField("fat", Field.FORMAT_FLOAT)
                .addField("bmi", Field.FORMAT_FLOAT)
// Add some common fields
                .addField(Field.FIELD_WEIGHT)
                .build()
       if(!mFitnessClient.isConnected){
           mFitnessClient.connect()
       }
       val response_1 = Fitness.getConfigClient(this@MainActivity, GoogleSignIn.getLastSignedInAccount(this@MainActivity)!!)
               .createCustomDataType(request)
       response_1.addOnFailureListener(object :OnFailureListener{
           override fun onFailure(p0: java.lang.Exception) {
               Log.i("pendingResult", this@MainActivity.packageName.toString())
               Log.i("pendingResult", "response_1- " + p0.toString())
           }

       })
       Tasks.await(response_1)
        val pendingResult = ConfigApi.readDataType(mFitnessClient, "bodygate.bcns.bodygation.data_type");
        pendingResult.setResultCallback(object : ResultCallback<DataTypeResult>{
            override fun onResult(p0: DataTypeResult) {
                Log.i("pendingResult", p0.toString())
                Log.i("pendingResult", p0.dataType.toString())
                val source = DataSource.Builder()
                        .setName("bodygate.bcns.bodygation.data_type")
                        .setDataType(p0.getDataType())
                        .setAppPackageName(this@MainActivity.context)
                        .setType(DataSource.TYPE_DERIVED)
                        .build()
                val dataPoint = DataPoint.create(source)
                // Set values for the data point
                // This data type has two custom fields (int, float) and a common field
                dataPoint.getValue(Field.zza("muscle", Field.FORMAT_FLOAT)).setFloat(my_musclemass_txtB.text.toString().toFloat())
                dataPoint.getValue(Field.zza("fat", Field.FORMAT_FLOAT)).setFloat(my_bodyfat_txtB.text.toString().toFloat())
                dataPoint.getValue(Field.zza("bmi", Field.FORMAT_FLOAT)).setFloat(my_bmi_txtB.text.toString().toFloat())
                dataPoint.getValue(Field.FIELD_WEIGHT).setFloat(my_bodyfat_txtB.text.toString().toFloat())
                dataPoint.setTimestamp(nowTime, TimeUnit.MILLISECONDS)
                val dataSet = DataSet.create(source)
                dataSet.add(dataPoint)
                val response = Fitness.getHistoryClient(this@MainActivity, GoogleSignIn.getLastSignedInAccount(this@MainActivity)!!).insertData(dataSet)
                launch(coroutineContext) { Tasks.await(response) }
            }

        })
    }
    override fun onConnected(p0: Bundle?) {
        Log.i(TAG, "onConnected")
        //Google Fit Client에 연결되었습니다.
        registerFitnessDataListener()
    }

    override fun onDataPoint(dataPoint: DataPoint) {
        Log.i(TAG, "onDataPoint")
        // Do cool stuff that matters. 중요한 것을 멋지게 처리하십시오.
        for (field: Field in dataPoint.getDataType().getFields()) {
            val value = dataPoint.getValue(field);
        }
    }

    override fun onConnectionSuspended(cause: Int) {
        Log.i(TAG, "onConnectionSuspended")
        // The connection has been interrupted. Wait until onConnected() is called.
    }

    @SuppressLint("RestrictedApi")
    fun registerFitnessDataListener() = launch(CommonPool) {
        val cal = Calendar.getInstance()
        val now = Date()
        val endTime = now.time
        cal.set(2000, 1, 1)
        val startTime = cal.timeInMillis
        Log.i(TAG, "Range Start: " + startTime.toString())
        Log.i(TAG, "Range End: " + endTime.toString())
        val task = GoogleSignIn.getLastSignedInAccount(this@MainActivity)
        val response = Fitness.getHistoryClient(this@MainActivity, task!!)
                .readData(DataReadRequest.Builder()
                        .read(DataType.TYPE_WEIGHT)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build())
        val response_step = Fitness.getHistoryClient(this@MainActivity, task)
            .readDailyTotal(DataType.AGGREGATE_STEP_COUNT_DELTA);
        val response_kcal = Fitness.getHistoryClient(this@MainActivity, task)
                .readDailyTotal(DataType.AGGREGATE_STEP_COUNT_DELTA);
        val pendingResult = ConfigApi.readDataType(mFitnessClient, "bodygate.bcns.bodygation.data_type")
        pendingResult.setResultCallback(object : ResultCallback<DataTypeResult> {
            override fun onResult(p0: DataTypeResult) {
                val response_muscle = Fitness.getHistoryClient(this@MainActivity, task)
                        .readData(DataReadRequest.Builder()
                                .read(p0.dataType)
                                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                                .build())
                val response_bmi = Fitness.getHistoryClient(this@MainActivity, task)
                        .readData(DataReadRequest.Builder()
                                .read(p0.dataType)
                                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                                .build());
                val response_fat = Fitness.getHistoryClient(this@MainActivity, task)
                        .readData(DataReadRequest.Builder()
                                .read(p0.dataType)
                                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                                .build());
                launch(CommonPool){
                   val muscle_readDataResult = Tasks.await(response_muscle);
                   val fat_readDataResult = Tasks.await(response_fat);
                   val bmi_readDataResult = Tasks.await(response_bmi);
                    muscle_data = muscle_readDataResult.getDataSet(p0.dataType)
                    fat_data = fat_readDataResult.getDataSet(p0.dataType)
                    bmi_data = bmi_readDataResult.getDataSet(p0.dataType)
            }
            }
        })

        val readDataResult = Tasks.await(response);
        walk_data = Tasks.await(response_step);
        calore_data = Tasks.await(response_kcal);
        val dataSet = readDataResult.getDataSet(DataType.TYPE_WEIGHT)
        Log.i(TAG + "dataSet", dataSet.toString())
        async(UI) { printData(readDataResult) }
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        Log.i(TAG, "onConnectionFailed")
        // Error while connecting. Try to resolve using the pending intent returned.
        Log.i(TAG, "onConnectionFailed" + ":" + result.toString())
        if (result.getErrorCode() == FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS) {
            try {
                result.startResolutionForResult(this, REQUEST_OAUTH);
            } catch (e: IntentSender.SendIntentException) {
                Log.i(TAG, "onConnectionFailed" + ":" + e.toString())
            }
        }
    }

    /**
     * method to handle google sign in result
     *
     * @param completedTask from google onActivityResult
     */

    /**
     * method to fetch user profile information from GoogleSignInAccount
     *
     * @param acct googleSignInAccount
     */
    private fun getProfileInformation(acct: GoogleSignInAccount?) {
        //if account is not null fetch the information
        if (acct != null) {
            //user display name
            val personName = acct.getDisplayName()

            //user first name
            val personGivenName = acct.getGivenName()

            //user last name
            val personFamilyName = acct.getFamilyName()

            //user email id
            val personEmail = acct.getEmail()

            //user unique id
            val personId = acct.getId()

            //user profile pic
            val personPhoto = acct.getPhotoUrl()

            //show the user details
            user_details_label.setText("ID : " + personId + "\nDisplay Name : " + personName + "\nFull Name : " + personGivenName + " " + personFamilyName + "\nEmail : " + personEmail);

            //show the user profile pic
            Picasso.get().load(personPhoto).fit().placeholder(R.mipmap.ic_launcher_round).into(user_profile_image_view);

            //change the text of Custom Sign in button to sign out
            custom_sign_in_button.setText(getResources().getString(R.string.sign_out));

            //show the label and image view
            user_details_label.setVisibility(View.VISIBLE);
            user_profile_image_view.setVisibility(View.VISIBLE);
        } else {
            //if account is null change the text back to Sign In and hide the label and image view
            custom_sign_in_button.setText(getResources().getString(R.string.sign_in));
            user_details_label.setVisibility(View.GONE);
            user_profile_image_view.setVisibility(View.GONE);
        }
    }

    /**
     * method to do google sign out
     * This code clears which account is connected to the app. To sign in again, the user must choose their account again.
     */
    @SuppressLint("RestrictedApi")
    private fun doGoogleSignOut() {
        mGoogleSignInClient?.signOut()?.addOnCompleteListener(this, {
            Toast.makeText(this, "Google Sign Out done.", Toast.LENGTH_SHORT).show()
            revokeAccess();
        })
    }

    /**
     * DISCONNECT ACCOUNTS
     * method to revoke access from this app
     * call this method after successful sign out
     * <p>
     * It is highly recommended that you provide users that signed in with Google the ability to disconnect their Google account from your app. If the user deletes their account, you must delete the information that your app obtained from the Google APIs
     */
    @SuppressLint("RestrictedApi")
    private fun revokeAccess() {
        mGoogleSignInClient?.revokeAccess()?.addOnCompleteListener(this, {
            Toast.makeText(this, "Google access revoked.", Toast.LENGTH_SHORT).show()
            getProfileInformation(null)
        })
    }

    companion object CREATOR : Parcelable.Creator<MainActivity> {
        override fun createFromParcel(parcel: Parcel): MainActivity {
            return MainActivity(parcel)
        }

        override fun newArray(size: Int): Array<MainActivity?> {
            return arrayOfNulls(size)
        }
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private fun getResultsFromApi() = launch {
        if (isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential!!.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
        } else {
            val task = MakeRequestTask(mCredential!!)
            task.execute()
            Log.i(TAG, task.get().toString())
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    private fun chooseAccount() {
        if (EasyPermissions.hasPermissions(
                        this, Manifest.permission.ACCOUNT_MANAGER)) {
            val accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential!!.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential!!.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.ACCOUNT_MANAGER);
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */

    override fun onRequestPermissionsResult(requestCode: Int,
                                            @NonNull permissions: Array<String>,
                                            @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public fun onPermissionsGranted(requestCode: Int, list: List<String>) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public fun onPermissionsDenied(requestCode: Int, list: List<String>) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private fun isDeviceOnline(): Boolean {
        val connMgr: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private fun isGooglePlayServicesAvailable(): Boolean {
        val apiAvailability =
                GoogleApiAvailability.getInstance();
        val connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private fun acquireGooglePlayServices() {
        val apiAvailability =
                GoogleApiAvailability.getInstance();
        val connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode)
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    fun showGooglePlayServicesAvailabilityErrorDialog(
            connectionStatusCode: Int) {
        val apiAvailability = GoogleApiAvailability.getInstance();
        val dialog = apiAvailability.getErrorDialog(
                this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the YouTube Data API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    @SuppressLint("StaticFieldLeak")
    inner class MakeRequestTask(mCredential: GoogleAccountCredential?) : AsyncTask<Void, Void, MutableList<String>?>() {
        override fun doInBackground(vararg p0: Void?): MutableList<String>? {
            Log.i(TAG, "MakeRequestTask")
            val transport = AndroidHttp.newCompatibleTransport ();
            val jsonFactory = JacksonFactory.getDefaultInstance ();
            mService = com.google.api.services.youtube.YouTube.Builder(
                    transport, jsonFactory, mCredential)
                    .setApplicationName("YouTube Data API Android Quickstart")
                    .build()
            try {
                return getDataFromApi(mService);
            } catch (e: Exception) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        var  mService:com.google.api.services.youtube.YouTube? = null;
        var mLastError:Exception? = null;

            /**
             * Fetch information about the "GoogleDevelopers" YouTube channel.
             * @return List of Strings containing information about the channel.
             * @throws IOException
             */
            private fun getDataFromApi(mService: com.google.api.services.youtube.YouTube?): MutableList<String> {
                Log.i(TAG, "getDataFromApi")
                // Get a list of up to 10 files.
                val channelInfo = ArrayList<String>()
                val result = mService!!.channels().list("snippet,contentDetails,statistics")
                        .setForUsername("GoogleDevelopers")
                        .execute();
                val channels = result.getItems();
                if (channels != null) {
                    val channel = channels.get(0);
                    channelInfo.add("This channel's ID is " + channel.getId() + ". " +
                            "Its title is '" + channel.getSnippet().getTitle() + ", " +
                            "and it has " + channel.getStatistics().getViewCount() + " views.");
                }
                return channelInfo
            }

            override fun onPreExecute() {
                super.onPreExecute()
               // mOutputText.setText("");
               // mProgress!!.show();
            }

            override fun onPostExecute(result: MutableList<String>?) {
                super.onPostExecute(result)
               // mProgress!!.hide();
                if (result == null || result.size == 0) {
                      mOutputText!!.setText("No results returned.");
                } else {
                    result.add(0, "Data retrieved using the YouTube Data API:");
                    mOutputText!!.setText(TextUtils.join("\n", result));
                }
            }


            override fun onCancelled() {
                super.onCancelled()
                //mProgress!!.hide();
                if (mLastError != null) {
                    if (mLastError is GooglePlayServicesAvailabilityIOException) {
                        showGooglePlayServicesAvailabilityErrorDialog(
                                (mLastError as GooglePlayServicesAvailabilityIOException)
                                        .getConnectionStatusCode());
                    } else if (mLastError is UserRecoverableAuthIOException) {
                        startActivityForResult(
                                (mLastError as UserRecoverableAuthIOException).getIntent(),
                                REQUEST_AUTHORIZATION);
                    } else {
                        //mOutputText.setText("The following error occurred:\n"+ mLastError.message)
                    }
                } else {
                   // mOutputText.setText("Request cancelled.");
                }
            }
        }

}





