package bodygate.bcns.bodygation

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
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
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.parcel.Parcelize
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class IntroActivity : AppCompatActivity() {
    var personUrl: Uri? = null

    lateinit var custom_Type: DataType
    lateinit var account: GoogleSignInAccount
    lateinit var mAuth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val REQUEST_OAUTH = 1001
    val TAG = "IntroActivity-"

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_OAUTH -> {
                Log.i(TAG, "REQUEST_OAUTH")
                if (resultCode == Activity.RESULT_OK) {
                    // Make sure the app is not already connected or attempting to connectTask<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    handleSignInResult(task)
                    account = task.result
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
            Log.i(TAG, "mGoogleSignInClient.silentSignIn().isSuccessful")
            accessGoogleFit(mGoogleSignInClient.silentSignIn().result)
            account = mGoogleSignInClient.silentSignIn().result
        }else{
            Log.i(TAG, "mGoogleSignInClient.silentSignIn().isFailed")
            signIn()
        }
    }
    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        Log.i(TAG, "handleSignInResult")
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
    private fun getProfileInformation(acct: FirebaseUser?) {
        //if account is not null fetch the information
        if (acct != null) {
            //user display name
            val personName = acct.getDisplayName()


            //user email id
            val personEmail = acct.getEmail()

            personUrl = acct.photoUrl

            val task = someTask(account, this@IntroActivity, personUrl!!)
            task.execute()
            Log.i("profile", "getProfileInformation : " + personName + "\t" + acct.photoUrl.toString() + "\t"+ personEmail +"\t" + acct.toString())
        }
    }

    fun accessGoogleFit(acc:GoogleSignInAccount){
        Log.i(TAG, "accessGoogleFit")
        val credential = GoogleAuthProvider.getCredential(acc.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(object : OnCompleteListener<AuthResult>{
                    override fun onComplete(p0: Task<AuthResult>) {
                        if(p0.isSuccessful){
                            val user = mAuth.currentUser
                            getProfileInformation(user!!)
                        }else{
                            getProfileInformation(null)
                        }
                    }
                })
    }
    private fun signIn() {
        Log.i(TAG, "signIn")
        val signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_OAUTH)
    }
    class someTask(acc: GoogleSignInAccount, cont:Context, uri:Uri) : AsyncTask<Void, Void, Void?>() {
        var ib = 0
        var account: GoogleSignInAccount
        @SuppressLint("StaticFieldLeak")
        var contextContext:Context
        val TAG = "someTask"
        var bmi_series:MutableList<Float> = ArrayList()
        var muscle_series:MutableList<Float> = ArrayList()
        var walk_series:MutableList<Int> = ArrayList()
        var fat_series:MutableList<Float> = ArrayList()
        var weight_series:MutableList<Float> = ArrayList()
        var kcal_series:MutableList<Float> = ArrayList()

        var bmi_Label:MutableList<String> = ArrayList()
        var muscle_Label:MutableList<String> = ArrayList()
        var walk_Label:MutableList<String> = ArrayList()
        var weight_Label:MutableList<String> = ArrayList()
        var fat_Label:MutableList<String> = ArrayList()
        var kcal_Label:MutableList<String> = ArrayList()
        var personUrl: Uri
        var cPb: ProgressDialog? = null
        var first_job = false
        var second_job = false
        var third_job = false
        var fourth_job = false
        var thread:Thread = Thread.currentThread()
        init {
            account = acc
            contextContext = cont
            personUrl = uri
        }
        override fun doInBackground(vararg params: Void?):Void? {
            ReadData(account)
            customReadData(account)
            thread.join(5000)
            return null
        }


        fun ReadData(acc:GoogleSignInAccount) {
            readRequest_weight(acc, contextContext).continueWithTask(object: com.google.android.gms.tasks.Continuation<DataReadResponse, Task<DataReadResponse>> {
                override fun then(p0: Task<DataReadResponse>): Task<DataReadResponse> {
                    return readRequest_arr(acc, contextContext)
                }
            })
        }
        fun customReadData(acc:GoogleSignInAccount) {
            customDataType(acc, contextContext).continueWithTask(object: com.google.android.gms.tasks.Continuation<DataType, Task<DataReadResponse>> {
                override fun then(p0: Task<DataType>): Task<DataReadResponse> {
                    return readRequest_custom(acc, p0.result, contextContext)
                }
            })
        }

        fun makeData(acc: GoogleSignInAccount, context:Context): Task<DataType> {
            Log.i(TAG, "makeData")
            Log.i(TAG, "custom_routine" + "makeData")
            val request = DataTypeCreateRequest.Builder()
                    // The prefix of your data type name must match your app's package name
                    .setName("bodygate.bcns.bodygation.personal")
                    .addField("bmi", Field.FORMAT_FLOAT)
                    .addField("muscle", Field.FORMAT_FLOAT)
                    .addField("fat", Field.FORMAT_FLOAT)
                    .build()
            return Fitness.getConfigClient(context, acc).createCustomDataType(request)
                    .addOnSuccessListener(object : OnSuccessListener<DataType> {
                        override fun onSuccess(p0: DataType?) {
                            Log.i(TAG, "makeData onSuccess")
                        }
                    })
                    .addOnFailureListener(object : OnFailureListener {
                        override fun onFailure(p0: Exception) {
                            Log.i(TAG, "makeData OnFailureListener")
                            Log.i(TAG, p0.message)
                        }
                    })
                    .addOnCompleteListener(object : OnCompleteListener<DataType> {
                        override fun onComplete(p0: Task<DataType>) {
                            Log.i(TAG, "makeData OnCompleteListener")

                        }
                    })
        }

        fun customDataType(acc: GoogleSignInAccount, context:Context): Task<DataType> {
            Log.i(TAG, "customDataType")
            Log.i(TAG, "custom_routine" + "customDataType")
            return Fitness.getConfigClient(context, acc).readDataType("bodygate.bcns.bodygation.personal")
                    .addOnSuccessListener(object : OnSuccessListener<DataType> {
                        override fun onSuccess(p0: DataType?) {
                            Log.i(TAG, "customDataType onSuccess")
                            //readRequest_custom(acc, p0!!)
                        }
                    })
                    .addOnFailureListener(object : OnFailureListener {
                        override fun onFailure(p0: Exception) {
                            Log.i(TAG, "customDataType OnFailureListener")
                            Log.i(TAG, p0.message)
                            val a = makeData(acc, context)
                            if(a.isSuccessful) {
                                val m = customDataType(acc, context)
                            }
                        }
                    })
                    .addOnCompleteListener(object : OnCompleteListener<DataType> {
                        override fun onComplete(p0: Task<DataType>) {
                            Log.i(TAG, "customDataType OnCompleteListener")
                        }
                    })
        }

        fun readRequest_weight(acc: GoogleSignInAccount, context:Context) : Task<DataReadResponse> {
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
            return Fitness.getHistoryClient(context, acc).readData(request)
                    .addOnSuccessListener(object : OnSuccessListener<DataReadResponse> {
                        override fun onSuccess(p0: DataReadResponse) {
                            printData(p0)
                        }
                    })
                    .addOnFailureListener(object : OnFailureListener {
                        override fun onFailure(p0: Exception) {
                            Log.i(TAG, "readRequest_weight OnFailureListener")
                            Log.i(TAG, p0.message)
                        }
                    })
                    .addOnCompleteListener(object : OnCompleteListener<DataReadResponse> {
                        override fun onComplete(p0: Task<DataReadResponse>) {
                            Log.i(TAG, "readRequest_weight OnCompleteListener")

                        }
                    })
        }

        fun readRequest_arr(acc: GoogleSignInAccount, context:Context): Task<DataReadResponse> {
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
            return Fitness.getHistoryClient(context, acc).readData(request)
                    .addOnSuccessListener(object : OnSuccessListener<DataReadResponse> {
                        override fun onSuccess(p0: DataReadResponse) {
                            printData(p0)
                        }
                    })
                    .addOnFailureListener(object : OnFailureListener {
                        override fun onFailure(p0: Exception) {
                            Log.i(TAG, "readRequest_arr OnFailureListener")
                            Log.i(TAG, p0.message)
                        }
                    })
                    .addOnCompleteListener(object : OnCompleteListener<DataReadResponse> {
                        override fun onComplete(p0: Task<DataReadResponse>) {
                            Log.i(TAG, "readRequest_arr OnCompleteListener")
                        }
                    })
        }

        fun readRequest_custom(acc: GoogleSignInAccount, type: DataType, context:Context): Task<DataReadResponse> {
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
            return Fitness.getHistoryClient(context, acc).readData(request)
                    .addOnSuccessListener(object : OnSuccessListener<DataReadResponse> {
                        override fun onSuccess(p0: DataReadResponse) {
                            printData(p0)
                        }
                    })
                    .addOnFailureListener(object : OnFailureListener {
                        override fun onFailure(p0: Exception) {
                            Log.i(TAG, "readRequest_custom OnFailureListener")
                            Log.i(TAG, p0.message)
                        }
                    })
                    .addOnCompleteListener(object : OnCompleteListener<DataReadResponse> {
                        override fun onComplete(p0: Task<DataReadResponse>) {
                            Log.i(TAG, "readRequest_custom OnCompleteListener")
                        }
                    })
        }

        @SuppressLint("SimpleDateFormat")
        fun printData(dataReadResult: DataReadResponse) {
            ib = 0
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
                            bmi_series.add(dp.getValue(field).asFloat())
                            bmi_Label.add(label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                            Log.i(TAG, "bmi"  + dp.getValue(field).asFloat().toString() + ", " + bmi_Label.last())
                        }
                        "muscle" -> {
                            muscle_series.add(dp.getValue(field).asFloat())
                            muscle_Label.add(label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                            Log.i(TAG, "muscle"  + dp.getValue(field).asFloat().toString() + ", " + muscle_Label.last())
                        }
                        "fat" -> {
                            fat_series.add(dp.getValue(field).asFloat())
                            fat_Label.add(label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                            Log.i(TAG, "fat"  + dp.getValue(field).asFloat().toString() + ", " + fat_Label.last())
                        }
                        Field.FIELD_WEIGHT.name ->{
                            weight_series.add(dp.getValue(field).asFloat())
                            weight_Label.add(label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                            Log.i(TAG, "weight"  + dp.getValue(field).asFloat().toString() + ", " + weight_Label.last())
                        }
                        Field.FIELD_CALORIES.name ->{
                            kcal_series.add(dp.getValue(field).asFloat())
                            kcal_Label.add(label.format(dp.getEndTime(TimeUnit.MILLISECONDS)))
                            Log.i(TAG, " kcal"  + dp.getValue(field).asFloat().toString() + ", " + kcal_Label.last())
                        }
                        Field.FIELD_STEPS.name ->{
                            walk_series.add(dp.getValue(field).asInt())
                            walk_Label.add(label.format(dp.getEndTime(TimeUnit.MILLISECONDS)))
                            Log.i(TAG, "walk"  + dp.getValue(field).asInt().toString() + ", " + walk_Label.last())
                        }
                    }
                    count += 1
                }
            }
        }

        override fun onPreExecute() {
            super.onPreExecute()
            cPb = ProgressDialog(contextContext)
            cPb!!.setTitle("데이터를 받아오는 중입니다.")
            cPb!!.setMessage("구글로 부터 사용자 정보를 받아오는 중입니다.")
            cPb!!.setCancelable(false)
            cPb!!.show()
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            Log.i(TAG, "stopProgress")
            cPb!!.dismiss()
            val intent = Intent(contextContext, MainActivity::class.java)
            intent.putExtra("EXTRA_SESSION_ID", DataClass(bmi_series, muscle_series, walk_series, fat_series, weight_series, kcal_series,
                    bmi_Label,muscle_Label,walk_Label, weight_Label, fat_Label, kcal_Label, personUrl))
            startActivity(contextContext, intent, null)
        }
    }
}

@Parcelize
class DataClass(var bmi_series:MutableList<Float>,var muscle_series:MutableList<Float>,var walk_series:MutableList<Int>,var fat_series:MutableList<Float>,var weight_series:MutableList<Float>,var kcal_series:MutableList<Float>
                ,var bmi_Label:MutableList<String>,var muscle_Label:MutableList<String>,var walk_Label:MutableList<String>,var weight_Label:MutableList<String>,var fat_Label:MutableList<String>,var kcal_Label:MutableList<String>,var personUrl: Uri?) : Parcelable {
}
