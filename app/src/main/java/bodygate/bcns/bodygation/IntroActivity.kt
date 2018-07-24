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
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import bodygate.bcns.bodygation.dummy.DataClass
import bodygate.bcns.bodygation.navigationitem.*
import bodygate.bcns.bodygation.support.FitConnect
import bodygate.bcns.bodygation.support.MainPageAdapter
import com.github.mikephil.charting.data.BarEntry
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class IntroActivity : AppCompatActivity(), FitConnect {

    lateinit var custom_Type: DataType
    override lateinit var account: GoogleSignInAccount
    override lateinit var mAuth: FirebaseAuth
    override lateinit var personUrl: Uri
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val REQUEST_OAUTH = 1001
    override var data:DataClass = DataClass()

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
            accessGoogleFit(mGoogleSignInClient.silentSignIn().result)
            account = mGoogleSignInClient.silentSignIn().result
        }else{
            signIn()
        }
        data.setImg(personUrl)
        val task = someTask(account, this.applicationContext, data)
        task.execute()
    }
    private fun signIn() {
        Log.i(TAG, "signIn")
        val signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_OAUTH)
    }
    class someTask(acc: GoogleSignInAccount, cont:Context, data:DataClass) : AsyncTask<Void, Void, DataClass>() {
        var ib = 0
        var account: GoogleSignInAccount
        @SuppressLint("StaticFieldLeak")
        var contextContext:Context
        var data:DataClass
        val TAG = "someTask"
        var cPb: ProgressDialog? = null
        init {
            account = acc
            contextContext = cont
            this.data = data
        }
        override fun doInBackground(vararg params: Void?): DataClass? {
            ReadData(account)
            customReadData(account)
            return data
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
                            data.bmi_series.add(BarEntry(count.toFloat(), dp.getValue(field).asFloat()))
                            data.bmi_Label.add(label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                            Log.i(TAG, "bmi"  + dp.getValue(field).asFloat().toString() + ", " + data.bmi_Label.last())
                        }
                        "muscle" -> {
                            data.muscle_series.add(BarEntry(count.toFloat(), dp.getValue(field).asFloat()))
                            data.muscle_Label.add(label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                            Log.i(TAG, "muscle"  + dp.getValue(field).asFloat().toString() + ", " + data.muscle_Label.last())
                        }
                        "fat" -> {
                            data.fat_series.add(BarEntry(count.toFloat(), dp.getValue(field).asFloat()))
                            data.fat_Label.add(label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                            Log.i(TAG, "fat"  + dp.getValue(field).asFloat().toString() + ", " + data.fat_Label.last())
                        }
                        Field.FIELD_WEIGHT.name ->{
                            data.weight_series.add(BarEntry(count.toFloat(), dp.getValue(field).asFloat()))
                            data.weight_Label.add(label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                            Log.i(TAG, "weight"  + dp.getValue(field).asFloat().toString() + ", " + data.weight_Label.last())
                        }
                        Field.FIELD_CALORIES.name ->{
                            data.kcal_series.add(BarEntry(ib.toFloat(), dp.getValue(field).asFloat()))
                            data.kcal_Label.add(label.format(dp.getEndTime(TimeUnit.MILLISECONDS)))
                            Log.i(TAG, " kcal"  + dp.getValue(field).asFloat().toString() + ", " + data.kcal_Label.last())
                        }
                        Field.FIELD_STEPS.name ->{
                            data.walk_series.add(BarEntry(ib.toFloat(), dp.getValue(field).asInt().toFloat()))
                            data.walk_Label.add(label.format(dp.getEndTime(TimeUnit.MILLISECONDS)))
                            Log.i(TAG, "walk"  + dp.getValue(field).asInt().toString() + ", " + data.walk_Label.last())
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

        override fun onPostExecute(result: DataClass?) {
            super.onPostExecute(result)
            Log.i(TAG, "stopProgress")
            cPb!!.dismiss()
            val intent = Intent(contextContext, MainActivity::class.java)
            intent.putExtra("EXTRA_SESSION_ID", data)
            startActivity(contextContext, intent, null)
        }
    }
}
