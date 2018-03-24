package bodygate.bcns.bodygation

import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.net.ConnectivityManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.NonNull
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import bodygate.bcns.bodygation.dummy.DummyContent
import bodygate.bcns.bodygation.dummy.SearchData
import bodygate.bcns.bodygation.dummy.listContent
import bodygate.bcns.bodygation.navigationitem.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.FitnessStatusCodes
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.OnDataPointListener
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeScopes
import com.google.api.services.youtube.model.Thumbnail
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.google_login.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONException
import org.json.JSONObject
import pub.devrel.easypermissions.EasyPermissions
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.text.DateFormat.getDateInstance
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


@Suppress("DUPLICATE_LABEL_IN_WHEN")
class MainActivity() : AppCompatActivity(), GoalFragment.OnGoalInteractionListener, FollowFragment.OnFollowInteraction,
        ForMeFragment.OnForMeInteraction, MovieFragment.OnMovieInteraction, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnDataPointListener, Parcelable, YouTubeResult.OnYoutubeResultInteraction {

    private val PREF_ACCOUNT_NAME = "accountName"
    private var mOutputText: TextView? = null;
    private var mCallApiButton: Button? =null
    var mProgress: ProgressDialog? =null
    val REQUEST_ACCOUNT_PICKER = 1000
    val REQUEST_AUTHORIZATION = 1001
    val REQUEST_GOOGLE_PLAY_SERVICES = 1002
    val REQUEST_PERMISSION_GET_ACCOUNTS = 1003
    private val PROPERTIES_FILENAME = "youtube.properties"
    private var authInProgress = false
    lateinit var mFitnessClient: GoogleApiClient
    private val REQUEST_OAUTH = 1001
    val ID: String? = null
    val PW: String? = null
    val TAG: String = "MainActivity_"
    private val AUTH_PENDING = "auth_state_pending"
    private val RC_SIGN_IN = 111//google sign in request code
    private val REQUEST_OAUTH_REQUEST_CODE = 1
    val forMeFragment: ForMeFragment = ForMeFragment()
    var weight_data: DataSet? = null
    var bfp_data: DataSet? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null//google sign in client
    var adapter: YoutubeResultListViewAdapter? = null
    val NUMBER_OF_VIDEOS_RETURNED: Long = 30
    var mCredential: GoogleAccountCredential? = null
    var SCOPES = YouTubeScopes.YOUTUBE_READONLY
    var queryKey:kotlin.collections.ArrayList<String>? = null
    var dataResult:kotlin.collections.ArrayList<SearchData>? = null
    /** Global instance of the HTTP transport.  */
    var HTTP_TRANSPORT:NetHttpTransport = NetHttpTransport()

    /** Global instance of the JSON factory.  */
    var JSON_FACTORY: JsonFactory? = JacksonFactory()

    override fun OnYoutubeResultInteraction(item: ArrayList<String>){
        getYoutubeAdapter(item)
    }

    fun  getYoutubeAdapter(item: ArrayList<String>)  = launch(CommonPool) { try {
       val youtube = YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, object : HttpRequestInitializer {
           @Throws(IOException::class)
           override fun initialize(request: HttpRequest) {
           }
       }).setApplicationName("youtube-cmdline-search-sample").build()

       // Get query term from user.
       val queryTerm = item

       val search = youtube.Search().list("id,snippet")
       /*
      * It is important to set your developer key from the Google Developer Console for
      * non-authenticated requests (found under the API Access tab at this link:
      * code.google.com/apis/). This is good practice and increased your quota.
      */
       val apiKey = getString(R.string.API_key)
       search.setKey(apiKey)
       search.setQ(queryTerm.toString())
       /*
      * We are only searching for videos (not playlists or channels). If we were searching for
      * more, we would add them as a string like this: "video,playlist,channel".
      */
       search.setType("video")
       /*
      * This method reduces the info returned to only the fields we need and makes calls more
      * efficient.
      */
       search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)")
       search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED)
       val searchResponse = search.execute()

       val searchResultList = searchResponse.getItems()
       val iteratorSearchResults = searchResultList.iterator()
       var i = 0
       val result = MutableList(searchResultList.size) { listContent.DummyItem(String(), String(), String()) }
       while (iteratorSearchResults.hasNext()) {

           val singleVideo = iteratorSearchResults.next()
           val rId = singleVideo.getId()

           // Double checks the kind is video.
           if (rId.getKind() == "youtube#video") {
               val thumbnail = singleVideo.getSnippet().getThumbnails().get("default") as Thumbnail

               Log.i(TAG +"Video Id" , rId.getVideoId())
               Log.i(TAG+"Title", singleVideo.getSnippet().getTitle())
               Log.i(TAG+"Thumbnail", thumbnail.url)
               result.add(i, listContent.DummyItem(rId.getVideoId(), singleVideo.getSnippet().getTitle(), thumbnail.url))
               i += 1
           }

       }
        async(UI) {
            adapter = YoutubeResultListViewAdapter(result, this@MainActivity) }
   } catch (e: GoogleJsonResponseException) {
       Log.i(TAG, ("There was a service error: " + e.getDetails().getCode() + " : "
               + e.getDetails().getMessage()))
   } catch (e: IOException) {
       Log.i(TAG, ("There was an IO error: " + e.cause + " : " + e.message))
   } catch (t: Throwable) {
       Log.i(TAG, t.toString())
   }
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


    override fun describeContents(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun OnGoalInteractionListener(uri: Uri) {
        //TODO("not implemented") To change body of created functions use File | Settings | File Templates.
    }

    override fun OnFollowInteraction(uri: ArrayList<String>?) {
        Log.i(TAG, "OnFollowInteraction")
        Log.i(TAG, uri?.get(2))
        queryKey = uri
        getUtube(queryKey)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.root_layout, YouTubeResult.newInstance(uri!!), "rageComicList")
                .commit()
    }

    override fun OnForMeInteraction() {
    }

    override fun OnMovieInteraction(item: DummyContent.DummyItem) {
        //  TODO("not implemented") To change body of created functions use File | Settings | File Templates.
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
            R.id.navigation_home -> {
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.root_layout, MovieFragment.newInstance(), "rageComicList")
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
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
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(ExponentialBackOff());
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        } else {
            supportFragmentManager.beginTransaction()
                    .add(R.id.root_layout, MovieFragment.newInstance(), "rageComicList")
                    .commit()
        }
        val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_WEIGHT)
                .addDataType(DataType.TYPE_BODY_FAT_PERCENTAGE)
                .addDataType(DataType.TYPE_BASAL_METABOLIC_RATE)
                .build()
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            registerFitnessDataListener();
        }
        getResultsFromApi()
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        mFitnessClient = GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
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
            REQUEST_GOOGLE_PLAY_SERVICES -> consume {
                if (resultCode != RESULT_OK) {
                    mOutputText!!.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
            }
            REQUEST_ACCOUNT_PICKER -> consume {
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
            REQUEST_AUTHORIZATION -> consume {
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
            }
            REQUEST_OAUTH -> consume {
                authInProgress = false;
                if (resultCode == RESULT_OK) {
                    // Make sure the app is not already connected or attempting to connect
                    if (!mFitnessClient.isConnecting() && !mFitnessClient.isConnected()) {
                        mFitnessClient.connect();
                    }
                }
            }
        }
    }

    inline fun consume(f: () -> Unit): Boolean {
        f()
        return true
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
    }

    override fun printData(dataReadResult: DataReadResponse) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getDataSets().size > 0) {
            Log.i(TAG, "Number of returned DataSets is: " + dataReadResult.getDataSets().size);
            for (dataSet in dataReadResult.getDataSets()) {
                if (dataSet.dataType == DataType.TYPE_WEIGHT) {
                    Log.i(TAG, "Data Type:" + "TYPE_WEIGHT")
                    weight_dumpDataSet(dataSet)
                } else if (dataSet.dataType == DataType.TYPE_BODY_FAT_PERCENTAGE) {
                    Log.i(TAG, "Data Type:" + "TYPE_BODY_FAT_PERCENTAGE")
                    bfp_dumpDataSet(dataSet)
                }
            }
        }
        // [END parse_read_data_result]
    }

    override fun bfp_dumpDataSet(dataSet: DataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        val dateFormat = getDateInstance()
        bfp_data = dataSet
        for (dp: DataPoint in dataSet.getDataPoints()) {
            Log.i(TAG, "Data point:" + dp.toString())
            Log.i(TAG, "Type: " + dp.getDataType().getName());
            Log.i(TAG, "Start: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "End: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "TimeStemp: " + dateFormat.format(dp.getTimestamp(TimeUnit.MILLISECONDS)) + "type: " + dp.getTimestamp(TimeUnit.MILLISECONDS).javaClass)
            Log.i(TAG, " Value: " + dp.getValue(Field.FIELD_PERCENTAGE) + "type: " + dp.getValue(Field.FIELD_PERCENTAGE).javaClass)
        }
        Log.i(TAG, "list point:" + bfp_list.toString())
    }

    override fun weight_dumpDataSet(dataSet: DataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        val dateFormat = getDateInstance()
        weight_data = dataSet
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
    override fun registerFitnessDataListener() = launch(CommonPool) {
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
                        .read(DataType.TYPE_BODY_FAT_PERCENTAGE)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build());
        val readDataResult = Tasks.await(response);
        val dataSet = readDataResult.getDataSet(DataType.TYPE_WEIGHT);
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
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.result

            // Signed in successfully, show authenticated UI.
            getProfileInformation(account)

            //show toast
            Toast.makeText(this, "Google Sign In Successful.", Toast.LENGTH_SHORT).show();

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());

            //show toast
            Toast.makeText(this, "Failed to do Sign In : " + e.getStatusCode(), Toast.LENGTH_SHORT).show();

            //update Ui for this
            getProfileInformation(null)
        }
    }

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
            Picasso.with(this).load(personPhoto).fit().placeholder(R.mipmap.ic_launcher_round).into(user_profile_image_view);

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
            val task = MakeRequestTask(mCredential)
            task.execute()
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

    fun getUtube(qt:kotlin.collections.ArrayList<String>?): JSONObject {
        val httpGet = HttpGet(
                "https://www.googleapis.com/youtube/v3/search?"
                        + "part=snippet&q=" + qt.toString()
                        + "&key="+ getString(R.string.API_key)+"&maxResults=50");  //EditText에 입력된 값으로 겁색을 합니다.
        // part(snippet),  q(검색값) , key(서버키)
        val client = DefaultHttpClient()
        val stringBuilder = StringBuilder()

        try {
            val response = client.execute(httpGet)
            val entity = response.getEntity()
            val stream = entity.getContent()
            while (stream.read() !== -1) {
                stringBuilder.append(stream.read() as Char)
            }

        } catch (e: ClientProtocolException) {
        } catch (e:IOException) {
        }

        var jsonObject = JSONObject()
        try {
            jsonObject = JSONObject(stringBuilder.toString())
        } catch (e: JSONException) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }
    private fun paringJsonData(jsonObject:JSONObject) {
        dataResult?.clear();

        var youtube = YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, HttpRequestInitializer { }).setApplicationName(application.packageName).build()
        val contacts = jsonObject.getJSONArray("items");
        var vodid = ""
        val a  = contacts.length()
        for (i:Int in 0..a) {
            val c = contacts.getJSONObject(i)
            val kind =  c.getJSONObject("id").getString("kind") // 종류를 체크하여 playlist도 저장
            if(kind.equals("youtube#video")){
                vodid = c.getJSONObject("id").getString("videoId"); // 유튜브
                // 동영상
                // 아이디
                // 값입니다.
                // 재생시
                // 필요합니다.
            }else{
                vodid = c.getJSONObject("id").getString("playlistId"); // 유튜브
            }

            val title = c.getJSONObject("snippet").getString("title"); //유튜브 제목을 받아옵니다
            var changString = "";
            try {
                changString = String(title.toByteArray( charset("8859_1")), charset("utf-8")); //한글이 깨져서 인코딩 해주었습니다
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace();
            }

            val date = c.getJSONObject("snippet").getString("publishedAt") //등록날짜
                    .substring(0, 10);
            val imgUrl = c.getJSONObject("snippet").getJSONObject("thumbnails")
                    .getJSONObject("default").getString("url");  //썸내일 이미지 URL값
            dataResult?.add(SearchData(vodid, changString, imgUrl, date));
            Log.i(TAG, vodid+ "/" + changString+ "/" + imgUrl+ "/"+ date)
        }

    }

    class MakeRequestTask(mCredential: GoogleAccountCredential?): AsyncTask<Void, Void, MutableList<String>?>() {
        private var mService: com.google.api.services.youtube.YouTube? = null
        private val mLastError: Exception? = null
        fun MakeRequestTask(credential: GoogleAccountCredential):  com.google.api.services.youtube.YouTube? {
            val transport = AndroidHttp.newCompatibleTransport()
            val jsonFactory = JacksonFactory.getDefaultInstance()
            mService = com.google.api.services.youtube.YouTube.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("YouTube Data API Android Quickstart")
                    .build()
            return mService
        }
        override fun doInBackground(vararg p0: Void): MutableList<String>? {
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
            // ...
        }

        override fun onPostExecute(result: MutableList<String>?) {
            super.onPostExecute(result)
            // ...
        }

    } /**
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
                return getDataFromApi();
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
            private fun getDataFromApi(): MutableList<String> {
                Log.i(TAG, "getDataFromApi")
                // Get a list of up to 10 files.

            }

            override fun onPreExecute() {
                super.onPreExecute()
               // mOutputText.setText("");
                mProgress!!.show();
            }

            override fun onPostExecute(result: MutableList<String>?) {
                super.onPostExecute(result)
                mProgress!!.hide();
                if (result == null || result.size == 0) {
                      mOutputText!!.setText("No results returned.");
                } else {
                    result.add(0, "Data retrieved using the YouTube Data API:");
                    mOutputText!!.setText(TextUtils.join("\n", result));
                }
            }


            override fun onCancelled() {
                super.onCancelled()
                mProgress!!.hide();
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
*/
}





