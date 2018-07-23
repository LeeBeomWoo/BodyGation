package bodygate.bcns.bodygation

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.PopupMenu
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import bodygate.bcns.bodygation.dummy.DummyContent
import bodygate.bcns.bodygation.navigationitem.*
import bodygate.bcns.bodygation.support.MainPageAdapter
import cn.gavinliu.android.lib.scale.config.ScaleConfig
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.DataTypeCreateRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchListResponse
import com.google.api.services.youtube.model.SearchResult
import com.google.common.io.BaseEncoding
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_follow.*
import kotlinx.android.synthetic.main.fragment_for_me.*
import kotlinx.android.synthetic.main.fragment_goal.*
import kotlinx.android.synthetic.main.maintablayout.*
import kotlinx.android.synthetic.main.toolbar_custom.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.coroutines.experimental.Continuation


@Suppress("DUPLICATE_LABEL_IN_WHEN", "CAST_NEVER_SUCCEEDS")
class MainActivity() : AppCompatActivity(), GoalFragment.OnGoalInteractionListener, FollowFragment.OnFollowInteraction,
        ForMeFragment.OnForMeInteraction, MovieFragment.OnMovieInteraction, YouTubeResult.OnYoutubeResultInteraction, GoogleApiClient.OnConnectionFailedListener, MainTabFragment.mainTab {
    val LIST_STATE_KEY:String = "recycler_list_state";
    var listState: Parcelable? = null
    private val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1
    private val REQUEST_OAUTH = 1001
    var menu_isShow:Boolean = false
    var fitloading:Boolean = false
    var fitcomp:Boolean = false
    var fitsending:Boolean = false
    val ID: String? = null
    val PW: String? = null
    val TAG: String = "MainActivity_"
    var personUrl:Uri? = null
    var mPopupWindow: PopupMenu? = null
    var page = ""
    override var totalpage = 100
    lateinit var mGoogleSignInClient: GoogleSignInClient//google sign in client
    var mAuth: FirebaseAuth? = null
    var email: String? = null
    var mPb:ProgressDialog? = null
    var pPb:ProgressDialog? = null
    var nPb:ProgressDialog? = null
    var cPb:ProgressDialog? = null
    var pB: ProgressDialog? = null
    override var visableFragment = ""
    private var doubleBackToExitPressedOnce: Boolean = false
    override val context:Context = this
    override var sendquery:ArrayList<String>? = null
    override var data: MutableList<SearchResult> = arrayListOf()

    var custom_Type:DataType? = null

    override val weight_series: MutableList<BarEntry> = ArrayList()
    override val muscle_series: MutableList<BarEntry> = ArrayList()
    override val walk_series: MutableList<BarEntry> = ArrayList()
    override val fat_series: MutableList<BarEntry> = ArrayList()
    override val bmi_series: MutableList<BarEntry> = ArrayList()
    override val kcal_series: MutableList<BarEntry> = ArrayList()

    override val weight_Label:MutableList<String> =  ArrayList()
    override val kcal_Label:MutableList<String> =  ArrayList()
    override val walk_Label:MutableList<String> =  ArrayList()
    override val fat_Label:MutableList<String> =  ArrayList()
    override val muscle_Label:MutableList<String> =  ArrayList()
    override val bmi_Label:MutableList<String> =  ArrayList()
    var ib = 0

    var goalFragment:Fragment? = null
    override var followFragment:Fragment? = null
    override var forMeFragment:Fragment? = null
    var youTubeResult:YouTubeResult? = null
    var mainTabFragment:Fragment? = null
    override var tabadapter: MainPageAdapter? = null
    var tabpage =0

    override fun stopProgress(i:Int) {
        when(i) {
            0-> if (mPb!!.isShowing) {
                Log.i(TAG, "stopProgress")
                mPb!!.dismiss()
            }
            1->    if(cPb!!.isShowing) {
                Log.i(TAG, "stopProgress")
                cPb!!.dismiss()
            }
            2-> if (pPb!!.isShowing) {
                Log.i(TAG, "stopProgress")
                pPb!!.dismiss()
            }
            3->   if(nPb!!.isShowing) {
                Log.i(TAG, "stopProgress")
                nPb!!.dismiss()
            }
        }
    }
    fun startProgress(i:Int) {
        when(i) {
            0-> if(mPb == null) {
                mPb = ProgressDialog.show(this, "데이터 받기", "유튜브 데이터를 받아오는 중입니다")
                mPb!!.setCancelable(false)
            }else if(!mPb!!.isShowing) {
                    Log.i(TAG, "startProgress")
                    mPb!!.show()
            }
            1->  if(cPb == null) {
                cPb = ProgressDialog.show(this, "데이터 받기", "피트니스 데이터를 받아오는 중입니다")
                cPb!!.setCancelable(false)
            }else if(!cPb!!.isShowing) {
                Log.i(TAG, "startProgress")
                cPb!!.show()
            }

            2-> if(pPb == null) {
                pPb = ProgressDialog.show(this, "데이터 받기", "유튜브 데이터를 받아오는 중입니다")
                pPb!!.setCancelable(false)
            }else if (!pPb!!.isShowing) {
                    Log.i(TAG, "startProgress")
                    pPb!!.show()
            }
            3->  if(nPb == null) {
                nPb = ProgressDialog.show(this, "데이터 받기", "유튜브 데이터를 받아오는 중입니다")
                nPb!!.setCancelable(false)
            }else if (!nPb!!.isShowing) {
                    Log.i(TAG, "startProgress")
                    nPb!!.show()
            }
        }
    }

    fun addData(response: SearchListResponse) {
        Log.i(TAG, "addData")
        Log.i("test", "second")
        val body = response.items
        Log.i("query", body.toString())
        Log.i("response", response.toString())
        if(response.nextPageToken != null) {
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
            if(page != "") {
                query.setPageToken(page)
            }
            query.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url), nextPageToken, pageInfo")
            query.setQ(q)
            query.setOrder(order)
            query.setMaxResults(max_result.toLong())
            launch(CommonPool) {
                val body = query.execute()
                if(body.nextPageToken != null && body.nextPageToken != ""){
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
        if(mGoogleSignInClient.silentSignIn().isSuccessful){
            val acc = mGoogleSignInClient.silentSignIn().result
            launch {
            launch(UI) {
                pB = ProgressDialog.show(this@MainActivity, "데이터 보내기", "구글 핏으로 부터 데이터를 보내는 중입니다...")
                fitsending = true
                if(insertData(acc) == null) {
                    insertData(acc)
                }
                if(my_weight_txtB.text.toString() != ""){
                    insert_second(acc)
                }
                pB!!.dismiss()
                fitsending = false
            }.join()
            }
        }
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
        launch(CommonPool) {addData( query.execute())}.join()
        Log.i("test", "third")
    }
    override fun OnFollowInteraction(q: ArrayList<String>?, s:Int) {
        Log.i(TAG, "OnFollowInteraction")
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.root_layout, YouTubeResult.newInstance(), "youtube")
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
        when (viewPager.currentItem) {
            0 -> {
                doubleBackToExitPressedOnce = false
                viewPager.currentItem = 1
            }
            1 -> {
                if (visableFragment == "YouTubeResult") {
                    sendquery = null
                    data.clear()
                    viewPager.setCurrentItem(1)
                } else {
                    if (doubleBackToExitPressedOnce) {
                        moveTaskToBack(true)
                        android.os.Process.killProcess(android.os.Process.myPid())
                        System.exit(1)
                        return
                    } else {
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
            2 -> {
                doubleBackToExitPressedOnce = false
                viewPager.currentItem = 1
            }
        }
    }
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScaleConfig.create(this,
                1080, // Design Width
                1920, // Design Height
                (3).toFloat(),    // Design Density
                (3).toFloat(),    // Design FontScale
                ScaleConfig.DIMENS_UNIT_DP);
        setContentView(R.layout.activity_main)
        Log.i(TAG + "_", "onCreate")
        //mPb = ProgressDialog(this)
        // pPb = ProgressDialog(this)
        // nPb = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        cPb = ProgressDialog(this)
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
                .build()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestProfile()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestScopes(Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .requestScopes(Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addExtension(fitnessOptions)
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        followFragment = supportFragmentManager.findFragmentByTag("follow") as FollowFragment?
        youTubeResult = supportFragmentManager.findFragmentByTag("youtube") as YouTubeResult?
        forMeFragment = supportFragmentManager.findFragmentByTag("forme") as ForMeFragment?
        goalFragment = supportFragmentManager.findFragmentByTag("goal") as GoalFragment?
        mainTabFragment = supportFragmentManager.findFragmentByTag("main") as MainTabFragment?
        tabadapter = MainPageAdapter(supportFragmentManager)
        if(mGoogleSignInClient.silentSignIn().isSuccessful){
            accessGoogleFit(mGoogleSignInClient.silentSignIn().result)
        }else{
            signIn()
        }
        if(savedInstanceState == null ) {
            tabpage = 1
        }else{
            tabpage = savedInstanceState.getInt("section")
        }
        if (mainTabFragment == null) {
            Log.i(TAG, "mainTabFragment")
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.root_layout, MainTabFragment.newInstance(personUrl.toString()), "main")
                    .commit()
        }else{
            supportFragmentManager
                    .beginTransaction().replace(R.id.root_layout, mainTabFragment!!).commit()
        }
        toolbarhome.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                if(sendquery != null)
                    sendquery = null
                data.clear()
                viewPager.setCurrentItem(1)
            }

        })
        toolbarmenu.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                //팝업메뉴
                if(!menu_isShow) {
                    menupopup(v!!)
                }
            }

        })
    }
    private fun signIn() {
        Log.i(TAG, "signIn")
        val signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_OAUTH)
    }
    private fun doGoogleSignOut() {
        mGoogleSignInClient.signOut()?.addOnCompleteListener(this, {
            Toast.makeText(this, "Google Sign Out done.", Toast.LENGTH_SHORT).show()
            revokeAccess()
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
        mGoogleSignInClient.revokeAccess()?.addOnCompleteListener(this, {
            Toast.makeText(this, "Google access revoked.", Toast.LENGTH_SHORT).show()
            getProfileInformation(null)
        })
    }
    override fun onStart() {
        super.onStart()
        // Connect to the Fitness API
        Log.i(TAG, "Connecting...")
       val currentUser = mAuth!!.getCurrentUser()
        if(currentUser != null)
        getProfileInformation(currentUser)
    }
    fun ReadData(acc:GoogleSignInAccount) {
        readRequest_weight(acc).continueWithTask(object: com.google.android.gms.tasks.Continuation<DataReadResponse, Task<DataReadResponse>> {
            override fun then(p0: Task<DataReadResponse>): Task<DataReadResponse> {
                return readRequest_arr(acc)
            }
        })
    }
    fun customReadData(acc:GoogleSignInAccount) {
        customDataType(acc).continueWithTask(object: com.google.android.gms.tasks.Continuation<DataType, Task<DataReadResponse>> {
            override fun then(p0: Task<DataType>): Task<DataReadResponse> {
                return readRequest_custom(acc, p0.result)
            }
        })
    }
    fun accessGoogleFit(acc:GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(acc.idToken, null)
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(object : OnCompleteListener<AuthResult>{
                    override fun onComplete(p0: Task<AuthResult>) {
                        if(p0.isSuccessful){
                            startProgress(1)
                            val user = mAuth!!.currentUser
                            getProfileInformation(user!!)
                            launch(UI) {
                            ReadData(acc)
                            customReadData(acc)
                            }
                            /*
                            launch (UI) {
                                startProgress(1)
                                fitloading = true
                                Log.i(TAG, "readRequest_weight before")
                                val a = readRequest_weight(acc)
                                Log.i(TAG, "readRequest_weight after")
                                Log.i(TAG, "readRequest_arr before")
                                val b = readRequest_arr(acc)
                                Log.i(TAG, "readRequest_arr after")
                                Log.i(TAG, "customDataType before")
                                val c = customDataType(acc)
                                Log.i(TAG, "customDataType after")
                                fitloading = false
                                fitcomp = true
                            }*/
                        }else{
                            getProfileInformation(null)
                        }
                    }
                })
    }


    override fun onStop() {
        super.onStop()
    }
    fun setDataType(type:DataType){
        custom_Type = type
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            GOOGLE_FIT_PERMISSIONS_REQUEST_CODE -> {
                Log.i(TAG, "GOOGLE_FIT_PERMISSIONS_REQUEST_CODE")
                if (resultCode == Activity.RESULT_OK) {
                    accessGoogleFit(GoogleSignIn.getLastSignedInAccount(this)!!)
                }
            }
            REQUEST_OAUTH -> {
                Log.i(TAG, "REQUEST_OAUTH")
                if (resultCode == Activity.RESULT_OK) {
                    // Make sure the app is not already connected or attempting to connectTask<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    handleSignInResult(task)
                    Log.i(TAG, data.toString())
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putBoolean("loading", fitloading)
        outState.putBoolean("sending", fitsending)
        if(viewPager != null)
        outState.putInt("section", viewPager.currentItem)
        if(fitsending) {
            outState.putString("bmi", my_bmi_txtB.text.toString())
            outState.putString("weight", my_weight_txtB.text.toString())
            outState.putString("muscle", my_musclemass_txtB.text.toString())
            outState.putString("fat", my_bodyfat_txtB.text.toString())
        }
        if(youTubeResult != null) {
            listState = result_list.layoutManager!!.onSaveInstanceState();
            outState.putParcelable(LIST_STATE_KEY, listState);
        }
    }

    override fun onResume() {
        super.onResume()
        if (listState != null) {
            result_list.layoutManager!!.onRestoreInstanceState(listState);
        }
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if(savedInstanceState != null) {
            listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
            fitloading = savedInstanceState.getBoolean("loading")
            fitsending = savedInstanceState.getBoolean("sending")
                if(mGoogleSignInClient.silentSignIn().isSuccessful){
                    val acc = mGoogleSignInClient.silentSignIn().result

                    if(fitsending) {
                        my_bmi_txtB.text = savedInstanceState.getString("bmi")
                        my_weight_txtB.setText(savedInstanceState.getString("weight"), TextView.BufferType.EDITABLE)
                        my_musclemass_txtB.setText(savedInstanceState.getString("muscle"), TextView.BufferType.EDITABLE)
                        my_bodyfat_txtB.setText(savedInstanceState.getString("fat"), TextView.BufferType.EDITABLE)
                    launch {
                    launch(UI) {
                        pB = ProgressDialog.show(this@MainActivity, "데이터 보내기", "구글 핏으로 부터 데이터를 보내는 중입니다...")

                        if(insertData(acc) == null) {
                            insertData(acc)
                        }
                        if(my_weight_txtB.text.toString() != ""){
                            insert_second(acc)
                        }
                        pB!!.dismiss()
                        fitsending = false
                    }.join()
                }
            }else if(fitloading){
                accessGoogleFit(acc)
            }
        }
        }

    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            accessGoogleFit(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            getProfileInformation(null)
        }

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

    override fun onPause() {
        super.onPause()
        if(pB != null)
            if(pB!!.isShowing)
                pB!!.dismiss()
    }

    private fun getProfileInformation(acct: FirebaseUser?) {
        //if account is not null fetch the information
        if (acct != null) {
            //user display name
            val personName = acct.getDisplayName()


            //user email id
            val personEmail = acct.getEmail()

            //user profile pic
            personUrl = acct.photoUrl
            Log.i("profile", "getProfileInformation : " + personName + "\t" + acct.photoUrl.toString() + "\t"+ personEmail +"\t" + acct.toString())
        }
    }

   fun makeData(acc:GoogleSignInAccount):Task<DataType>{
        Log.i(TAG, "makeData")
       Log.i(TAG, "custom_routine" + "makeData")
        val request = DataTypeCreateRequest.Builder()
                // The prefix of your data type name must match your app's package name
                .setName("bodygate.bcns.bodygation.personal")
                .addField("bmi", Field.FORMAT_FLOAT)
                .addField("muscle", Field.FORMAT_FLOAT)
                .addField("fat", Field.FORMAT_FLOAT)
                .build()
        return Fitness.getConfigClient(this, acc).createCustomDataType(request)
                .addOnSuccessListener(object :OnSuccessListener<DataType>{
                    override fun onSuccess(p0: DataType?) {
                        Log.i(TAG, "makeData onSuccess")
                        custom_Type = p0
                    }
                })
                .addOnFailureListener(object :OnFailureListener{
                    override fun onFailure(p0: Exception) {
                        Log.i(TAG, "makeData OnFailureListener")
                        Log.i(TAG, p0.message)
                    }
                })
                .addOnCompleteListener(object :OnCompleteListener<DataType>{
                    override fun onComplete(p0: Task<DataType>) {
                        Log.i(TAG, "makeData OnCompleteListener")

                    }
                })
    }

    suspend fun insert_second(acc:GoogleSignInAccount):Task<Void>{
        val cal = Calendar.getInstance()
        val now = Date()
        cal.time = now
        val nowTime = cal.timeInMillis
        val weight_source = DataSource.Builder()
                .setDataType(DataType.TYPE_WEIGHT)
                .setAppPackageName(this@MainActivity.context.packageName)
                .setType(DataSource.TYPE_RAW)
                .build()
        val weight_dataPoint = DataPoint.create(weight_source)
        weight_dataPoint.setTimestamp(nowTime,  TimeUnit.MILLISECONDS)
        weight_dataPoint.getValue(Field.FIELD_WEIGHT).setFloat(my_weight_txtB.text.toString().toFloat())
        val weight_Set = DataSet.create(weight_source)
        weight_Set.add(weight_dataPoint)
        return Fitness.getHistoryClient(this, acc).insertData(weight_Set)
                .addOnSuccessListener(object :OnSuccessListener<Void>{
                    override fun onSuccess(p0: Void?) {
                        Log.i(TAG, "insert_second onSuccess")
                    }
                })
                .addOnFailureListener(object :OnFailureListener{
                    override fun onFailure(p0: Exception) {
                        Log.i(TAG, "insert_second OnFailureListener")
                        Log.i(TAG, p0.message)
                    }
                })
                .addOnCompleteListener(object :OnCompleteListener<Void>{
                    override fun onComplete(p0: Task<Void>) {
                        Log.i(TAG, "insertData OnCompleteListener")
                        Toast.makeText(this@MainActivity, "전송이 완료되었습니다 매달 체크하여 건강하고 행복하세요~", Toast.LENGTH_SHORT).show()
                        accessGoogleFit(acc)
                        viewPager.currentItem = 1
                    }
                })
    }
    suspend fun insertData(acc:GoogleSignInAccount):Task<Void>? {
        val cal = Calendar.getInstance()
        val now = Date()
        cal.time = now
        val nowTime = cal.timeInMillis
        // Create a new dataset and insertion request.
            if (custom_Type == null) {
                launch(UI) {
                val a = makeData(acc)
                }
                return null
            }else {
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

                return Fitness.getHistoryClient(this, acc).insertData(dataSet)
                        .addOnSuccessListener(object : OnSuccessListener<Void> {
                            override fun onSuccess(p0: Void?) {
                                Log.i(TAG, "insertData onSuccess")

                            }
                        })
                        .addOnFailureListener(object : OnFailureListener {
                            override fun onFailure(p0: Exception) {
                                Log.i(TAG, "insertData OnFailureListener")
                                Log.i(TAG, p0.message)
                                launch(UI) {
                                    val a = makeData(acc)
                                }

                            }
                        })
                        .addOnCompleteListener(object : OnCompleteListener<Void> {
                            override fun onComplete(p0: Task<Void>) {
                                Log.i(TAG, "insertData OnCompleteListener")
                            }
                        })
            }
    }

    fun customDataType(acc:GoogleSignInAccount):Task<DataType>{
        Log.i(TAG, "customDataType")
        Log.i(TAG, "custom_routine" + "customDataType")
        return Fitness.getConfigClient(this, acc).readDataType("bodygate.bcns.bodygation.personal")
                .addOnSuccessListener(object :OnSuccessListener<DataType>{
                    override fun onSuccess(p0: DataType?) {
                        Log.i(TAG, "customDataType onSuccess")
                        //readRequest_custom(acc, p0!!)
                    }
                })
                .addOnFailureListener(object :OnFailureListener{
                    override fun onFailure(p0: Exception) {
                        Log.i(TAG, "customDataType OnFailureListener")
                        Log.i(TAG, p0.message)
                            val a = makeData(acc)
                        if(a.isSuccessful) {
                            val m = customDataType(acc)
                        }
                    }
                })
                .addOnCompleteListener(object :OnCompleteListener<DataType>{
                    override fun onComplete(p0: Task<DataType>) {
                        Log.i(TAG, "customDataType OnCompleteListener")
                    }
                })
    }

  fun readRequest_weight(acc:GoogleSignInAccount) :Task<DataReadResponse> {
        Log.i(TAG, "readRequest_weight")
        val cal = Calendar.getInstance()
        val now = Date()
        val endTime = now.time
        cal.set(Calendar.DAY_OF_YEAR, -400)
        val startTime = cal.timeInMillis
        val request = DataReadRequest.Builder()
                .read(DataType.TYPE_WEIGHT)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build()
        return Fitness.getHistoryClient(this, acc).readData(request)
                .addOnSuccessListener(object :OnSuccessListener<DataReadResponse>{
                    override fun onSuccess(p0: DataReadResponse) {
                        printData(p0)
                    }
                })
                .addOnFailureListener(object :OnFailureListener{
                    override fun onFailure(p0: Exception) {
                        Log.i(TAG, "readRequest_weight OnFailureListener")
                        Log.i(TAG, p0.message)
                    }
                })
                .addOnCompleteListener(object :OnCompleteListener<DataReadResponse>{
                    override fun onComplete(p0: Task<DataReadResponse>) {
                        Log.i(TAG, "readRequest_weight OnCompleteListener")

                    }
                })
    }
   fun readRequest_arr(acc:GoogleSignInAccount):Task<DataReadResponse>{
        Log.i(TAG, "readRequest_AGGREGATE")
        val cal = Calendar.getInstance()
        val now = Date()
        val endTime = now.time
        cal.set(Calendar.DAY_OF_YEAR, -400)
        val startTime = cal.timeInMillis
        val request = DataReadRequest.Builder()
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build()
        return Fitness.getHistoryClient(this, acc).readData(request)
                .addOnSuccessListener(object :OnSuccessListener<DataReadResponse>{
                    override fun onSuccess(p0: DataReadResponse) {
                        printData(p0)
                    }
                })
                .addOnFailureListener(object :OnFailureListener{
                    override fun onFailure(p0: Exception) {
                        Log.i(TAG, "readRequest_arr OnFailureListener")
                        Log.i(TAG, p0.message)
                    }
                })
                .addOnCompleteListener(object :OnCompleteListener<DataReadResponse>{
                    override fun onComplete(p0: Task<DataReadResponse>) {
                        Log.i(TAG, "readRequest_arr OnCompleteListener")
                    }
                })
    }
   fun readRequest_custom(acc:GoogleSignInAccount, type:DataType):Task<DataReadResponse>{
        Log.i(TAG, "readRequest_custom")
        Log.i(TAG, "custom_routine" + "readRequest_custom")
        val cal = Calendar.getInstance()
        val now = Date()
        val endTime = now.time
        cal.set(Calendar.DAY_OF_YEAR, -400)
        val startTime = cal.timeInMillis
        val request = DataReadRequest.Builder()
                .read(type)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build()
       return Fitness.getHistoryClient(this, acc).readData(request)
                .addOnSuccessListener(object :OnSuccessListener<DataReadResponse>{
                    override fun onSuccess(p0: DataReadResponse) {
                        printData(p0)
                    }
                })
                .addOnFailureListener(object :OnFailureListener{
                    override fun onFailure(p0: Exception) {
                        Log.i(TAG, "readRequest_custom OnFailureListener")
                        Log.i(TAG, p0.message)
                    }
                })
                .addOnCompleteListener(object :OnCompleteListener<DataReadResponse>{
                    override fun onComplete(p0: Task<DataReadResponse>) {
                        Log.i(TAG, "readRequest_custom OnCompleteListener")
                        stopProgress(1)
                    }
                })
    }

    @SuppressLint("SimpleDateFormat")
    fun printData(dataReadResult: DataReadResponse) {
        ib =0
        Log.i(TAG, "ptrintdata")
        if (dataReadResult.buckets.size > 0) {
            for (bucket: com.google.android.gms.fitness.data.Bucket in dataReadResult.buckets) {
                for(dataset: com.google.android.gms.fitness.data.DataSet in bucket.dataSets) {
                    dumpDataSet(dataset)
                    ib += 1
                }
            }
        } else if (dataReadResult.dataSets.size > 0) {
            for (dataSet: com.google.android.gms.fitness.data.DataSet in dataReadResult.dataSets) {
                dumpDataSet(dataSet)
            }
        }
    }
    @SuppressLint("SimpleDateFormat")
    fun dumpDataSet(dataSet:DataSet) {
        Log.i(TAG, "dumpDataSet")
            Log.i(TAG, dataSet.toString())
        var count = 0
        val label = SimpleDateFormat("MM/dd")
        for ( dp :com.google.android.gms.fitness.data.DataPoint in dataSet.dataPoints)
        {
            Log.i(TAG, "Data point:");
            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            for (field:com.google.android.gms.fitness.data.Field in dp.dataType.fields)
            {
                Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
                when(field.name){
                    "bmi" -> {
                        bmi_series.add(BarEntry(count.toFloat(), dp.getValue(field).asFloat()))
                        bmi_Label.add(label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                        Log.i(TAG, "bmi"  + dp.getValue(field).asFloat().toString() + ", " + bmi_Label.last())
                    }
                    "muscle" -> {
                        muscle_series.add(BarEntry(count.toFloat(), dp.getValue(field).asFloat()))
                        muscle_Label.add(label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                        Log.i(TAG, "muscle"  + dp.getValue(field).asFloat().toString() + ", " + muscle_Label.last())
                    }
                    "fat" -> {
                        fat_series.add(BarEntry(count.toFloat(), dp.getValue(field).asFloat()))
                        fat_Label.add(label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                        Log.i(TAG, "fat"  + dp.getValue(field).asFloat().toString() + ", " + fat_Label.last())
                    }
                    Field.FIELD_WEIGHT.name ->{
                        weight_series.add(BarEntry(count.toFloat(), dp.getValue(field).asFloat()))
                        weight_Label.add(label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                        Log.i(TAG, "weight"  + dp.getValue(field).asFloat().toString() + ", " + weight_Label.last())
                    }
                    Field.FIELD_CALORIES.name ->{
                        kcal_series.add(BarEntry(ib.toFloat(), dp.getValue(field).asFloat()))
                        kcal_Label.add(label.format(dp.getEndTime(TimeUnit.MILLISECONDS)))
                        Log.i(TAG, " kcal"  + dp.getValue(field).asFloat().toString() + ", " + kcal_Label.last())
                    }
                    Field.FIELD_STEPS.name ->{
                        walk_series.add(BarEntry(ib.toFloat(), dp.getValue(field).asInt().toFloat()))
                        walk_Label.add(label.format(dp.getEndTime(TimeUnit.MILLISECONDS)))
                        Log.i(TAG, "walk"  + dp.getValue(field).asInt().toString() + ", " + walk_Label.last())
                    }
                }
                count += 1
            }
        }
    }
    @SuppressLint("InflateParams")
    fun menupopup(v:View){
        mPopupWindow = PopupMenu(this@MainActivity, v);
        mPopupWindow!!.getMenuInflater().inflate(R.menu.menu, mPopupWindow!!.getMenu())

        mPopupWindow!!.setOnMenuItemClickListener(object: PopupMenu.OnMenuItemClickListener{
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when(item!!.itemId){
                    R.id.accountchange_Btn->{
                        doGoogleSignOut()
                        signIn()
                        return true
                    }
                    R.id.pesnaldatarefresh_Btn->{
                        weight_series.clear()
                        muscle_series.clear()
                        walk_series.clear()
                        fat_series.clear()
                        bmi_series.clear()
                        kcal_series.clear()
                        weight_Label.clear()
                        kcal_Label.clear()
                        walk_Label.clear()
                        fat_Label.clear()
                        muscle_Label.clear()
                        bmi_Label.clear()
                        return true
                    }
                    R.id.dataupload_Btn->{
                        if(goalFragment == null) {
                            supportFragmentManager
                                    .beginTransaction()
                                    .replace(R.id.root_layout, GoalFragment.newInstance(ID, PW), "goal")
                                    .commit()
                        }else{
                            supportFragmentManager
                                    .beginTransaction().replace(R.id.root_layout, goalFragment!!).commit()
                        }
                        return true
                    }
                }
                return false
            }
        })
        mPopupWindow!!.show()
    }

}

