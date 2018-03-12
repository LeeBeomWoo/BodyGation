package bodygate.bcns.bodygation

import android.app.Activity
import android.content.Intent
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
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA
import com.google.android.gms.fitness.data.Field.FIELD_WEIGHT
import com.google.android.gms.tasks.Tasks
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.util.*
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import android.R.attr.data
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.widget.Toast
import jdk.nashorn.internal.runtime.ECMAException.getException
import org.junit.experimental.results.ResultMatchers.isSuccessful
import com.google.firebase.auth.AuthResult
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.AuthCredential






class MainActivity() : AppCompatActivity(), GoalFragment.OnGoalInteractionListener, FollowFragment.OnFollowInteraction,
                            ForMeFragment.OnForMeInteraction, MovieFragment.OnMovieInteraction{
    override fun OnGoalInteractionListener(uri: Uri) {
        //TODO("not implemented") To change body of created functions use File | Settings | File Templates.
    }

    override fun OnFollowInteraction(uri: Uri) {
       // TODO("not implemented") To change body of created functions use File | Settings | File Templates.
    }

    override fun OnForMeInteraction(uri: Uri) {
       // TODO("not implemented") To change body of created functions use File | Settings | File Templates.
    }

    override fun OnMovieInteraction(item: DummyContent.DummyItem) {
      //  TODO("not implemented") To change body of created functions use File | Settings | File Templates.
    }

    val mAuth = FirebaseAuth.getInstance();
    val ID: String? = null
    val PW: String? = null
    val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE:Int = 533
    val TAG:String = "MainActivity"
    val activity:MainActivity = this
     private val REQUEST_OAUTH_REQUEST_CODE = 1
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }
    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, object : OnCompleteListener<AuthResult> {
                    fun onComplete(task: Task<AuthResult>) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success")
                            val user = mAuth.currentUser
                            updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException())
                            Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                            updateUI(null)
                        }

                        // ...
                    }
                })
    }
    fun getData90():String {
        val extension = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build()
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .addExtension(extension)
                .build();
        val task = GoogleSignIn.getClient(this, signInOptions)
        .silentSignIn()
        val googleSigninAccount = Tasks.await(task)
        val response = Fitness.getHistoryClient(this, googleSigninAccount)
                .readDailyTotalFromLocalDevice(TYPE_STEP_COUNT_DELTA)
        val totalSet = Tasks.await(response)
        if (response.isSuccessful) {
            val total = (if (totalSet.isEmpty){
                Log.d(TAG + "_data_get", totalSet.toString())
            } else
                totalSet.dataPoints[0].getValue(FIELD_WEIGHT).asInt())
        } else {
            // handle failure
        }
        return totalSet.toString()
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
                    loadDataAsync()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.root_layout, MovieFragment.newInstance(), "rageComicList")
                    .commit()
        }else {
        }
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        val fitnessOptions = FitnessOptions.builder()
            //.addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            //.addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                //.addDataType(DataType.TYPE_WORKOUT_EXERCISE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_WEIGHT_SUMMARY, FitnessOptions.ACCESS_READ)
                //.addDataType(DataType.TYPE_BODY_FAT_PERCENTAGE, FitnessOptions.ACCESS_READ)
                //.addDataType(DataType.AGGREGATE_BASAL_METABOLIC_RATE_SUMMARY, FitnessOptions.ACCESS_READ)
                //.addDataType(DataType.TYPE_BASAL_METABOLIC_RATE, FitnessOptions.ACCESS_READ)
                //.addDataType(DataType.AGGREGATE_BODY_FAT_PERCENTAGE_SUMMARY, FitnessOptions.ACCESS_READ)
            .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this))) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            loadDataAsync()
        }
    }
    fun loadDataAsync() = async(CommonPool) {
        try {
            //Turn on busy indicator.
            val job = async(CommonPool) {
                //We're on a background thread here.
                //Execute blocking calls, such as retrofit call.execute().body() + caching.
                Log.d(TAG + "_data_load", getData90())
            }
            job.await();
            //We're back on the main thread here.
            //Update UI controls such as RecyclerView adapter data.
        }
        catch (e: Exception) {
            Log.d(TAG + "_error", e.toString())
        }
        finally {
            //Turn off busy indicator.
            Log.d(TAG + "_data", "Successed")
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                if (result.isSuccess) {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = result.signInAccount
                    firebaseAuthWithGoogle(account)
                } else {
                    // Google Sign In failed, update UI appropriately
                    // ...
                }
                loadDataAsync()
            }
        }
    }
}



