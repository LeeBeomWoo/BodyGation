package bodygate.bcns.bodygation

import android.accounts.Account
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.provider.Contacts
import android.support.annotation.NonNull
import android.support.annotation.Nullable
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.FitnessStatusCodes
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.OnDataPointListener
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.fitness.result.DataReadResult
import com.google.android.gms.tasks.*
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.google.android.youtube.player.YouTubeThumbnailLoader
import com.google.android.youtube.player.YouTubeThumbnailView
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.people.v1.People
import com.google.api.services.people.v1.model.Person
import com.google.api.services.youtube.YouTubeScopes
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_follow.*
import kotlinx.android.synthetic.main.fragment_movie.*
import kotlinx.android.synthetic.main.google_login.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.URLEncoder
import java.text.DateFormat.getDateInstance
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


@Suppress("DUPLICATE_LABEL_IN_WHEN")
class MainActivity() : AppCompatActivity(), GoalFragment.OnGoalInteractionListener, FollowFragment.OnFollowInteraction,
        ForMeFragment.OnForMeInteraction, MovieFragment.OnMovieInteraction, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnDataPointListener, Parcelable, YouTubeResult.OnYoutubeResultInteraction{
    override var ndata: MutableList<YoutubeResponse.Items>
        get() = nMaindata
        set(value) {}
    override var pdata: MutableList<YoutubeResponse.Items>
        get() = pMaindata
        set(value) {}
    override var mdata: MutableList<YoutubeResponse.Items>
        get() = mMaindata
        set(value) {}

    private val RC_RECOVERABLE = 9002
    private val REQUEST_RESOLVE_ERROR = 1001
    // Unique tag for the error dialog fragment
    private val DIALOG_ERROR = "dialog_error"
    // Bool to track whether the app is already resolving an error
    private val mResolvingError = false
    private val PREF_ACCOUNT_NAME = "accountName"
    private var mOutputText: TextView? = null;
    val REQUEST_ACCOUNT_PICKER = 1000
    val REQUEST_AUTHORIZATION = 1001
    val REQUEST_GOOGLE_PLAY_SERVICES = 1002
    val REQUEST_PERMISSION_GET_ACCOUNTS = 1003
    private var authInProgress = false
    var mFitnessClient: GoogleApiClient? = null
    private val REQUEST_OAUTH = 1001
    val ID: String? = null
    val PW: String? = null
    val TAG: String = "MainActivity_"
    var preData:MutableList<YoutubeResponse.Items> = ArrayList()
    private val AUTH_PENDING = "auth_state_pending"
    private val RC_SIGN_IN = 111//google sign in request code
    private val REQUEST_OAUTH_REQUEST_CODE = 1
    var weight_data: DataSet? = null
    var bfp_data: DataSet? = null
    var walk_data: DataSet? = null
    var calore_data: DataSet? = null
    var nMaindata: MutableList<YoutubeResponse.Items> = ArrayList()
    var pMaindata: MutableList<YoutubeResponse.Items> = ArrayList()
    var mMaindata: MutableList<YoutubeResponse.Items> = ArrayList()
    private var mAccount: Account? = null
    var page = ""
    private var mGoogleSignInClient: GoogleSignInClient? = null//google sign in client
    var mCredential: GoogleAccountCredential? = null
    var SCOPES = YouTubeScopes.YOUTUBE_READONLY
    val context:Context = this
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
    override var walk_dateSET: DataSet?
        get() =walk_data
        set(value) {}
    override var calole_dateSET: DataSet?
        get() = calore_data
        set(value) {}
    override var bfp_dateSET: DataSet?
        get() = bfp_data
        set(value) {}
    override var weight_dateSET: DataSet?
        get() = weight_data
        set(value) {}
    override var bfp_list: Array<com.jjoe64.graphview.series.DataPoint>?
        set(value) {}
        get() = arrayOf()
    override var weight_list: Array<com.jjoe64.graphview.series.DataPoint>?
        set(value) {}
        get() = arrayOf()

    override var walk_list: Array<com.jjoe64.graphview.series.DataPoint>?
        set(value) {}
        get() = arrayOf()
    override var calole_list: Array<com.jjoe64.graphview.series.DataPoint>?
        set(value) {}
        get() = arrayOf()
    override fun writeToParcel(p0: Parcel?, p1: Int) {
    }

    override fun OnGoalInteractionListener(uri: Uri) {
    }

    override fun getDatas(part: String, q: String, api_Key: String, max_result: Int, more:Boolean,  page: String?, section:Int): MutableList<YoutubeResponse.Items>{
        val a = q.replace("[", "");
        val b = a.replace("]", "")
        Log.i(TAG, "getDatas")
        val task = GetYouTubeTask(section, b, ProgressDialog(this))
        task.execute(b)
        return task.get()
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
                val pb = ProgressDialog(this)
                val tak = GetFitnessTask(pb, this)
                weight_data = tak.execute().get()[0]
                bfp_data = tak.execute().get()[1]
                calore_data = tak.execute().get()[2]
                walk_data = tak.execute().get()[3]
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
                .addDataType(DataType.TYPE_CALORIES_EXPENDED)
                .addDataType(DataType.TYPE_STEP_COUNT_CADENCE)
                .build()
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
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
                .enableAutoManage(this, this)
                .build()
        mFitnessClient!!.connect()
    }

    @SuppressLint("RestrictedApi")
    override fun onStart() {
        super.onStart()
        // Connect to the Fitness API
        Log.i(TAG, "Connecting...")
        val account = GoogleSignIn.getLastSignedInAccount(this)
        //update the UI if user has already sign in with the google for this app
        mFitnessClient!!.connect()
        //getProfileInformation(account)
    }

    override fun onStop() {
        super.onStop()
        if (mFitnessClient!!.isConnected()) {
            mFitnessClient!!.disconnect()
        }
    }
    private fun handleSignInResult(@NonNull completedTask:Task<GoogleSignInAccount>) {
        Log.d(TAG, "handleSignInResult:" + completedTask.isSuccessful())
        try
        {
            val account = completedTask.getResult(ApiException::class.java)
            updateUI(account)
            // Store the account from the result
            mAccount = account.getAccount()
            // Asynchronously access the People API for the account
            getResultsFromApi()
        }
        catch (e: ApiException) {
            Log.w(TAG, "handleSignInResult:error", e)
            // Clear the local account
            mAccount = null
            // Signed out, show unauthenticated UI.
            updateUI(null)
        }
    }
    @SuppressLint("ShowToast", "RestrictedApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RC_SIGN_IN)
        {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
        if (requestCode == RC_RECOVERABLE)
        {
            if (resultCode == RESULT_OK)
            {
                getResultsFromApi()
            }
            else
            {
                Toast.makeText(this, "계정 연결에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    @SuppressLint("RestrictedApi")
    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    @SuppressLint("RestrictedApi")
    private fun signOut() {
        // Signing out clears the current authentication state and resets the default user,
        // this should be used to "switch users" without fully un-linking the user's google
        // account from your application.
        mGoogleSignInClient!!.signOut().addOnCompleteListener(this, object:OnCompleteListener<Void> {
           override fun onComplete(@NonNull task:Task<Void>) {
                updateUI(null)
            }
        })
    }

    private fun updateUI(@Nullable account: GoogleSignInAccount?) {
        if (account != null) {
            user_details_label.setText(account.displayName)

            default_google_sign_in_button.setVisibility(View.GONE)
            custom_sign_in_button.setVisibility(View.VISIBLE)
        } else {
            user_details_label.setText(null)

            default_google_sign_in_button.setVisibility(View.VISIBLE)
            custom_sign_in_button.setVisibility(View.GONE)
        }
    }
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
    }

    override fun onConnected(p0: Bundle?) {
        Log.i(TAG, "onConnected")
        //Google Fit Client에 연결되었습니다.
        val pb = ProgressDialog(this)
        val tak = GetFitnessTask(pb, this)
        weight_data = tak.execute().get()[0]
        bfp_data = tak.execute().get()[1]
        calore_data = tak.execute().get()[2]
        walk_data = tak.execute().get()[3]
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
            val pb = ProgressDialog(this@MainActivity)
            val task = MakeRequestTask(pb)
            task.execute(mCredential!!)
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

    protected fun onRecoverableAuthException(recoverableException: UserRecoverableAuthIOException) {
        Log.w(TAG, "onRecoverableAuthException", recoverableException)
        startActivityForResult(recoverableException.intent, RC_RECOVERABLE)
    }
    /**
     * An asynchronous task that handles the YouTube Data API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */

    class MakeRequestTask(pd:ProgressDialog): AsyncTask<GoogleAccountCredential, Void, MutableList<String>?>() {

        private var mService: com.google.api.services.youtube.YouTube? = null
        var pd:ProgressDialog
        private val mLastError: Exception? = null
        init{
            this.pd = pd
        }
        fun MakeRequestTask(credential: GoogleAccountCredential):  com.google.api.services.youtube.YouTube? {
            val transport = AndroidHttp.newCompatibleTransport()
            val jsonFactory = JacksonFactory.getDefaultInstance()
            mService = com.google.api.services.youtube.YouTube.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("YouTube Data API Android Quickstart")
                    .build()
            return mService
        }
        override fun doInBackground(vararg p0: GoogleAccountCredential): MutableList<String>? {
            val channelInfo = ArrayList<String>()
            val result = MakeRequestTask(p0.last())!!.channels().list("snippet,contentDetails,statistics")
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
            pd.setMessage("Uploading . . .")
            pd.show()
            pd.setCancelable(false)
            // ...
        }

        override fun onPostExecute(result: MutableList<String>?) {
            super.onPostExecute(result)
            // ...
            pd.dismiss()
        }

    }
    private class GetYouTubeTask(section:Int, q:String, pb:ProgressDialog):AsyncTask<String, Void, MutableList<YoutubeResponse.Items>>(){
        var mq=""
        var msection = 0
        val TAG = "GetYouTubeTask"
        var mPb:ProgressDialog? = null
        val preData:MutableList<YoutubeResponse.Items> = ArrayList()
        init{
            mq=q
            msection = section
            mPb = pb
        }
        override fun doInBackground(vararg params: String?): MutableList<YoutubeResponse.Items> {
            return access(msection, "AIzaSyDYApmvsPUrZkd1vr8yeN4ZvxkmE75HDa4", 40)
        }
        fun access(section:Int, api_Key: String, max_result: Int):MutableList<YoutubeResponse.Items>{
            val searchType = "video"

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
            Log.i(TAG, "getDatas")
            val youtubeResponseCall = apiService.searchVideo("snippet", 40, mq, "KR", searchType, order, api_Key)
            val body = youtubeResponseCall.execute()
            if(body.body()!!.nextPageToken == null) {
                getData(body)
            }else{
                val page = body.body()!!.nextPageToken
                addData(body, api_Key, 40, page, order)
            }
            return preData
        }
        fun getData(response: Response<YoutubeResponse>){
            val body = response.body()
            Log.i(TAG, response.raw().request().url().toString())
            if (body != null) {
                val items = body.items
                preData.addAll(items)
                }
            }

        fun addData(response: Response<YoutubeResponse>,  api_Key: String, max_result: Int, mpage: String?, order:String) {
            val searchType = "video"
            val apiService = YoutubeApi.create()
            Log.i(TAG, mq)
            if(mpage == null){
                getData(response)
            }else{
                val youtubeResponseCall = apiService.nextVideo("snippet", 40, mq, "KR", searchType, mpage, order, api_Key)
                val body = youtubeResponseCall.execute()
                val items = body.body()
                if (items != null) {
                    preData.addAll(items.items)
                    val page = items.nextPageToken
                    addData(response, api_Key, max_result, page, order)
                }
            }
        }

        override fun onPreExecute() {
            super.onPreExecute()
            mPb!!.show()
            mPb!!.setTitle("목록을 가져오는 중입니다 잠시만 기다려 주세요")
        }

        override fun onPostExecute(result: MutableList<YoutubeResponse.Items>?) {
            super.onPostExecute(result)
                mPb!!.dismiss()
        }
    }
    private class GetFitnessTask( pb:ProgressDialog, context: Context):AsyncTask<Void, Void, List<DataSet>>() {
        var mPb:ProgressDialog? = null
        val TAG = "GetFitnessTask"
        @SuppressLint("StaticFieldLeak")
        var mContext:Context
        init{
            mPb = pb
            mContext = context
        }
        @SuppressLint("RestrictedApi")
        override fun doInBackground(vararg p0: Void?): List<DataSet>? {
            val cal = Calendar.getInstance()
            val now = Date()
            val endTime = now.time
            cal.set(2000, 1, 1)
            val startTime = cal.timeInMillis
            Log.i(TAG, "Range Start: " + startTime.toString())
            Log.i(TAG, "Range End: " + endTime.toString())
            val task = GoogleSignIn.getLastSignedInAccount(mContext)
            val response = Fitness.getHistoryClient(mContext, task!!)
                    .readData(DataReadRequest.Builder()
                            .read(DataType.TYPE_WEIGHT)
                            .read(DataType.TYPE_BODY_FAT_PERCENTAGE)
                            .read(DataType.TYPE_CALORIES_EXPENDED)
                            .read(DataType.TYPE_STEP_COUNT_CADENCE)
                            .read(DataType.TYPE_BASAL_METABOLIC_RATE)
                            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                            .build())
            val readDataResult = Tasks.await(response);
            val result: MutableList<DataSet> = ArrayList()
            if (readDataResult.getBuckets().size > 0) {
                Log.e("History", "Number of buckets: " + readDataResult.getBuckets().size);
                for (bucket: Bucket in readDataResult.getBuckets()) {
                    val dataSets = bucket.getDataSets()
                    for (dataSet:DataSet in dataSets) {
                        when(dataSet.dataType){
                            DataType.TYPE_WEIGHT->{
                                result.add(0, dataSet)
                            }
                            DataType.TYPE_BODY_FAT_PERCENTAGE->{
                                result.add(1, dataSet)
                            }
                            DataType.TYPE_CALORIES_EXPENDED->{
                                result.add(2, dataSet)
                            }
                            DataType.TYPE_STEP_COUNT_CADENCE->{
                                result.add(3, dataSet)
                            }
                        }
                    }
                }
            }
//Used for non-aggregated data
            else if (readDataResult.getDataSets().size > 0) {
                Log.e("History", "Number of returned DataSets: " + readDataResult.getDataSets().size);
                for (dataSet: DataSet in readDataResult.getDataSets()) {
                    when (dataSet.dataType) {
                        DataType.TYPE_WEIGHT -> {
                            result.add(0, dataSet)
                        }
                        DataType.TYPE_BODY_FAT_PERCENTAGE -> {
                            result.add(1, dataSet)
                        }
                        DataType.TYPE_CALORIES_EXPENDED -> {
                            result.add(2, dataSet)
                        }
                        DataType.TYPE_STEP_COUNT_CADENCE -> {
                            result.add(3, dataSet)
                        }
                    }
                }
            }
            return result
        }
        override fun onCancelled() {}
        override fun onPreExecute() {
            super.onPreExecute()
            mPb!!.show()
            mPb!!.setTitle("구글핏 데이터를 받아오는 중입니다.")
        }
        override fun onPostExecute(result: List<DataSet>?) {
            mPb!!.dismiss()
        }
    }
}





