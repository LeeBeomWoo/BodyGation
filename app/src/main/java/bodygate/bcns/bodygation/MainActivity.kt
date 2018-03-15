package bodygate.bcns.bodygation

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import bodygate.bcns.bodygation.dummy.DummyContent
import bodygate.bcns.bodygation.navigationitem.FollowFragment
import bodygate.bcns.bodygation.navigationitem.ForMeFragment
import bodygate.bcns.bodygation.navigationitem.GoalFragment
import bodygate.bcns.bodygation.navigationitem.MovieFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.Scopes
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlin.coroutines.experimental.CoroutineContext
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.NonNull
import com.google.android.gms.fitness.request.DataReadRequest
import android.view.MotionEvent
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.*
import com.google.android.gms.fitness.*
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataSourcesRequest
import com.google.android.gms.fitness.request.OnDataPointListener
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.fitness.result.DataReadResult
import com.google.android.gms.fitness.result.DataSourcesResult
import com.google.android.gms.tasks.*
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.storage.UploadTask
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.series.Series
import kotlinx.android.synthetic.main.fragment_for_me.*
import kotlinx.coroutines.experimental.*
import java.lang.Exception
import java.text.DateFormat
import java.text.DateFormat.getDateInstance
import java.text.DateFormat.getTimeInstance
import java.util.*
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.text.Typography.times


class MainActivity() : AppCompatActivity(), GoalFragment.OnGoalInteractionListener, FollowFragment.OnFollowInteraction,
        ForMeFragment.OnForMeInteraction, MovieFragment.OnMovieInteraction, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnDataPointListener, Parcelable {

    override fun OnGoalInteractionListener(uri: Uri) {
        //TODO("not implemented") To change body of created functions use File | Settings | File Templates.
    }

    override fun OnFollowInteraction(uri: Uri) {
        // TODO("not implemented") To change body of created functions use File | Settings | File Templates.
    }

    override fun OnForMeInteraction(dataSetList: List<DataSet>) {
        val dateFormat = getTimeInstance()
        for (dataSet: DataSet in dataSetList) {
            for (dp: DataPoint in dataSet.getDataPoints()) {
                Log.i(TAG, "Data point:");
                Log.i(TAG, "\tType: " + dp.getDataType().getName());
                Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                var dataPoint: com.jjoe64.graphview.series.DataPoint? = null
                val pointSet: DataPoint = dp
                for (field: Field in dp.getDataType().getFields()) {
                    Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));

                }
            }
        }
    }

    override fun OnMovieInteraction(item: DummyContent.DummyItem) {
        //  TODO("not implemented") To change body of created functions use File | Settings | File Templates.
    }

    private var authInProgress = false
    lateinit var mFitnessClient: GoogleApiClient
    lateinit var mGoogleSignInAccount: GoogleSignInAccount
    private val REQUEST_OAUTH = 1001
    val ID: String? = null
    val PW: String? = null
    val TAG:String = "MainActivity_"
    private val AUTH_PENDING = "auth_state_pending"


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
        mGoogleSignInAccount = parcel.readParcelable(GoogleSignInAccount::class.java.classLoader)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG +"_", "onCreate")
        if (savedInstanceState != null) {

            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }else {
            supportFragmentManager.beginTransaction()
                    .add(R.id.root_layout, MovieFragment.newInstance(), "rageComicList")
                    .commit()
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
        mFitnessClient!!.connect()
    }

    override fun onStop() {
        super.onStop()
        if (mFitnessClient!!.isConnected()) {
            mFitnessClient!!.disconnect()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false
            if (resultCode == Activity.RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mFitnessClient!!.isConnecting() && !mFitnessClient!!.isConnected()) {
                    mFitnessClient!!.connect()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
    }
    fun printData(dataReadResult:DataReadResponse) {
    // [START parse_read_data_result]
    // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
    // as buckets containing DataSets, instead of just DataSets.
    if (dataReadResult.getBuckets().size > 0) {
      Log.i(
          TAG, "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size);
      for (bucket: Bucket in dataReadResult.getBuckets()) {
       val dataSets = bucket.getDataSets()
        for (dataSet : DataSet in dataSets) {
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
    private fun dumpDataSet(dataSet:DataSet) {
  Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
  val dateFormat = getTimeInstance()

  for (dp:DataPoint in dataSet.getDataPoints()) {
    Log.i(TAG, "Data point:");
    Log.i(TAG, "\tType: " + dp.getDataType().getName());
    Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
    Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
    for (field:Field in dp.getDataType().getFields()) {
      Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
    }
  }
}
    override fun onConnected(p0: Bundle?) {
        Log.i(TAG, "onConnected")
        //Google Fit Client에 연결되었습니다.

        registerFitnessDataListener()
    }
    override fun onDataPoint(dataPoint:DataPoint) {
        Log.i(TAG, "onDataPoint")
        // Do cool stuff that matters. 중요한 것을 멋지게 처리하십시오.
        for( field:Field in dataPoint.getDataType().getFields() ) {
            val value = dataPoint.getValue( field );
        }
    }

    override fun onConnectionSuspended(cause:Int) {
        Log.i(TAG, "onConnectionSuspended")
            // The connection has been interrupted. Wait until onConnected() is called.
        }

    //fun registerFitnessDataListener(dataSource:DataSource, dataType:DataType) :List<DataSet> {
       fun registerFitnessDataListener() = launch(CommonPool) {

        val cal = Calendar.getInstance()
        val now = Date()
        val endTime = now.time
        cal.set(2000,1,1)
        val startTime = cal.timeInMillis
        val dateFormat = getDateInstance()
        Log.i(TAG, "Range Start: " + startTime.toString())
        Log.i(TAG, "Range End: " + endTime.toString())

        //PendingResult<DataReadResult>
        /**
        val pendingResult = Fitness.HistoryApi.readData(
         mFitnessClient,
         DataReadRequest.Builder()
                 .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                 .bucketByTime(1, TimeUnit.DAYS)
                 .aggregate(DataType.TYPE_WEIGHT, DataType.AGGREGATE_WEIGHT_SUMMARY)
             .build())
        Log.i(TAG, pendingResult.toString())
        //List<DataSet>
        val dataSets = pendingResult.await()
        val readResult = dataSets.dataSets
        */
        val extension =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
                        .build()
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Fitness.SCOPE_LOCATION_READ_WRITE)
                .requestScopes(Fitness.SCOPE_ACTIVITY_READ_WRITE)
                .requestScopes(Fitness.SCOPE_BODY_READ_WRITE)
                .requestScopes(Fitness.SCOPE_NUTRITION_READ_WRITE)
                .requestIdToken(getString(R.string.server_client_id))
                .addExtension(extension)
                .build()
       val task = GoogleSignIn.getClient(this@MainActivity, signInOptions)
                .silentSignIn()
        val googleSigninAccount = Tasks.await(task)
        val response = Fitness.getHistoryClient(this@MainActivity, googleSigninAccount)
        .readData(DataReadRequest.Builder()
            .read(DataType.TYPE_WEIGHT)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build());

        val readDataResult = Tasks.await(response);
        val dataSet = readDataResult.getDataSet(DataType.TYPE_WEIGHT);
        Log.i(TAG + "dataSet", dataSet.toString())
        OnForMeInteraction(readDataResult.dataSets)
        printData(readDataResult)
    }
    override fun onConnectionFailed(result: ConnectionResult) {
        Log.i(TAG, "onConnectionFailed")
            // Error while connecting. Try to resolve using the pending intent returned.
            if (result.getErrorCode() == FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS) {
                try {
                    result.startResolutionForResult(this, REQUEST_OAUTH);
                } catch (e: IntentSender.SendIntentException) {
                }
            }
        }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (authInProgress) 1 else 0)
        parcel.writeParcelable(mGoogleSignInAccount, flags)
    }

    override fun describeContents(): Int {
        return 0
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



