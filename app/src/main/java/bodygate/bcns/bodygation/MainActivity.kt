package bodygate.bcns.bodygation

import android.Manifest
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.provider.Settings
import android.support.annotation.NonNull
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import bodygate.bcns.bodygation.dummy.DummyContent
import bodygate.bcns.bodygation.navigationitem.*
import cn.gavinliu.android.lib.scale.config.ScaleConfig
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.FitnessStatusCodes
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.DataTypeCreateRequest
import com.google.android.gms.fitness.request.OnDataPointListener
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.fitness.result.DataReadResult
import com.google.android.gms.tasks.*
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeScopes
import com.google.api.services.youtube.model.SearchListResponse
import com.google.api.services.youtube.model.SearchResult
import com.google.common.io.BaseEncoding
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_for_me.*
import kotlinx.android.synthetic.main.fragment_goal.*
import kotlinx.android.synthetic.main.toolbar_custom.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.defaultSharedPreferences
import pub.devrel.easypermissions.EasyPermissions
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DateFormat.getDateInstance
import java.text.DateFormat.getTimeInstance
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.security.auth.callback.Callback
import kotlin.collections.ArrayList


@Suppress("DUPLICATE_LABEL_IN_WHEN")
class MainActivity() : AppCompatActivity(), GoalFragment.OnGoalInteractionListener, FollowFragment.OnFollowInteraction,
        ForMeFragment.OnForMeInteraction, MovieFragment.OnMovieInteraction, YouTubeResult.OnYoutubeResultInteraction, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    var sendcheck:Boolean = false
    var pB: ProgressDialog? = null
    private val PREF_ACCOUNT_NAME = "accountName"
    private val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1
    val REQUEST_ACCOUNT_PICKER = 2
    private val REQUEST_OAUTH = 1001
    val REQUEST_GOOGLE_PLAY_SERVICES = 1002
    val REQUEST_PERMISSION_GET_ACCOUNTS = 1003
    var mClient:GoogleApiClient? = null
    var connectFitAPI:Boolean = false
    val ID: String? = null
    val PW: String? = null
    val TAG: String = "MainActivity_"
    var personUrl:Uri? = null
    var page = ""
    override var totalpage = 100
    lateinit var mGoogleSignInClient: GoogleSignInClient//google sign in client
    var mCredential: GoogleAccountCredential? = null
    var SCOPES = YouTubeScopes.YOUTUBE_READONLY
    var mPb:ProgressDialog? = null
    var pPb:ProgressDialog? = null
    var nPb:ProgressDialog? = null
    var cPb:ProgressDialog? = null
    override var visableFragment = ""
    private var doubleBackToExitPressedOnce: Boolean = false
    override val context:Context = this
    override var sendquery:ArrayList<String>? = null
    override var data: MutableList<SearchResult> = arrayListOf()
    override var last_position = 0
    override var current_position = 0
    private val AUTH_PENDING = "auth_state_pending"
    var accountname = ""

    override var display_label:MutableList<String> =  ArrayList()
    override var display_series: MutableList<String> = ArrayList()

    val weight_series: MutableList<BarEntry> = ArrayList()
    val muscle_series: MutableList<BarEntry> = ArrayList()
    val walk_series: MutableList<BarEntry> = ArrayList()
    val fat_series: MutableList<BarEntry> = ArrayList()
    val bmi_series: MutableList<BarEntry> = ArrayList()
    val kcal_series: MutableList<BarEntry> = ArrayList()

    var custom_Type:DataType? = null
    var weight_Label:MutableList<String> =  ArrayList()
    var kcal_Label:MutableList<String> =  ArrayList()
    var walk_Label:MutableList<String> =  ArrayList()
    var fat_Label:MutableList<String> =  ArrayList()
    var muscle_Label:MutableList<String> =  ArrayList()
    var bmi_Label:MutableList<String> =  ArrayList()
    var ib = 0
    override fun stopProgress(i:Int) {
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

  fun addData(response: SearchListResponse, section:Int) {
       Log.i(TAG, "addData")
      Log.i("test", "second")
       val body = response.items
       Log.i("query", body.toString())
       Log.i("response", response.toString())
      if(response.nextPageToken.isNotEmpty()) {
          page = response.nextPageToken
          totalpage = (response.pageInfo.totalResults/5)
          Log.i("data_page", page)
          Log.i("data_page", totalpage.toString())
      }else{
          page = ""
      }
          data = body
      Log.i("data", data.toString())
       }

    suspend override fun getNetxtPage( q: String, api_Key: String, max_result: Int, more:Boolean, section:Int){
        val youTube = YouTube.Builder(NetHttpTransport(), JacksonFactory.getDefaultInstance(), object: HttpRequestInitializer {
            @Throws(IOException::class)
            override fun initialize(request:HttpRequest) {
                val SHA1 = getSHA1(packageName)
                request.getHeaders().set("X-Android-Package", packageName)
                request.getHeaders().set("X-Android-Cert", SHA1)
            }
        }).setApplicationName(packageName).build()
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
        totalpage -= 1
        Log.i("data_page", page)
        Log.i("data_page", totalpage.toString())
        if(totalpage > 1){
        val query = youTube.search().list("id, snippet")
        query.setKey(api_Key)
        query.setType("video")
        if(page != "")
            query.setPageToken(page)
        query.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url), nextPageToken, pageInfo")
        query.setQ(q)
        query.setOrder(order)
        query.setMaxResults(max_result.toLong())
        launch(CommonPool) {
            val body = query.execute()
            if(body.nextPageToken.isNotEmpty()){
            page = body.nextPageToken
            }else{
                page = ""
            }
            data = body.items }.join()
        }else{
            data.clear()
        }
    }
    override fun OnYoutubeResultInteraction(){

    }
    override fun OnGoalInteractionListener() {
        insertData()
    }
    @SuppressLint("PackageManagerGetSignatures")
    private fun getSHA1(packageName:String):String? {
        try
        {
            val signatures = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures
            for (signature in signatures)
            {
                val md:MessageDigest
                md = MessageDigest.getInstance("SHA-1")
                Log.i(TAG, md.toString())
                md.update(signature.toByteArray())
                return BaseEncoding.base16().encode(md.digest())
            }
        }
        catch (e:PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }
   suspend override fun getDatas(part: String, q: String, api_Key: String, max_result: Int, more:Boolean, section:Int) {
        Log.i(TAG, "getDatas")
       launch(UI){startProgress(section)}
       val youTube = YouTube.Builder(NetHttpTransport(), JacksonFactory.getDefaultInstance(), object: HttpRequestInitializer {
           @Throws(IOException::class)
           override fun initialize(request:HttpRequest) {
               val SHA1 = getSHA1(packageName)
               request.getHeaders().set("X-Android-Package", packageName)
               request.getHeaders().set("X-Android-Cert", SHA1)
           }
        }).setApplicationName(packageName).build()
        val searchType = "video"
        val a = q.replace("[", "");
        val b = a.replace("]", "")
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
        val query = youTube.search().list("id, snippet")
        query.setKey(api_Key)
        query.setType("video")
        query.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url), nextPageToken, pageInfo")
        val bReader = BufferedReader(InputStreamReader(b.byteInputStream()))
        val inputQuery = bReader.readLine()
        query.setQ(inputQuery)
        query.setMaxResults(max_result.toLong())
        query.setOrder(order)
        query.setType(searchType)
       Log.i("test", "first")
        launch(CommonPool) {addData( query.execute(), section)}.join()
       Log.i("test", "third")
    }
    override fun OnFollowInteraction(q: ArrayList<String>?, s:Int) {
        Log.i(TAG, "OnFollowInteraction")
        Log.i("test", "다섯번 째")
            sendquery = q
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.root_layout, YouTubeResult.newInstance(), "rageComicList")
                    .commit()
    }

    override fun OnMovieInteraction(item: DummyContent.DummyItem) {
    }

    override fun getpage(): String {
        return nextpage()
    }
    fun nextpage():String{
        return page
    }
    override fun onBackPressed() {
        Log.i(TAG,"onBackPressed")

        // Do something
        when(bottom_navigation.currentItem){
            1 ->{
                if(visableFragment == "YouTubeResult" ){
                    sendquery = null
                    data.clear()
                    bottom_navigation.setCurrentItem(1)
                }else {
                    if (doubleBackToExitPressedOnce) {
                        moveTaskToBack(true)
                        android.os.Process.killProcess(android.os.Process.myPid())
                        System.exit(1)
                        return
                    }else{
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
                                doubleBackToExitPressedOnce = false
                                dialog!!.dismiss()
                            }
                        }
                        )
                        builder.show()
                        doubleBackToExitPressedOnce = true
                    }
                }
            }
            0 ->{
                doubleBackToExitPressedOnce = false
                    bottom_navigation.currentItem = 1
            }
           2 ->{
                doubleBackToExitPressedOnce = false
                    bottom_navigation.currentItem = 1
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScaleConfig.create(this,
                1080, // Design Width
                1920, // Design Height
                (3).toFloat(),    // Design Density
                (3).toFloat(),    // Design FontScale
                ScaleConfig.DIMENS_UNIT_DP);
        setContentView(R.layout.activity_main)
        Log.d(TAG + "_", "onCreate")
        //mPb = ProgressDialog(this)
        // pPb = ProgressDialog(this)
        // nPb = ProgressDialog(this)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestProfile()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        cPb = ProgressDialog(this)
        mClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Fitness.CONFIG_API)
                .addApi(Fitness.HISTORY_API)
                .addScope(Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addScope(Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
        if(GoogleSignIn.getLastSignedInAccount(this) == null){
            signIn()
        }else{
            getProfileInformation(GoogleSignIn.getLastSignedInAccount(this))
        }
        val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_WRITE)
                .build();
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions)
        } else {
            val task = fitTask()
            task.execute()
        }
        // Create items
        val item1 = AHBottomNavigationItem(getString(R.string.title_goal), getDrawable(R.drawable.select_goalmenu))
        val item2 = AHBottomNavigationItem(getString(R.string.follow_media), getDrawable(R.drawable.select_followmenu))
        val item3 = AHBottomNavigationItem(getString(R.string.title_infome), getDrawable(R.drawable.select_formemenu))
        bottom_navigation.addItem(item1)
        bottom_navigation.addItem(item2)
        bottom_navigation.addItem(item3)
        bottom_navigation.setDefaultBackgroundColor(Color.parseColor("#FFFFFFFF"))
        bottom_navigation.setOnTabSelectedListener(object: AHBottomNavigation.OnTabSelectedListener{
            override fun onTabSelected(item: Int, wasSelected: Boolean): Boolean {
                when (item) {
                //해당 페이지로 이동
                    0 -> {
                        supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.root_layout, GoalFragment.newInstance(ID, PW), "rageComicList")
                                .commit()
                        return true
                    }
                    1 -> {
                        if(sendquery != null){
                            OnFollowInteraction(sendquery, 0)
                        }else{
                            supportFragmentManager
                                    .beginTransaction()
                                    .replace(R.id.root_layout, FollowFragment.newInstance(), "rageComicList")
                                    .commit()
                        }
                        return true
                    }
                    2 -> {
                            supportFragmentManager
                                    .beginTransaction()
                                    .replace(R.id.root_layout, ForMeFragment.newInstance(personUrl.toString()), "rageComicList")
                                    .commit()
                        return true
                    }
                }
                return false
            }

        })
        bottom_navigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE)
        bottom_navigation.setCurrentItem(1)
        bottom_navigation.setForceTint(true)
        bottom_navigation.setAccentColor(Color.parseColor("#41c0c1"));
        bottom_navigation.setInactiveColor(Color.parseColor("#696969"));
        bottom_navigation.setTranslucentNavigationEnabled(true)
        toolbarhome.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                if(sendquery != null)
                sendquery = null
                data.clear()
                bottom_navigation.setCurrentItem(1)
            }

        })
        toolbarmenu.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                //팝업메뉴
            }

        })
    }
    private fun signIn() {
        Log.i(TAG, "signIn")
        val signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_OAUTH)
    }
    fun insertData() {
        val cal = Calendar.getInstance()
        val now = Date()
        cal.time = now
        val nowTime = cal.timeInMillis
        // Create a new dataset and insertion request.
        val source = DataSource.Builder()
        .setName("bodygate.bcns.bodygation.personal")
                .setDataType(custom_Type)
                .setAppPackageName(this@MainActivity.context.packageName)
                .setType(DataSource.TYPE_DERIVED)
                .build()
        val dataPoint = DataPoint.create(source)
        // Set values for the data point
        // This data type has two custom fields (int, float) and a common field
        for (s: Int in 0..(custom_Type!!.fields.size - 1)) {
            when (custom_Type!!.fields[s].name) {
                "bmi" -> {
                    dataPoint.getValue(custom_Type!!.fields[s]).setFloat(my_bmi_txtB.text.toString().toFloat())
                }
                "muscle" -> {
                    dataPoint.getValue(custom_Type!!.fields[s]).setFloat(my_musclemass_txtB.text.toString().toFloat())
                }
                "fat" -> {
                    dataPoint.getValue(custom_Type!!.fields[s]).setFloat(my_bodyfat_txtB.text.toString().toFloat())
                }
            }
        }
        dataPoint.setTimestamp(nowTime, TimeUnit.MILLISECONDS)
        val dataSet = DataSet.create(source)
        dataSet.add(dataPoint)
        val response = Fitness.getHistoryClient(this@MainActivity, GoogleSignIn.getLastSignedInAccount(this@MainActivity)!!).insertData(dataSet)
                .addOnFailureListener(object : OnFailureListener {
                    override fun onFailure(p0: Exception) {
                        if (sendcheck) {
                            Log.i(TAG + "Failure", "onFailure" + p0.hashCode().toString())
                            Log.i(TAG + "Failure", "onFailure" + p0.message)
                            Log.i(TAG + "Failure", "onFailure" + p0.localizedMessage)
                            Toast.makeText(this@MainActivity, "자료가 업데이트 되지 않았습니다. 반복 될 경우 개발자에게 문의 부탁드립니다.~^^", Toast.LENGTH_SHORT).show()
                        } else {
                            sendcheck = true
                            launch(CommonPool) { makeData()}
                        }
                    }
                })
                .addOnSuccessListener(object :OnSuccessListener<Void>{
                    override fun onSuccess(p0: Void?) {
                        Toast.makeText(this@MainActivity, "자료가 성공적으로 업데이트 되었습니다. 다음달에도 꼭 등록하여 주세요~^^", Toast.LENGTH_SHORT).show()
                        bottom_navigation.currentItem = 1
                    }

                })
        // Then, invoke the History API to insert the data.
        Log.i(TAG, "Inserting the dataset in the History API.")
        launch(CommonPool) { Tasks.await(response)}
    }

    override fun onStart() {
        super.onStart()
        // Connect to the Fitness API
        Log.i(TAG, "Connecting...")
        mClient!!.connect()
    }

    override fun onStop() {
        super.onStop()
        if (mClient!!.isConnected()) {
            mClient!!.disconnect();
        }
    }
    fun chooseAccount() {
        startActivityForResult(mCredential!!.newChooseAccountIntent(),
                REQUEST_ACCOUNT_PICKER)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            GOOGLE_FIT_PERMISSIONS_REQUEST_CODE -> {
                Log.i(TAG, "GOOGLE_FIT_PERMISSIONS_REQUEST_CODE")
                if (resultCode == Activity.RESULT_OK) {
                    getProfileInformation(GoogleSignIn.getLastSignedInAccount(this))
                    val task = fitTask()
                    task.execute()
                }
            }
            REQUEST_ACCOUNT_PICKER -> {
                Log.i(TAG, "REQUEST_ACCOUNT_PICKER")
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null && data.getExtras() != null) {
                        val accountName =
                                data.getExtras().getString(
                                        AccountManager.KEY_ACCOUNT_NAME);
                        if (accountName != null) {
                            setSelectedAccountName(accountName);
                            val editor: SharedPreferences.Editor = defaultSharedPreferences.edit()
                            editor.putString(PREF_ACCOUNT_NAME, accountName)
                            editor.apply()
                            // User is authorized.
                        }
                    }
                }
            }
            REQUEST_OAUTH -> {
                Log.i(TAG, "REQUEST_OAUTH")
                if (resultCode == Activity.RESULT_OK) {
                    // Make sure the app is not already connected or attempting to connectTask<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    getProfileInformation(task.result);
                }
            }
        }
    }
private fun setSelectedAccountName(accountName:String) {
    val editor:SharedPreferences.Editor = defaultSharedPreferences.edit()
  editor.putString(PREF_ACCOUNT_NAME, accountName);
  editor.apply()
  mCredential!!.setSelectedAccountName(accountName);
  this.accountname = accountName;
}
    @SuppressLint("SetTextI18n")
    override fun OnForMeInteraction(section:Int):BarData {
        last_position = 0
        val data = BarData()
        when (section) {
            0 -> {//체중
                if(weight_series.size >0) {
                    Log.i(TAG, "체중 있음")
                    last_position = weight_series.size - 1
                    display_label = weight_Label
                    val set1 = BarDataSet(weight_series, getString(R.string.weight))
                    set1.setColors(Color.rgb(65, 192, 193))
                    data.addDataSet(set1)
                }else{
                    Log.i(TAG, "체중 없음")
                    Toast.makeText(this, "현재 구글핏에서 데이터를 받아오지 못했습니다. 구글핏을 확인하시고 데이터가 있는데도 반복될 경우 개발자에게 오류보고 부탁드립니다.", Toast.LENGTH_SHORT).show()
                }
            }
            1 -> {//걷기
                if(walk_series.size >0) {
                    Log.i(TAG, "걷기자료 있음")
                    last_position = walk_series.size - 1
                    display_label = walk_Label
                    val set1 = BarDataSet(walk_series, getString(R.string.walk))
                    set1.setColors(Color.rgb(65, 192, 193))
                    val xAxis = graph.xAxis
                    xAxis.setGranularity(1f)
                    xAxis.setValueFormatter(MyXAxisValueFormatter(walk_Label.toTypedArray()))
                    data.addDataSet(set1)
                }else{
                    Log.i(TAG, "걷기자료 없음")
                    Toast.makeText(this, "현재 구글핏에서 데이터를 받아오지 못했습니다. 구글핏을 확인하시고 데이터가 있는데도 반복될 경우 개발자에게 오류보고 부탁드립니다.", Toast.LENGTH_SHORT).show()
                }
            }
            2 -> {//칼로리
                if(kcal_series.size >0) {
                    Log.i(TAG, "칼로리 있음")
                    last_position = kcal_series.size - 1
                    display_label = kcal_Label
                    val set1 = BarDataSet(kcal_series, getString(R.string.calore))
                    set1.setColors(Color.rgb(65, 192, 193))
                    val xAxis = graph.xAxis
                    xAxis.setGranularity(1f)
                    xAxis.setValueFormatter(MyXAxisValueFormatter(walk_Label.toTypedArray()))
                    data.addDataSet(set1)
                }else{
                    Log.i(TAG, "칼로리 없음")
                    Toast.makeText(this, "현재 구글핏에서 데이터를 받아오지 못했습니다. 구글핏을 확인하시고 데이터가 있는데도 반복될 경우 개발자에게 오류보고 부탁드립니다.", Toast.LENGTH_SHORT).show()
                }
            }
            3 -> {//체지방비율
                if(fat_series.size >0) {
                    Log.i(TAG, "체지방비율 있음")
                    last_position = fat_series.size - 1
                    display_label = fat_Label
                    val set1 = BarDataSet(fat_series, getString(R.string.bodyfat))
                    set1.setColors(Color.rgb(65, 192, 193))
                    val xAxis = graph.xAxis
                    xAxis.setGranularity(1f)
                    xAxis.setValueFormatter(MyXAxisValueFormatter(walk_Label.toTypedArray()))
                    data.addDataSet(set1)
                }else{
                    Log.i(TAG, "체지방비율 없음")
                    Toast.makeText(this, "현재 구글핏에서 데이터를 받아오지 못했습니다. 구글핏을 확인하시고 데이터가 있는데도 반복될 경우 개발자에게 오류보고 부탁드립니다.", Toast.LENGTH_SHORT).show()
                }
            }
            4 -> {//골격근
                if(muscle_series.size >0) {
                    Log.i(TAG, "골격근 있음")
                    last_position = muscle_series.size - 1
                    display_label = muscle_Label
                    val set1 = BarDataSet(muscle_series, getString(R.string.musclemass))
                    set1.setColors(Color.rgb(65, 192, 193))
                    val xAxis = graph.xAxis
                    xAxis.setGranularity(1f)
                    xAxis.setValueFormatter(MyXAxisValueFormatter(walk_Label.toTypedArray()))
                    data.addDataSet(set1)
                }else{
                    Log.i(TAG, "골격근 없음")
                    Toast.makeText(this, "현재 구글핏에서 데이터를 받아오지 못했습니다. 구글핏을 확인하시고 데이터가 있는데도 반복될 경우 개발자에게 오류보고 부탁드립니다.", Toast.LENGTH_SHORT).show()
                }
            }
            5 -> {//BMI
                if(bmi_series.size >0) {
                    Log.i(TAG, "BMI 있음")
                    last_position = bmi_series.size - 1
                    display_label = bmi_Label
                    val set1 = BarDataSet(bmi_series, getString(R.string.bmi))
                    set1.setColors(Color.rgb(65, 192, 193))
                    val xAxis = graph.xAxis
                    xAxis.setGranularity(1f)
                    xAxis.setValueFormatter(MyXAxisValueFormatter(walk_Label.toTypedArray()))
                    data.addDataSet(set1)
                }else{
                    Log.i(TAG, "BMI 없음")
                    Toast.makeText(this, "현재 구글핏에서 데이터를 받아오지 못했습니다. 구글핏을 확인하시고 데이터가 있는데도 반복될 경우 개발자에게 오류보고 부탁드립니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        Log.i(TAG, "label , series :" + last_position.toString())
        return data
    }
    override fun onConnectionSuspended(i: Int) {
        // If your connection to the sensor gets lost at some point,
        // you'll be able to determine the reason and react to it here.
        if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            Log.i(TAG, "Connection lost.  Cause: Network Lost.");
        } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
        }
    }
    override fun onConnected(p0: Bundle?) {
        Log.i(TAG, "Connected!!!");
        val task = fitTask()
        task.execute()
    }
    override fun onConnectionFailed(result: ConnectionResult) {
        Log.i(TAG, "Connection failed. Cause: " + result.toString());
        if (!result.hasResolution()) {
            // Show the localized error dialog
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                    this, 0).show();
            return;
        }else{
            result.startResolutionForResult(this, REQUEST_OAUTH)
        }
    }
   suspend fun makeData(){
        Log.i(TAG, "makeData")
        val request = DataTypeCreateRequest.Builder()
    // The prefix of your data type name must match your app's package name
                .setName("bodygate.bcns.bodygation.personal")
                .addField("bmi", Field.FORMAT_FLOAT)
                .addField("muscle", Field.FORMAT_FLOAT)
                .addField("fat", Field.FORMAT_FLOAT)
                .build()
        val pendingResult = Fitness.getConfigClient(this, GoogleSignIn.getLastSignedInAccount(this)!!).createCustomDataType(request)
       launch(CommonPool) { Tasks.await(pendingResult)}.join()
    }

    override fun onPause() {
        super.onPause()
        if(pB != null)
        if(pB!!.isShowing)
            pB!!.dismiss()
    }

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
            personUrl = acct.photoUrl
            Log.i("profile", "getProfileInformation : " + personName + "\t" + acct.photoUrl.toString() + "\t"+ personEmail +"\t" + personId + "\t" + acct.toString())
        }
    }
    fun customDataType(){
        Log.i(TAG, "customDataType")
        val pendingResult_custom = Fitness.ConfigApi.readDataType(mClient, "bodygate.bcns.bodygation.personal")
        launch { custom_Type = pendingResult_custom.await().dataType}
        Log.i(TAG, custom_Type.toString())
    }

    fun readRequest_weight(){
        Log.i(TAG, "readRequest_weight")
        val cal = Calendar.getInstance()
        val now = Date()
        val endTime = now.time
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        cal.set(2015, 1, 1)
        val startTime = cal.timeInMillis
        val request = DataReadRequest.Builder()
                .read(DataType.TYPE_WEIGHT)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build()
        val fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
                        .build();
        val gsa = GoogleSignIn.getAccountForExtension(this, fitnessOptions)
        val response = Fitness.HistoryApi.readData(mClient, request)
                /*
                .addOnFailureListener(object :OnFailureListener{
                    override fun onFailure(p0: Exception) {
                        Log.i(TAG, p0.toString())
                        Log.i(TAG, "readRequest_weight_" +"onFailure")
                        Log.i(TAG, "localizedMessage :" +p0.localizedMessage)
                        Log.i(TAG, "message :" +p0.message)
                    }
                })
                .addOnCompleteListener(object : OnCompleteListener<DataReadResponse>{
                    override fun onComplete(p0: Task<DataReadResponse>) {
                        Log.i(TAG, "readRequest_weight_" + "onComplete")
                    }
                })
                .addOnSuccessListener(object :OnSuccessListener<DataReadResponse>{
                    override fun onSuccess(p0: DataReadResponse?) {
                        Log.i(TAG, "readRequest_weight_" + "onSuccess")
                        printData(p0!!)
                    }
                })*/
       dumpDataSet(response.await().getDataSet(DataType.TYPE_WEIGHT))
    }
    fun readRequest_arr(){
        Log.i(TAG, "readRequest_AGGREGATE")
        val cal = Calendar.getInstance()
        val now = Date()
        val endTime = now.time
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        cal.set(2015, 1, 1)
        val startTime = cal.timeInMillis
        val request = DataReadRequest.Builder()
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build()
       val fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                        .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                        .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                        .build();
        val gsa = GoogleSignIn.getAccountForExtension(this, fitnessOptions)
        val response = Fitness.getHistoryClient(this, gsa).readData(request)
                .addOnFailureListener(object :OnFailureListener{
                    override fun onFailure(p0: Exception) {
                        Log.i(TAG, p0.toString())
                        Log.i(TAG, "AGGREGATE_" +"onFailure")
                        Log.i(TAG, "localizedMessage :" +p0.localizedMessage)
                        Log.i(TAG, "message :" +p0.message)
                    }
                })
                .addOnCompleteListener(object : OnCompleteListener<DataReadResponse>{
                    override fun onComplete(p0: Task<DataReadResponse>) {
                        Log.i(TAG, "AGGREGATE_" + "onComplete")
                    }
                })
                .addOnSuccessListener(object :OnSuccessListener<DataReadResponse>{
                    override fun onSuccess(p0: DataReadResponse?) {
                        Log.i(TAG, "AGGREGATE_" + "onSuccess")
                        printData(p0!!)
                    }
                })
        Tasks.await(response)
    }
    fun readRequest_custom(){
        Log.i(TAG, "readRequest_custom")
        val cal = Calendar.getInstance()
        val now = Date()
        val endTime = now.time
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        cal.set(2015, 1, 1)
        val startTime = cal.timeInMillis
        val request = DataReadRequest.Builder()
                .read(custom_Type)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build()
        val fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(custom_Type!!, FitnessOptions.ACCESS_READ)
                        .build();
        val gsa = GoogleSignIn.getAccountForExtension(this, fitnessOptions)
        val response = Fitness.getHistoryClient(this, gsa).readData(request)
                .addOnFailureListener(object :OnFailureListener{
                    override fun onFailure(p0: Exception) {
                        Log.i(TAG, p0.toString())
                        Log.i(TAG, "readRequest_custom_" +"onFailure")
                        Log.i(TAG, "localizedMessage :" +p0.localizedMessage)
                        Log.i(TAG, "message :" +p0.message)
                    }
                })
                .addOnCompleteListener(object : OnCompleteListener<DataReadResponse>{
                    override fun onComplete(p0: Task<DataReadResponse>) {
                        Log.i(TAG, "readRequest_custom_" + "onComplete")
                    }
                })
                .addOnSuccessListener(object :OnSuccessListener<DataReadResponse>{
                    override fun onSuccess(p0: DataReadResponse?) {
                        Log.i(TAG, "readRequest_custom_" + "onSuccess")
                        printData(p0!!)
                    }
                })
        Tasks.await(response)
    }

    @SuppressLint("SimpleDateFormat")
    fun printData(dataReadResult: DataReadResponse) {
        Log.i(TAG+ "printData", "dataReadResult.getBuckets()" + dataReadResult.getBuckets().size.toString())
        Log.i(TAG+ "printData", "dataReadResult.getDataSets()" + dataReadResult.getDataSets().size.toString())
        Log.i(TAG+ "printData", "dataReadResult.status" + dataReadResult.status.toString())
        val label = SimpleDateFormat("MM/dd")
        var ia = 0
        ib =0
        if (dataReadResult.buckets.size > 0) {
            Log.i(TAG+ "printData", "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size)
            for (bucket: com.google.android.gms.fitness.data.Bucket in dataReadResult.getBuckets()) {
                for(dataset: com.google.android.gms.fitness.data.DataSet in bucket.dataSets) {
                    Log.i(TAG+ "printData", "dumpDataSet")
                    Log.i(TAG+ "printData", dataset.toString())
                    Log.i(TAG+ "printData", "Bucket point:");
                    Log.i(TAG+ "printData", "bucket : " + bucket.toString())
                    Log.i(TAG+ "printData", "\tStart: " + label.format(bucket.getStartTime(TimeUnit.MILLISECONDS)))
                    Log.i(TAG+ "printData", "\tEnd: " + label.format(bucket.getEndTime(TimeUnit.MILLISECONDS)))
                    Log.i(TAG+ "printData", "\tdataSets: " + bucket.dataSets.toString())
                    dumpDataSet(dataset)
                    ia += 1
                }
                Log.i(TAG+ "printData", "\tia : " + ia.toString())
            }
        } else if (dataReadResult.getDataSets().size > 0) {
            Log.i(TAG+ "printData", "Number of returned DataSets is: " + dataReadResult.getDataSets().size);
            for (dataSet: com.google.android.gms.fitness.data.DataSet in dataReadResult.getDataSets()) {
                dumpDataSet(dataSet)
                ia += 1
                Log.i(TAG+ "printData", "\tia : " + ia.toString())
            }
        }
    }
    @SuppressLint("SimpleDateFormat")
    fun dumpDataSet(dataSet:DataSet) {
        var count = 0
        val label = SimpleDateFormat("MM/dd")
        Log.i(TAG + "DataSet", dataSet.toString())
        Log.i(TAG + "DataSet", dataSet.dataPoints.size.toString())
        for ( dp :com.google.android.gms.fitness.data.DataPoint in dataSet.dataPoints)
        {
            Log.i(TAG+ "DataSet", "\tType: " + dp.dataType.name)
            Log.i(TAG+ "DataSet", "\tStart: " + label.format(dp.getStartTime(TimeUnit.MILLISECONDS)))
            Log.i(TAG+ "DataSet", "\tEnd: " + label.format(dp.getEndTime(TimeUnit.MILLISECONDS)))
            Log.i(TAG+ "DataSet", "\tField: " + dp.dataType.fields.toString())
            for (field:com.google.android.gms.fitness.data.Field in dp.dataType.fields)
            {
                Log.i(TAG+ "_DataSet", "\tField: " + field.name + " Value: " + dp.getValue(field))
                when(field.name){
                    "bmi" -> {
                        bmi_series.add(BarEntry(count.toFloat(), dp.getValue(field).asFloat()))
                        bmi_Label.add(label.format(Date(dp.getEndTime(TimeUnit.MILLISECONDS))))
                    }
                    "muscle" -> {
                        muscle_series.add(BarEntry(count.toFloat(), dp.getValue(field).asFloat()))
                        muscle_Label.add(label.format(Date(dp.getEndTime(TimeUnit.MILLISECONDS))))
                    }
                    "fat" -> {
                        fat_series.add(BarEntry(count.toFloat(), dp.getValue(field).asFloat()))
                        fat_Label.add(label.format(Date(dp.getEndTime(TimeUnit.MILLISECONDS))))
                    }
                    Field.FIELD_WEIGHT.name ->{
                        weight_series.add(BarEntry(count.toFloat(), dp.getValue(field).asFloat()))
                        weight_Label.add(label.format(Date(dp.getEndTime(TimeUnit.MILLISECONDS))))
                    }
                    Field.FIELD_CALORIES.name ->{
                        kcal_series.add(BarEntry(count.toFloat(), dp.getValue(field).asFloat()))
                        kcal_Label.add(label.format(Date(dp.getEndTime(TimeUnit.MILLISECONDS))))
                    }
                    Field.FIELD_STEPS.name ->{
                        walk_series.add(BarEntry(count.toFloat(), dp.getValue(field).asInt().toFloat()))
                        walk_Label.add(label.format(Date(dp.getEndTime(TimeUnit.MILLISECONDS))))
                    }
                }
                count += 1
            }
        }
    }
    @SuppressLint("StaticFieldLeak")
    private inner class fitTask : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void): Void? {
            Log.i(TAG, "doInBackground")
            readRequest_weight()
            readRequest_arr()
            if(custom_Type != null) {
                readRequest_custom()
            }
            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
            customDataType()
        }

    }
}



