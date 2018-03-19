package bodygate.bcns.bodygation

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import bodygate.bcns.bodygation.dummy.DummyContent
import bodygate.bcns.bodygation.navigationitem.FollowFragment
import bodygate.bcns.bodygation.navigationitem.ForMeFragment
import bodygate.bcns.bodygation.navigationitem.GoalFragment
import bodygate.bcns.bodygation.navigationitem.MovieFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
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
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.jjoe64.graphview.series.LineGraphSeries
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_for_me.*
import kotlinx.android.synthetic.main.google_login.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import java.text.DateFormat.FULL
import java.text.DateFormat.getDateInstance
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity() : AppCompatActivity(), GoalFragment.OnGoalInteractionListener, FollowFragment.OnFollowInteraction,
        ForMeFragment.OnForMeInteraction, MovieFragment.OnMovieInteraction, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnDataPointListener, Parcelable {
    override fun describeContents(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var authInProgress = false
    lateinit var mFitnessClient: GoogleApiClient
    private val REQUEST_OAUTH = 1001
    val ID: String? = null
    val PW: String? = null
    val TAG: String = "MainActivity_"
    private val AUTH_PENDING = "auth_state_pending"
    private val RC_SIGN_IN = 111//google sign in request code
    private val REQUEST_OAUTH_REQUEST_CODE = 1
    lateinit var list:MutableList<com.jjoe64.graphview.series.DataPoint>
    lateinit var dataSet:DataSet
    val forMeFragment: ForMeFragment = ForMeFragment()

    private var mGoogleSignInClient: GoogleSignInClient? = null//google sign in client
    override fun OnGoalInteractionListener(uri: Uri) {
        //TODO("not implemented") To change body of created functions use File | Settings | File Templates.
    }

    override fun OnFollowInteraction(uri: Uri) {
        // TODO("not implemented") To change body of created functions use File | Settings | File Templates.
    }

    override fun OnForMeInteraction(): DataSet {
        if(dataSet == null){
            registerFitnessDataListener()
        }
        return dataSet
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
                        .replace(R.id.root_layout, ForMeFragment.newInstance(ID, PW), "rageComicList")
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    constructor(parcel: Parcel) : this() {
        authInProgress = parcel.readByte() != 0.toByte()
    }

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
    private fun doGoogleSignIn() {
        val signInIntent = mGoogleSignInClient?.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)//pass the declared request code here
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG + "_", "onCreate")
        configureGoogleSignIn()
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        } else {
            supportFragmentManager.beginTransaction()
                    .add(R.id.root_layout, MovieFragment.newInstance(), "rageComicList")
                    .commit()
        }
        val fitnessOptions = FitnessOptions.builder().addDataType(DataType.TYPE_WEIGHT).build();
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            registerFitnessDataListener();
        }
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        mFitnessClient = GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(Scope(Scopes.FITNESS_LOCATION_READ))
                .addScope(Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(Scope(Scopes.FITNESS_NUTRITION_READ_WRITE))
                .addScope(Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
        mFitnessClient.connect()
    }

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
        if (mFitnessClient!!.isConnected()) {
            mFitnessClient!!.disconnect()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
            if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_OAUTH) {
                authInProgress = false
                mFitnessClient.connect()
            }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
    }

    override fun printData(dataReadResult: DataReadResponse) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size > 0) {
            Log.i(
                    TAG, "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size);
            for (bucket: Bucket in dataReadResult.getBuckets()) {
                val dataSets = bucket.getDataSets()
                for (dataSet: DataSet in dataSets) {
                    dumpDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size > 0) {
            Log.i(TAG, "Number of returned DataSets is: " + dataReadResult.getDataSets().size);
            for (dataSet in dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
        }
        // [END parse_read_data_result]
    }

     override fun dumpDataSet(dataSet: DataSet):LineGraphSeries<com.jjoe64.graphview.series.DataPoint> {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        val dateFormat = getDateInstance(FULL)
         var weight_lineseries = LineGraphSeries<com.jjoe64.graphview.series.DataPoint>()
        for (dp: DataPoint in dataSet.getDataPoints()) {
            Log.i(TAG, "Data point:" + dp.toString())
            Log.i(TAG, "Type: " + dp.getDataType().getName());
            Log.i(TAG, "Start: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "End: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "TimeStemp: " + dateFormat.format(dp.getTimestamp(TimeUnit.MILLISECONDS)));
            for (field: Field in dp.getDataType().getFields()) {
                Log.i(TAG, "Field: " + field.toString() + " Value: " + dp.getValue(field));
            }
            for (dp: com.google.android.gms.fitness.data.DataPoint in dataSet.getDataPoints()) {
                for (field: Field in dp.getDataType().getFields()) {
                    val setDate = Date(dp.getTimestamp(TimeUnit.MILLISECONDS))
                    weight_lineseries. = LineGraphSeries<com.jjoe64.graphview.series.DataPoint>(arrayOf<com.jjoe64.graphview.series.DataPoint>(com.jjoe64.graphview.series.DataPoint(setDate, dp.getValue(field).asFloat().toDouble())))
                }
            }
        }
         this.dataSet = dataSet

         return weight_lineseries
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

    fun registerFitnessDataListener() = launch(CommonPool) {

        val cal = Calendar.getInstance()
        val now = Date()
        val endTime = now.time
        cal.set(2000, 1, 1)
        val startTime = cal.timeInMillis
        Log.i(TAG, "Range Start: " + startTime.toString())
        Log.i(TAG, "Range End: " + endTime.toString())
        val task = GoogleSignIn.getLastSignedInAccount(this@MainActivity)
        val response = Fitness.getHistoryClient(this@MainActivity, task)
                .readData(DataReadRequest.Builder()
                        .read(DataType.TYPE_WEIGHT)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build());
        val readDataResult = Tasks.await(response);
        val dataSet = readDataResult.getDataSet(DataType.TYPE_WEIGHT);
        Log.i(TAG + "dataSet", dataSet.toString())
        printData(readDataResult)
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
    private fun doGoogleSignOut() {
        mGoogleSignInClient?.signOut()?.addOnCompleteListener(this, { Toast.makeText(this, "Google Sign Out done.", Toast.LENGTH_SHORT).show()
            revokeAccess(); })
    }

    /**
     * DISCONNECT ACCOUNTS
     * method to revoke access from this app
     * call this method after successful sign out
     * <p>
     * It is highly recommended that you provide users that signed in with Google the ability to disconnect their Google account from your app. If the user deletes their account, you must delete the information that your app obtained from the Google APIs
     */
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
}



