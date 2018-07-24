package bodygate.bcns.bodygation.support

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import bodygate.bcns.bodygation.dummy.DataClass
import com.github.mikephil.charting.data.BarEntry
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.fitness.Fitness
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
import kotlinx.android.synthetic.main.fragment_goal.*
import kotlinx.android.synthetic.main.maintablayout.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

interface FitConnect{
    val TAG: String
        get() = "FitConnect"
    var custom_Type:DataType
    var account:GoogleSignInAccount
    var  mAuth: FirebaseAuth
    var personUrl:Uri
    var data:DataClass
    var ib:Int

    var weight_series: MutableList<BarEntry>
    var muscle_series: MutableList<BarEntry>
    var walk_series: MutableList<BarEntry>
    var fat_series: MutableList<BarEntry>
    var bmi_series: MutableList<BarEntry>
    var kcal_series: MutableList<BarEntry>

    var weight_Label:MutableList<String>
    var kcal_Label:MutableList<String>
    var walk_Label:MutableList<String>
    var fat_Label:MutableList<String>
    var muscle_Label:MutableList<String>
    var bmi_Label:MutableList<String>
    fun dataClass():DataClass{
        return data
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

    suspend fun insert_second(acc: GoogleSignInAccount, context:Context, sa:Float): Task<Void> {
        val cal = Calendar.getInstance()
        val now = Date()
        cal.time = now
        val nowTime = cal.timeInMillis
        val weight_source = DataSource.Builder()
                .setDataType(DataType.TYPE_WEIGHT)
                .setAppPackageName(context.packageName)
                .setType(DataSource.TYPE_RAW)
                .build()
        val weight_dataPoint = DataPoint.create(weight_source)
        weight_dataPoint.setTimestamp(nowTime,  TimeUnit.MILLISECONDS)
        weight_dataPoint.getValue(Field.FIELD_WEIGHT).setFloat(sa)
        val weight_Set = DataSet.create(weight_source)
        weight_Set.add(weight_dataPoint)
        return Fitness.getHistoryClient(context, acc).insertData(weight_Set)
                .addOnSuccessListener(object : OnSuccessListener<Void> {
                    override fun onSuccess(p0: Void?) {
                        Log.i(TAG, "insert_second onSuccess")
                    }
                })
                .addOnFailureListener(object : OnFailureListener {
                    override fun onFailure(p0: Exception) {
                        Log.i(TAG, "insert_second OnFailureListener")
                        Log.i(TAG, p0.message)
                    }
                })
                .addOnCompleteListener(object : OnCompleteListener<Void> {
                    override fun onComplete(p0: Task<Void>) {
                        Log.i(TAG, "insertData OnCompleteListener")
                        Toast.makeText(context, "전송이 완료되었습니다 매달 체크하여 건강하고 행복하세요~", Toast.LENGTH_SHORT).show()
                    }
                })
    }
    suspend fun insertData(acc: GoogleSignInAccount, context:Context, sa:ArrayList<Float>): Task<Void>? {
        val cal = Calendar.getInstance()
        val now = Date()
        cal.time = now
        val nowTime = cal.timeInMillis
        // Create a new dataset and insertion request.
        if (custom_Type == null) {
            launch(UI) {
                val a = makeData(acc, context)
            }
            return null
        }else {
            val source = DataSource.Builder()
                    .setName("bodygate.bcns.bodygation.personal")
                    .setDataType(custom_Type)
                    .setAppPackageName(context.packageName)
                    .setType(DataSource.TYPE_DERIVED)
                    .build()
            val dataPoint = DataPoint.create(source)
            // Set values for the data point
            // This data type has two custom fields (int, float) and a common field
            for (s: Int in 0..(custom_Type.fields.size - 1)) {
                when (custom_Type.fields[s].name) {
                    "bmi" -> {
                        dataPoint.getValue(custom_Type.fields[s]).setFloat(sa[0])
                    }
                    "muscle" -> {
                        dataPoint.getValue(custom_Type.fields[s]).setFloat(sa[1])
                    }
                    "fat" -> {
                        dataPoint.getValue(custom_Type.fields[s]).setFloat(sa[2])
                    }
                }
            }

            dataPoint.setTimestamp(nowTime, TimeUnit.MILLISECONDS)
            val dataSet = DataSet.create(source)
            dataSet.add(dataPoint)

            return Fitness.getHistoryClient(context, acc).insertData(dataSet)
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
                                val a = makeData(acc, context)
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
    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
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

            //user profile pic
            personUrl = acct.photoUrl!!
            Log.i("profile", "getProfileInformation : " + personName + "\t" + acct.photoUrl.toString() + "\t"+ personEmail +"\t" + acct.toString())
        }
    }

    fun accessGoogleFit(acc:GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(acc.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(object : OnCompleteListener<AuthResult>{
                    override fun onComplete(p0: Task<AuthResult>) {
                        if(p0.isSuccessful){
                            val user = mAuth.currentUser
                            getProfileInformation(user!!)
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
}