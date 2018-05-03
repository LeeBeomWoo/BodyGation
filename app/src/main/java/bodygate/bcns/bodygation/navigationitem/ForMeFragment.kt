package bodygate.bcns.bodygation.navigationitem

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bodygate.bcns.bodygation.CheckableImageButton
import bodygate.bcns.bodygation.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.Bucket
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.OnSuccessListener
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.helper.StaticLabelsFormatter
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.fragment_for_me.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.runOnUiThread
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ForMeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ForMeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ForMeFragment : Fragment(), bodygate.bcns.bodygation.CheckableImageButton.OnCheckedChangeListener {
    override fun onCheckedChanged(button: CheckableImageButton?, check: Boolean) {
        when (button!!.id) {
            R.id.weight_Btn -> {
                weight_Btn.setChecked(check)
                if (walk_Btn.isChecked)
                    walk_Btn.setChecked(!check)
                if (kal_Btn.isChecked)
                    kal_Btn.setChecked(!check)
                if (bfp_Btn.isChecked)
                    bfp_Btn.setChecked(!check)
                if (muscle_Btn.isChecked)
                    muscle_Btn.setChecked(!check)
                if (bmi_Btn.isChecked)
                    bmi_Btn.setChecked(!check)
                if (weight_Btn.isChecked){
                   graphSet(0)
                }
            }
            R.id.walk_Btn -> {
                walk_Btn.setChecked(check)
                if (weight_Btn.isChecked)
                    weight_Btn.setChecked(!check)
                if (kal_Btn.isChecked)
                    kal_Btn.setChecked(!check)
                if (bfp_Btn.isChecked)
                    bfp_Btn.setChecked(!check)
                if (muscle_Btn.isChecked)
                    muscle_Btn.setChecked(!check)
                if (bmi_Btn.isChecked)
                    bmi_Btn.setChecked(!check)
                if (walk_Btn.isChecked){
                    graphSet(1)
                }
            }
            R.id.kal_Btn -> {
                kal_Btn.setChecked(check)
                if (walk_Btn.isChecked)
                    walk_Btn.setChecked(!check)
                if (weight_Btn.isChecked)
                    weight_Btn.setChecked(!check)
                if (bfp_Btn.isChecked)
                    bfp_Btn.setChecked(!check)
                if (muscle_Btn.isChecked)
                    muscle_Btn.setChecked(!check)
                if (bmi_Btn.isChecked)
                    bmi_Btn.setChecked(!check)
                if (kal_Btn.isChecked){
                    graphSet(2)
                }
            }
            R.id.bfp_Btn -> {
                bfp_Btn.setChecked(check)
                if (walk_Btn.isChecked)
                    walk_Btn.setChecked(!check)
                if (weight_Btn.isChecked)
                    weight_Btn.setChecked(!check)
                if (kal_Btn.isChecked)
                    kal_Btn.setChecked(!check)
                if (muscle_Btn.isChecked)
                    muscle_Btn.setChecked(!check)
                if (bmi_Btn.isChecked)
                    bmi_Btn.setChecked(!check)
                if (bfp_Btn.isChecked){
                    graphSet(3)
                }
            }
            R.id.bmi_Btn -> {
                bmi_Btn.setChecked(check)
                if (walk_Btn.isChecked)
                    walk_Btn.setChecked(!check)
                if (weight_Btn.isChecked)
                    weight_Btn.setChecked(!check)
                if (kal_Btn.isChecked)
                    kal_Btn.setChecked(!check)
                if (bfp_Btn.isChecked)
                    bfp_Btn.setChecked(!check)
                if (muscle_Btn.isChecked)
                    muscle_Btn.setChecked(!check)
                if (bmi_Btn.isChecked){
                    graphSet(5)
                }
            }
            R.id.muscle_Btn -> {
                muscle_Btn.setChecked(check)
                if (walk_Btn.isChecked)
                    walk_Btn.setChecked(!check)
                if (weight_Btn.isChecked)
                    weight_Btn.setChecked(!check)
                if (kal_Btn.isChecked)
                    kal_Btn.setChecked(!check)
                if (bfp_Btn.isChecked)
                    bfp_Btn.setChecked(!check)
                if (bmi_Btn.isChecked)
                    bmi_Btn.setChecked(!check)
                if (muscle_Btn.isChecked){
                    graphSet(4)
                }
            }
        }
    }

    val kcalories_Str = arrayOf("0", "1000", "2000", "5000", "4000")
    val bmi_Str = arrayOf("0", "18.5", "25", "30")
    val muscle_Str = arrayOf("0", "1000", "2000", "5000", "4000")
    val fatman_Str = arrayOf("0", "13", "22", "28")
    val fatgirl_Str = arrayOf("0", "22", "34", "40")
    val walk_Str = arrayOf("0", "5000", "10000", "15000", "20000", "25000")
    private var mParam1: String? = null
    private var mParam2: DoubleArray? = null
    private var mParam3: Array<String>? = null
    private var datalist: Array<DataPoint>? = null
    private var mListener: OnForMeInteraction? = null
    val value:MutableList<Double> =  ArrayList()
    val horizonValue:MutableList<Date> =  ArrayList()
    val verticalValue:MutableList<String> =  ArrayList()
    var first_value:Double? = null
    var last_value:Double? = null
    var first_date:Date? = null
    var last_date:Date? = null
    val TAG = "ForMeFragment_"
    var series: BarGraphSeries<DataPoint>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i(TAG, "onCreateView")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_for_me, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mListener!!.OnForMeInteraction()
        weight_Btn.setOnCheckedChangeListener(this)
        kal_Btn.setOnCheckedChangeListener(this)
        walk_Btn.setOnCheckedChangeListener(this)
        bfp_Btn.setOnCheckedChangeListener(this)
        bmi_Btn.setOnCheckedChangeListener(this)
        muscle_Btn.setOnCheckedChangeListener(this)
    }
    fun graphSet(p:Int){
        graph.removeSeries(series)
        horizonValue.clear()
        verticalValue.clear()
        value.clear()
        when(p){
            0->{//체중
                if(mListener!!.readResponse == null){
                    Log.i(TAG, "체중 없음")
                }else {
                    Log.i("graphSet_"+ "horizonValue", horizonValue.size.toString())
                    Log.i("graphSet_"+ "verticalValue", verticalValue.size.toString())
                    Log.i("graphSet_" + "value", value.size.toString())
                    if(series ==null){
                        series = BarGraphSeries<com.jjoe64.graphview.series.DataPoint>(printData(mListener!!.readResponse!!, p).toTypedArray())
                        series!!.setColor(Color.BLUE)
                        series!!.setSpacing(20)
                        series!!.setDrawValuesOnTop(true)
                        series!!.valuesOnTopSize = 20.toFloat()
                        graph.addSeries(series)
                    }else{
                        series!!.resetData(printData(mListener!!.readResponse!!, p).toTypedArray())
                        series!!.setColor(Color.BLUE)
                        series!!.setSpacing(20)
                        series!!.setDrawValuesOnTop(true)
                        series!!.valuesOnTopSize = 20.toFloat()
                        graph.addSeries(series)
                    }
                    Log.i("graphSet_"+ "horizonValue", horizonValue.size.toString())
                    Log.i("graphSet_"+ "verticalValue", verticalValue.size.toString())
                    Log.i("graphSet_" + "value", value.size.toString())
                    graph.title = getString(R.string.weight)
                    graph.titleTextSize = 100.toFloat()
                    graph.gridLabelRenderer.setLabelFormatter(DateAsXAxisLabelFormatter(mListener!!.context));
                    graph.gridLabelRenderer.setHumanRounding(false)
                   // graph.viewport.setMaxX(last_date!!.time)
                   // graph.viewport.setMinX(1.0)
                   // graph.viewport.setMaxY(8.0)
                   // graph.viewport.setMinY(3.0)
                    graph.viewport.isXAxisBoundsManual = true
                    graph.viewport.isYAxisBoundsManual = true
                    graph.viewport.setScrollable(true)
                    graph.gridLabelRenderer.numHorizontalLabels = 4
                    graph.gridLabelRenderer.numVerticalLabels = 6
                }
            }
            1->{//걷기
                if(mListener!!.walkResponse == null){
                    Log.i(TAG, "걷기 없음")
                }else {
                    Log.i("graphSet_"+ "horizonValue", horizonValue.size.toString())
                    Log.i("graphSet_"+ "verticalValue", verticalValue.size.toString())
                    Log.i("graphSet_" + "value", value.size.toString())
                    if(series == null){
                        series = BarGraphSeries<com.jjoe64.graphview.series.DataPoint>(printData(mListener!!.walkResponse!!, p).toTypedArray())
                        series!!.setColor(Color.YELLOW)
                        series!!.setSpacing(20)
                        series!!.setDrawValuesOnTop(true)
                        series!!.valuesOnTopSize = 20.toFloat()
                        graph.addSeries(series)
                    }else{
                        series!!.resetData(printData(mListener!!.walkResponse!!, p).toTypedArray())
                        series!!.setSpacing(20)
                        series!!.setDrawValuesOnTop(true)
                        series!!.valuesOnTopSize = 20.toFloat()
                        series!!.setColor(Color.YELLOW)
                            graph.addSeries(series)
                    }
                    Log.i("graphSet_"+ "horizonValue", horizonValue.size.toString())
                    Log.i("graphSet_"+ "verticalValue", verticalValue.size.toString())
                    Log.i("graphSet_" + "value", value.size.toString())
                    graph.title = getString(R.string.walk)
                    graph.titleTextSize = 100.toFloat()
                    graph.gridLabelRenderer.setLabelFormatter(DateAsXAxisLabelFormatter(mListener!!.context));
                    graph.gridLabelRenderer.setHumanRounding(false)
                    graph.gridLabelRenderer.numHorizontalLabels = 3
                    graph.gridLabelRenderer.numVerticalLabels = 6
                    graph.viewport.isYAxisBoundsManual = true
                    graph.viewport.setMaxY(last_value!! + 100)
                    graph.viewport.setMinY(first_value!! + 10)
                }
            }
            2->{//칼로리
                if(mListener!!.kcalResponse == null){
                    Log.i(TAG, "칼로리 없음")
                }else {
                    Log.i("graphSet_"+ "horizonValue", horizonValue.size.toString())
                    Log.i("graphSet_"+ "verticalValue", verticalValue.size.toString())
                    Log.i("graphSet_" + "value", value.size.toString())
                    if(series == null){
                        series = BarGraphSeries<com.jjoe64.graphview.series.DataPoint>(printData(mListener!!.kcalResponse!!, p).toTypedArray())
                        series!!.setColor(Color.YELLOW)
                        series!!.setSpacing(50)
                        series!!.setDrawValuesOnTop(true)
                        series!!.setValuesOnTopSize(20.toFloat())
                        graph.addSeries(series)
                    }else {
                        series!!.resetData(printData(mListener!!.kcalResponse!!, p).toTypedArray())
                        series!!.setColor(Color.RED)
                        series!!.setSpacing(50)
                        series!!.setDrawValuesOnTop(true)
                        series!!.setValuesOnTopSize(20.toFloat())
                        graph.addSeries(series)
                    }
                    Log.i("graphSet_"+ "horizonValue", horizonValue.size.toString())
                    Log.i("graphSet_"+ "verticalValue", verticalValue.size.toString())
                    Log.i("graphSet_" + "value", value.size.toString())
                    graph.title = getString(R.string.calore)
                    graph.titleTextSize = 100.toFloat()
                    graph.gridLabelRenderer.setLabelFormatter(DateAsXAxisLabelFormatter(mListener!!.context));
                    graph.gridLabelRenderer.setHumanRounding(false)
                    graph.viewport.setScrollable(true)
                    graph.viewport.setScrollableY(false)
                    graph.viewport.isXAxisBoundsManual = true
                    graph.viewport.isYAxisBoundsManual = true
                }
            }
            3->{//체지방비율
                if(mListener!!.fatResponse == null){
                    Log.i(TAG, "체지방비율 없음")
                }else {
                    Log.i("graphSet_"+ "horizonValue", horizonValue.size.toString())
                    Log.i("graphSet_"+ "verticalValue", verticalValue.size.toString())
                    Log.i("graphSet_" + "value", value.size.toString())
                    if(series ==null){
                        series = BarGraphSeries<com.jjoe64.graphview.series.DataPoint>(printData(mListener!!.fatResponse!!, p).toTypedArray())
                        series!!.setColor(Color.MAGENTA)
                        graph.addSeries(series)
                    }else{
                        series!!.resetData(printData(mListener!!.fatResponse!!, p).toTypedArray())
                         series!!.setColor(Color.MAGENTA)
                        graph.addSeries(series)
                    }
                    Log.i("graphSet_"+ "horizonValue", horizonValue.size.toString())
                    Log.i("graphSet_"+ "verticalValue", verticalValue.size.toString())
                    Log.i("graphSet_" + "value", value.size.toString())
                    graph.title = getString(R.string.bodyfat)
                    graph.titleTextSize = 100.toFloat()
                    series!!.setSpacing(50)
                    series!!.setDrawValuesOnTop(true);
                    graph.gridLabelRenderer.setLabelFormatter(DateAsXAxisLabelFormatter(mListener!!.context));
                    graph.gridLabelRenderer.setHumanRounding(false)
                    graph.viewport.setScalable(true)
                    graph.viewport.setMaxX(5.0)
                   // graph.viewport.setMinX(1.0)
                    graph.viewport.setMaxY(8.0)
                   // graph.viewport.setMinY(3.0)
                }
            }
            4->{//골격근
                if(mListener!!.muscleResponse == null){
                    Log.i(TAG, "골격근 없음")
                }else {
                    if(series ==null){
                        series = BarGraphSeries<com.jjoe64.graphview.series.DataPoint>(printData(mListener!!.muscleResponse!!, p).toTypedArray())
                        series!!.setColor(Color.DKGRAY)
                        graph.addSeries(series)
                    }else{
                        series!!.resetData(printData(mListener!!.muscleResponse!!, p).toTypedArray())
                        series!!.setColor(Color.DKGRAY)
                        graph.addSeries(series)
                    }
                    graph.title = getString(R.string.musclemass)
                    graph.titleTextSize = 100.toFloat()
                    series!!.setSpacing(50)
                    series!!.setDrawValuesOnTop(true);
                    graph.gridLabelRenderer.setLabelFormatter(DateAsXAxisLabelFormatter(mListener!!.context));
                    graph.gridLabelRenderer.setHumanRounding(false)
                    graph.viewport.setScalable(true)
                    graph.viewport.setMaxX(5.0)
                    graph.viewport.setMinX(1.0)
                    graph.viewport.setMaxY(8.0)
                    graph.viewport.setMinY(3.0)
                }
            }
            5->{//BMI
                if(mListener!!.bmiResponse == null){
                    Log.i(TAG, "BMI 없음")
                }else {
                    if(series ==null){
                        series = BarGraphSeries<com.jjoe64.graphview.series.DataPoint>(printData(mListener!!.bmiResponse!!, p).toTypedArray())
                        series!!.setColor(Color.GREEN)
                        graph.addSeries(series)
                    }else{
                        series!!.resetData(printData(mListener!!.bmiResponse!!, p).toTypedArray())
                        series!!.setColor(Color.GREEN)
                        graph.addSeries(series)
                    }
                    graph.title = getString(R.string.bmi)
                    graph.titleTextSize = 100.toFloat()
                    series!!.setSpacing(50)
                    series!!.setDrawValuesOnTop(true);
                    graph.gridLabelRenderer.setLabelFormatter(DateAsXAxisLabelFormatter(mListener!!.context));
                    graph.gridLabelRenderer.setHumanRounding(false)
                    graph.viewport.setScalable(true)
                    graph.viewport.setMaxX(5.0)
                    graph.viewport.setMinX(1.0)
                    graph.viewport.setMaxY(8.0)
                    graph.viewport.setMinY(3.0)
                }
            }
        }
    }
    @SuppressLint("SimpleDateFormat")
    fun printData(dataReadResult: DataReadResponse, i:Int):MutableList<com.jjoe64.graphview.series.DataPoint> {
        val label = SimpleDateFormat("MM/dd")
        var ia = 0
        val line :MutableList<com.jjoe64.graphview.series.DataPoint> = ArrayList()
            if (dataReadResult.getBuckets().size > 0) {
                Log.i("printData", "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size)

                    for (bucket: Bucket in dataReadResult.getBuckets()) {
                        Log.i("printData", "Bucket point:");
                        Log.i("printData", "bucket : " + bucket.toString())
                        Log.i("printData", "\tStart: " + label.format(bucket.getStartTime(TimeUnit.MILLISECONDS)))
                        Log.i("printData", "\tEnd: " + label.format(bucket.getEndTime(TimeUnit.MILLISECONDS)))
                        Log.i("printData", "\tdataSets: " + bucket.dataSets.toString())
                        printBucket(bucket, i)
                        ia += 1
                        Log.i("printData", "\tia : " + ia.toString())
                    }
                Log.i("printData", "\thorizonValue : " + horizonValue.size.toString())
                Log.i("printData", "\tvalue : " + value.size.toString())
                for(a:Int in 0..(horizonValue.size-1)){
                     line.add(com.jjoe64.graphview.series.DataPoint(horizonValue.get(a), value.get(a))) //동적
                    // line.add(com.jjoe64.graphview.series.DataPoint(a.toDouble(), value.get(a))) //정적
                }
                last_date = horizonValue.last()
                first_date = horizonValue.first()
                first_value = value.min()
                last_value = value.max()
            } else if (dataReadResult.getDataSets().size > 0) {
                Log.i("printData", "Number of returned DataSets is: " + dataReadResult.getDataSets().size);
                for (dataSet: DataSet in dataReadResult.getDataSets()) {
                    line.addAll(dumpDataSet(dataSet, i))
                    ia += 1
                }
            }
        return line
    }

    @SuppressLint("SimpleDateFormat")
    fun printBucket(bucket:Bucket, i: Int) {
        Log.i("printData", "printBucket")
        val label = SimpleDateFormat("MM/dd")
        when(i){
            1 -> {//걷기
                val set = bucket.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA)!!
                for(dp:com.google.android.gms.fitness.data.DataPoint in set.dataPoints){
                    if(dp.getValue(Field.FIELD_STEPS).asInt() > 10) {
                        Log.i("printData", "\tdataPoints: " + dp.toString())
                        Log.i("printData", "\tgetValue: " + dp.getValue(Field.FIELD_STEPS).toString())
                        verticalValue.add(label.format(Date(bucket.getEndTime(TimeUnit.MILLISECONDS))))
                        horizonValue.add(Date(bucket.getEndTime(TimeUnit.MILLISECONDS)))
                        value.add(dp.getValue(Field.FIELD_STEPS).asInt().toDouble())
                    }
                }
            }
            2 -> {//칼로리
                val set = bucket.getDataSet(DataType.AGGREGATE_CALORIES_EXPENDED)!!
                for(dp:com.google.android.gms.fitness.data.DataPoint in set.dataPoints){
                    Log.i("printData", "\tdataPoints: " + dp.toString())
                    Log.i("printData", "\tgetValue: " + dp.getValue(Field.FIELD_CALORIES).toString())
                    if(dp.getValue(Field.FIELD_CALORIES).asFloat().toDouble() > 100.0) {
                        verticalValue.add(label.format(Date(bucket.getEndTime(TimeUnit.MILLISECONDS))))
                        horizonValue.add(Date(bucket.getEndTime(TimeUnit.MILLISECONDS)))
                        value.add(dp.getValue(Field.FIELD_CALORIES).asFloat().toDouble())
                    }
                }
            }
        }

    }
    @SuppressLint("SimpleDateFormat")
    private fun dumpDataSet(dataSet:DataSet, i:Int): ArrayList<com.jjoe64.graphview.series.DataPoint> {
        Log.i("printData", "Data returned for Data type: " + dataSet.getDataType().getName());
        val label = SimpleDateFormat("MM/dd")
        Log.i("printData", "Data size:" + dataSet.getDataPoints().size.toString())
        Log.i("printData", "Data set:" + dataSet.toString())
        var ia = 0
        for ( dp : com.google.android.gms.fitness.data.DataPoint in dataSet.getDataPoints()) {
            if(dp.getValue(dp.getDataType().fields.get(0)).toString().toDouble() > 0.5) {
                verticalValue.add(label.format(Date(dp.getTimestamp(TimeUnit.MILLISECONDS))))
                horizonValue.add(Date(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                value.add(dp.getValue(dp.getDataType().fields.get(0)).toString().toDouble())
                ia += 1
                Log.i("printData", "Data point:");
                Log.i("printData", "\tType: " + dp.getDataType().getName());
                Log.i("printData", "\tStart: " + label.format(dp.getStartTime(TimeUnit.MILLISECONDS)))
                Log.i("printData", "\tEnd: " + label.format(dp.getEndTime(TimeUnit.MILLISECONDS)))
                Log.i("printData", "\tTimestamp: " + label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                Log.i("printData", "\tValue: " + dp.getValue(dp.getDataType().fields.get(0)).toString());
                Log.i("printData", "ia: " + ia.toString())
            }
        }
        last_date = horizonValue.last()
        first_date = horizonValue.first()
        first_value = value.min()
        last_value = value.max()
        val mlist:ArrayList<com.jjoe64.graphview.series.DataPoint> = ArrayList()
        Log.i("printData", "\thorizonValue : " + horizonValue.size.toString())
        Log.i("printData", "\tvalue : " + value.size.toString())
        for(a:Int in 0..(horizonValue.size - 1)){
            mlist.add(com.jjoe64.graphview.series.DataPoint(horizonValue.get(a), value.get(a))) //동적
         //   mlist.add(com.jjoe64.graphview.series.DataPoint(a.toDouble(), value.get(a))) //정적
        }
        Log.i("printData", "Data ia:"+ia.toString())
        return mlist
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnForMeInteraction) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnForMeInteraction {
        // TODO: Update argument type and name
        fun OnForMeInteraction()
        var kcalResponse: DataReadResponse?
        var walkResponse: DataReadResponse?
        var readResponse:DataReadResponse?
        var muscleResponse: DataReadResponse?
        var fatResponse: DataReadResponse?
        var bmiResponse: DataReadResponse?
        val context:Context
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        val TAG = "newInstance_"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ForMeFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?): ForMeFragment {
            Log.i(TAG, "ForMeFragment")
            val fragment = ForMeFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            Log.i(TAG, "ForMeFragment")
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
