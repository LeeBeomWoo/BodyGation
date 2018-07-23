package bodygate.bcns.bodygation

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import bodygate.bcns.bodygation.dummy.DataClass
import bodygate.bcns.bodygation.navigationitem.*
import bodygate.bcns.bodygation.support.FitConnect
import bodygate.bcns.bodygation.support.MainPageAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.experimental.launch

class IntroActivity : AppCompatActivity() , FitConnect{
    override var ib: Int
        get() = 0
        set(value) {}

    override lateinit var custom_Type: DataType
    override lateinit var account: GoogleSignInAccount
    override lateinit var mAuth: FirebaseAuth
    override lateinit var personUrl: Uri
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val REQUEST_OAUTH = 1001
    override lateinit var data:DataClass

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        mAuth = FirebaseAuth.getInstance()
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
        if(mGoogleSignInClient.silentSignIn().isSuccessful){
            accessGoogleFit(mGoogleSignInClient.silentSignIn().result)
        }else{
            signIn()
        }

        customReadData(GoogleSignIn.getLastSignedInAccount(this)!!)
    }
    fun ReadData(acc:GoogleSignInAccount) {
        readRequest_weight(acc, this.applicationContext).continueWithTask(object: com.google.android.gms.tasks.Continuation<DataReadResponse, Task<DataReadResponse>> {
            override fun then(p0: Task<DataReadResponse>): Task<DataReadResponse> {
                return readRequest_arr(acc, this@IntroActivity.applicationContext)
            }
        })
    }
    fun customReadData(acc:GoogleSignInAccount) {
        customDataType(acc, this.applicationContext).continueWithTask(object: com.google.android.gms.tasks.Continuation<DataType, Task<DataReadResponse>> {
            override fun then(p0: Task<DataType>): Task<DataReadResponse> {
                return readRequest_custom(acc, p0.result, this@IntroActivity.applicationContext)
            }
        })
    }
    private fun signIn() {
        Log.i(TAG, "signIn")
        val signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_OAUTH)
    }
}
