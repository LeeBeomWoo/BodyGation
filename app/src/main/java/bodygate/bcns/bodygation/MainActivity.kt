package bodygate.bcns.bodygation

import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.NonNull
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import bodygate.bcns.bodygation.dummy.DummyContent
import bodygate.bcns.bodygation.navigationitem.*
import cn.gavinliu.android.lib.scale.config.ScaleConfig
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_follow.*
import kotlinx.android.synthetic.main.fragment_goal.*
import kotlinx.android.synthetic.main.toolbar_custom.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.anko.custom.async
import pub.devrel.easypermissions.EasyPermissions
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


@Suppress("DUPLICATE_LABEL_IN_WHEN")
class MainActivity() : AppCompatActivity(), GoalFragment.OnGoalInteractionListener, FollowFragment.OnFollowInteraction,
        ForMeFragment.OnForMeInteraction, MovieFragment.OnMovieInteraction, OnDataPointListener, Parcelable, YouTubeResult.OnYoutubeResultInteraction, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    override fun describeContents(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var sendcheck:Boolean = false
    private val PREF_ACCOUNT_NAME = "accountName"
    val REQUEST_ACCOUNT_PICKER = 1000
    private val REQUEST_OAUTH = 1001
    var mFitnessClient:GoogleApiClient? = null
    val ID: String? = null
    val PW: String? = null
    val TAG: String = "MainActivity_"
    private val RC_SIGN_IN = 111//google sign in request code
    private val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1
    var personUrl:Uri? = null
    var page = ""
    override var totalpage = 100
    private var mGoogleSignInClient: GoogleSignInClient? = null//google sign in client
    var mCredential: GoogleAccountCredential? = null
    var gsa :GoogleSignInAccount? = null
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
    override var kcalResponse: DataReadResponse? = null
    override var walkResponse: DataReadResponse? = null
    override var readResponse: DataReadResponse? = null
    override var customResponse: DataReadResponse? = null
    var customType:DataType? = null

    constructor(parcel: Parcel) : this() {
        sendcheck = parcel.readByte() != 0.toByte()
        personUrl = parcel.readParcelable(Uri::class.java.classLoader)
        page = parcel.readString()
        totalpage = parcel.readInt()
        gsa = parcel.readParcelable(GoogleSignInAccount::class.java.classLoader)
        SCOPES = parcel.readString()
        visableFragment = parcel.readString()
        doubleBackToExitPressedOnce = parcel.readByte() != 0.toByte()
        customType = parcel.readParcelable(DataType::class.java.classLoader)
    }


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

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        if (p0 != null) {
            p0.writeByte(if (sendcheck) 1 else 0)
            p0.writeParcelable(personUrl, p1)
            p0.writeString(page)
            p0.writeInt(totalpage)
            p0.writeParcelable(gsa, p1)
            p0.writeString(SCOPES)
            p0.writeString(visableFragment)
            p0.writeByte(if (doubleBackToExitPressedOnce) 1 else 0)
            p0.writeParcelable(customType, p1)
        }
    }

    override fun OnGoalInteractionListener(uri: Uri) {
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
        catch (e: NoSuchAlgorithmException) {
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
        Log.d(TAG + "_", "doGoogleSignIn")
        val signInIntent = mGoogleSignInClient?.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)//pass the declared request code here
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
        mGoogleSignInClient = buildGoogleSignInClient()
        Log.d(TAG + "_", "onCreate")
        //mPb = ProgressDialog(this)
        // pPb = ProgressDialog(this)
        // nPb = ProgressDialog(this)
        cPb = ProgressDialog(this)
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(ExponentialBackOff())
        if(GoogleSignIn.getLastSignedInAccount(this) == null){
            doGoogleSignIn()
        }else{
            getProfileInformation(GoogleSignIn.getLastSignedInAccount(this))
        }
        fitnessConectFun()
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
                                    .replace(R.id.root_layout, FollowFragment.newInstance(ID, PW), "rageComicList")
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
    fun fitnessConectFun(){
        Log.i(TAG, "fitnessConectFun")
        val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_WRITE)
                .build()
        mFitnessClient = GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.CONFIG_API)
                .addScope(Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(Scope(Scopes.FITNESS_NUTRITION_READ_WRITE))
                .addScope(Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
        mFitnessClient!!.connect()
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions)
        }else{
            launch { registerFitnessDataListener()}
        }
    }
    private fun handleSignInResult(completedTask:Task<GoogleSignInAccount>) {
        Log.i(TAG, "handleSignInResult")
        try {
            val acc = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            getProfileInformation(acc)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            getProfileInformation(null)
        }
    }
    private fun buildGoogleSignInClient(): GoogleSignInClient {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()//request email id
                .requestProfile()
                .requestId ()
                .requestIdToken(getString(R.string.server_client_id))
                //.requestServerAuthCode(getString(R.string.server_client_id))
                .requestScopes(Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .requestScopes(Scope(Scopes.FITNESS_NUTRITION_READ_WRITE))
                .requestScopes(Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .requestScopes(Scope(SCOPES))
                .build()
        return GoogleSignIn.getClient(this, signInOptions)
    }
    @SuppressLint("RestrictedApi")
    override fun onStart() {
        super.onStart()
        // Connect to the Fitness API
        Log.i(TAG, "Connecting...")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            REQUEST_ACCOUNT_PICKER -> {
                Log.i(TAG, "REQUEST_ACCOUNT_PICKER")
                if (resultCode == RESULT_OK && data.getExtras() != null) {
                    val accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        val settings =
                                getPreferences(Context.MODE_PRIVATE)
                        val editor = settings.edit()
                        editor.putString(PREF_ACCOUNT_NAME, accountName)
                        editor.apply()
                        mCredential!!.setSelectedAccountName(accountName)
                    }
                }
            }
            RC_SIGN_IN ->{
                Log.i(TAG, "RC_SIGN_IN")
                if (resultCode == RESULT_OK){
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    fitnessConectFun()
                    handleSignInResult(task)
                    launch {  registerFitnessDataListener()}
                }
            }
            GOOGLE_FIT_PERMISSIONS_REQUEST_CODE ->{
                Log.i(TAG, "GOOGLE_FIT_PERMISSIONS_REQUEST_CODE")
                if (resultCode == RESULT_OK){
                    launch {  registerFitnessDataListener()}
                }
            }
        }
    }
    override fun onConnected(p0: Bundle?) {
        Log.i(TAG, "onConnected")
        //Google Fit Client에 연결되었습니다.
        Log.i(TAG, p0.toString())
        launch { registerFitnessDataListener()}
    }
    override fun onConnectionSuspended(cause: Int) {
        Log.i(TAG, "onConnectionSuspended")
        // The connection has been interrupted. Wait until onConnected() is called.
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
    override fun makePersonalData(){
        Log.i("pendingResult", "makePersonalData")
        val cal = Calendar.getInstance()
        val now = Date()
        cal.time = now
        val nowTime = cal.timeInMillis
        launch(UI) {
            if (GoogleSignIn.getLastSignedInAccount(this@MainActivity) == null) {
                doGoogleSignIn()
            }else {
                if (customType == null) {
                    val pendingResult_custom = Fitness.getConfigClient(this@MainActivity, GoogleSignIn.getLastSignedInAccount(this@MainActivity)!!).readDataType("bodygate.bcns.bodygation.personal")
                            .addOnFailureListener(object : OnFailureListener {
                                override fun onFailure(p0: Exception) {
                                    if (sendcheck) {
                                        Log.i(TAG + "Failure", "onFailure" + p0.hashCode().toString())
                                        Log.i(TAG + "Failure", "onFailure" + p0.message)
                                        Log.i(TAG + "Failure", "onFailure" + p0.localizedMessage)
                                    } else {
                                        sendcheck = true
                                        launch(CommonPool) { makeData()}
                                        makePersonalData()
                                    }
                                }
                            })
                            .addOnSuccessListener(object : OnSuccessListener<DataType> {
                                override fun onSuccess(p0: DataType?) {
                                    sendcheck = false
                                    customType = p0
                                }

                            })
                    launch(CommonPool) { Tasks.await((pendingResult_custom)) }.join()
                    launch(CommonPool) { makePersonalData() }.join()
                } else {
                    val source = DataSource.Builder()
                            .setName("bodygate.bcns.bodygation.personal")
                            .setDataType(customType!!)
                            .setAppPackageName(this@MainActivity.context.packageName)
                            .setType(DataSource.TYPE_DERIVED)
                            .build()
                    val dataPoint = DataPoint.create(source)
                    // Set values for the data point
                    // This data type has two custom fields (int, float) and a common field
                    for (s: Int in 0..(customType!!.fields.size - 1)) {
                        when (customType!!.fields[s].name) {
                            "bmi" -> {
                                dataPoint.getValue(customType!!.fields[s]).setFloat(my_bmi_txtB.text.toString().toFloat())
                            }
                            "muscle" -> {
                                dataPoint.getValue(customType!!.fields[s]).setFloat(my_musclemass_txtB.text.toString().toFloat())
                            }
                            "fat" -> {
                                dataPoint.getValue(customType!!.fields[s]).setFloat(my_bodyfat_txtB.text.toString().toFloat())
                            }
                        }
                    }
                    dataPoint.setTimestamp(nowTime, TimeUnit.MILLISECONDS)
                    val dataSet = DataSet.create(source)
                    dataSet.add(dataPoint)
                    val response = Fitness.getHistoryClient(this@MainActivity, GoogleSignIn.getLastSignedInAccount(this@MainActivity)!!).insertData(dataSet)
                            .addOnFailureListener(object : OnFailureListener {
                                override fun onFailure(p0: Exception) {
                                        Log.i(TAG + "Failure", "onFailure" + p0.hashCode().toString())
                                        Log.i(TAG + "Failure", "onFailure" + p0.message)
                                        Log.i(TAG + "Failure", "onFailure" + p0.localizedMessage)
                                }
                            })
                            .addOnSuccessListener {
                            }
                    launch(CommonPool) { Tasks.await(response) }.join()
                    if(response.isSuccessful){
                        launch (UI){
                            Toast.makeText(this@MainActivity, "자료가 성공적으로 업데이트 되었습니다. 다음달에도 꼭 등록하여 주세요~^^", Toast.LENGTH_SHORT).show()
                            bottom_navigation.currentItem = 1 }.join()
                    }
                }
            }
        }
    }
   suspend fun makeData()= launch{
        Log.i(TAG, "makeData")
        val request = DataTypeCreateRequest.Builder()
    // The prefix of your data type name must match your app's package name
                .setName("bodygate.bcns.bodygation.personal")
                .addField("bmi", Field.FORMAT_FLOAT)
                .addField("muscle", Field.FORMAT_FLOAT)
                .addField("fat", Field.FORMAT_FLOAT)
                .build()
// 2. Invoke the Config API with:
// - The Google API client object
// - The create data type request
val pendingResult = ConfigApi.createCustomDataType(mFitnessClient, request)
        pendingResult.setResultCallback(
                object: ResultCallback<DataTypeResult> {
                    override fun onResult(p0: DataTypeResult) {
                        customType = p0.getDataType()
                    }
                }
        )
        launch(CommonPool) {customType = pendingResult.await().dataType}.join()
    }
    override fun onDataPoint(dataPoint: DataPoint) {
        Log.i(TAG, "onDataPoint")
        // Do cool stuff that matters. 중요한 것을 멋지게 처리하십시오.
        for (field: Field in dataPoint.getDataType().getFields()) {
            val value = dataPoint.getValue(field);
        }
    }

 suspend fun registerFitnessDataListener() {
      Log.i(TAG, "registerFitnessDataListener")
        val cal = Calendar.getInstance()
        val now = Date()
        val endTime = now.time
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        cal.set(2015, 1, 1)
        val startTime = cal.timeInMillis
        Log.i(TAG, "Range Start: " + startTime.toString())
        Log.i(TAG, "Range End: " + endTime.toString())
      if(GoogleSignIn.getLastSignedInAccount(this@MainActivity) == null){
          doGoogleSignIn()
      }else {
          if(!sendcheck){
              launch(CommonPool) { makeData()}.join()
              sendcheck = true
              launch(CommonPool){registerFitnessDataListener()}
          }else{
              val pendingResult_custom = ConfigApi.readDataType(mFitnessClient, "bodygate.bcns.bodygation.personal")
              launch(CommonPool) {
                  val taype = pendingResult_custom.await().dataType
                  if (taype != null) {
                      val response_second = Fitness.getHistoryClient(this@MainActivity, GoogleSignIn.getLastSignedInAccount(this@MainActivity)!!)
                              .readData(DataReadRequest.Builder()
                                      .read(taype)
                                      .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                                      .build())
                              .addOnSuccessListener(object : OnSuccessListener<DataReadResponse> {
                                  override fun onSuccess(p0: DataReadResponse?) {
                                      Log.i(TAG, "customResponse_onSuccess")
                                      Log.i(TAG, p0.toString())
                                  }

                              })
                              .addOnFailureListener(object : OnFailureListener {
                                  override fun onFailure(p0: Exception) {
                                      Log.i(TAG, "customResponse :" + p0.message.toString())
                                      if (sendcheck) {
                                          Log.i(TAG + "Failure", "onFailure" + p0.hashCode().toString())
                                          Log.i(TAG + "Failure", "onFailure" + p0.message)
                                          Log.i(TAG + "Failure", "onFailure" + p0.localizedMessage)
                                      } else {
                                          launch(CommonPool) { makeData()}
                                          sendcheck = true
                                          launch(CommonPool){registerFitnessDataListener()}
                                      }
                                  }
                              })
                              .addOnCompleteListener(object : OnCompleteListener<DataReadResponse> {
                                  override fun onComplete(p0: Task<DataReadResponse>) {
                                      customResponse = p0.result
                                  }
                              })
                      launch(CommonPool){Tasks.await(response_second)}.join()
                  }
              }.join()
              launch(CommonPool) {
                  val response = Fitness.getHistoryClient(this@MainActivity, GoogleSignIn.getLastSignedInAccount(this@MainActivity)!!)
                          .readData(DataReadRequest.Builder()
                                  .read(DataType.TYPE_WEIGHT)
                                  .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                                  .build())
                          .addOnSuccessListener(object : OnSuccessListener<DataReadResponse> {
                              override fun onSuccess(p0: DataReadResponse?) {
                                  Log.i(TAG, "readResponse_onSuccess")
                                  Log.i(TAG, p0.toString())
                              }

                          })
                          .addOnFailureListener(object : OnFailureListener {
                              override fun onFailure(p0: Exception) {
                                  Log.i(TAG, "readResponse :" + p0.message.toString())
                              }
                          })
                          .addOnCompleteListener(object : OnCompleteListener<DataReadResponse> {
                              override fun onComplete(p0: Task<DataReadResponse>) {
                                  readResponse = p0.result
                              }
                          })
                  launch(CommonPool) { Tasks.await(response) }.join()
              }.join()
              launch(CommonPool) {
                  val response_ds = DataReadRequest.Builder()
                          .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                          .bucketByTime(1, TimeUnit.DAYS)
                          .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                          .build()
                  val sss = Fitness.getHistoryClient(this@MainActivity, GoogleSignIn.getLastSignedInAccount(this@MainActivity)!!)
                          .readData(response_ds)
                          .addOnSuccessListener(object : OnSuccessListener<DataReadResponse> {
                              override fun onSuccess(p0: DataReadResponse?) {
                                  Log.i(TAG, "walkResponse_onSuccess")
                                  Log.i(TAG, p0.toString())
                              }

                          })
                          .addOnFailureListener(object : OnFailureListener {
                              override fun onFailure(p0: Exception) {
                                  Log.i(TAG, "walkResponse :" + p0.message.toString())
                                  Log.i(TAG, p0.toString())
                              }
                          })
                          .addOnCompleteListener(object : OnCompleteListener<DataReadResponse> {
                              override fun onComplete(p0: Task<DataReadResponse>) {
                                  walkResponse = p0.result
                              }

                          })
                  launch(CommonPool) { Tasks.await(sss) }.join()
              }.join()
              launch(CommonPool) {
                  val response_dc = DataReadRequest.Builder()
                          .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                          .bucketByTime(1, TimeUnit.DAYS)
                          .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                          .build()
                  val ccc = Fitness.getHistoryClient(this@MainActivity, GoogleSignIn.getLastSignedInAccount(this@MainActivity)!!)
                          .readData(response_dc)
                          .addOnSuccessListener(object : OnSuccessListener<DataReadResponse> {
                              override fun onSuccess(p0: DataReadResponse?) {
                                  Log.i(TAG, "kcalResponse_onSuccess")
                              }

                          })
                          .addOnFailureListener(object : OnFailureListener {
                              override fun onFailure(p0: Exception) {
                                  Log.i(TAG, "kcalResponse :" + p0.message.toString())
                              }
                          })
                          .addOnCompleteListener(object : OnCompleteListener<DataReadResponse> {
                              override fun onComplete(p0: Task<DataReadResponse>) {
                                  kcalResponse = p0.result
                              }
                          })
                  launch(CommonPool) { Tasks.await(ccc) }.join()
              }.join()
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
            personUrl = acct.photoUrl
            launch { makeData()}
            Log.i("profile", personName + "\t" + acct.photoUrl.toString() + "\t" + personGivenName +"\t" + personEmail +"\t" + personId + "\t" + acct.toString())
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

    companion object CREATOR : Parcelable.Creator<MainActivity> {
        override fun createFromParcel(parcel: Parcel): MainActivity {
            return MainActivity(parcel)
        }

        override fun newArray(size: Int): Array<MainActivity?> {
            return arrayOfNulls(size)
        }
    }

}



