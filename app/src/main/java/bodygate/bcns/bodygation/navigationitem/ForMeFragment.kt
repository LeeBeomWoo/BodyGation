package bodygate.bcns.bodygation.navigationitem

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.HandlerThread
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bodygate.bcns.bodygation.CheckableImageButton
import bodygate.bcns.bodygation.R
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.google.android.gms.fitness.data.Bucket
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.result.DataReadResponse
import kotlinx.android.synthetic.main.fragment_for_me.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.math.min


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
                    launch(UI){graphSet(0)}
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
                    launch(UI) {graphSet(1)}
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
                    launch(UI){graphSet(2)}
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
                    launch(UI){ graphSet(3)}
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
                    launch(UI){graphSet(5)}
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
                    launch(UI){graphSet(4)}
                }
            }
        }
    }
    private var mParam1: String? = null
    private var mListener: OnForMeInteraction? = null
    val value:MutableList<Double> =  ArrayList()
    val kcalvalue:MutableList<Double> =  ArrayList()
    val horizonValue:MutableList<Date> =  ArrayList()
    val kcal_horizonValue:MutableList<Date> =  ArrayList()
    var weight_Label:MutableList<String> =  ArrayList()
    var kcal_Label:MutableList<String> =  ArrayList()
    var walk_Label:MutableList<String> =  ArrayList()
    var fat_Label:MutableList<String> =  ArrayList()
    var muscle_Label:MutableList<String> =  ArrayList()
    var bmi_Label:MutableList<String> =  ArrayList()
    val TAG = "ForMeFragment_"
    var weight_series: MutableList<BarEntry> = ArrayList()
    var muscle_series: MutableList<BarEntry> = ArrayList()
    var walk_series: MutableList<BarEntry> = ArrayList()
    var fat_series: MutableList<BarEntry> = ArrayList()
    var bmi_series: MutableList<BarEntry> = ArrayList()
    var kcal_series: MutableList<BarEntry> = ArrayList()
    var kcalB_series: MutableList<Entry> = ArrayList()
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
    fun getKcalLabel(bk:MutableList<String>, k:MutableList<String>):List<String>{
        val result = bk.union(k)
        Log.i("kcalseries_k", k.size.toString())
        Log.i("kcalseries_bk", bk.size.toString())
        Log.i("kcalseries_result", result.size.toString())
        return result.distinct()
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
        graph.getDescription().setEnabled(false)
        graph.setPinchZoom(true)
        graph.setDrawBarShadow(false)
        graph.setDrawGridBackground(false)
        val l = graph.getLegend()
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM)
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT)
        l.setOrientation(Legend.LegendOrientation.VERTICAL)
        l.setDrawInside(true)
        l.setTypeface(Typeface.MONOSPACE)
        l.setYOffset(0f)
        l.setXOffset(10f)
        l.setYEntrySpace(0f)
        l.setTextSize(8f)
        val rAxis = graph.getAxisRight()
        rAxis.setEnabled(false)
        val xAxis = graph.getXAxis()
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        graph.setVisibleXRangeMaximum(5.toFloat())
        graph.setNoDataText("아래 머튼을 클릭하시면 해당 기록이 이곳에 보여집니다.")
        graph.setNoDataTextColor(R.color.colorPrimaryDark)
        graph.setDrawOrder(arrayOf(CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE))
    }
   fun series_dataSet():MutableList<BarEntry>{
        val series: MutableList<BarEntry> = ArrayList()
        val x = value.size-1
        for (a: Int in 0..x) {
            //series.add(BarEntry(horizonValue[a].time.toFloat(), value.get(a).toFloat()))
            series.add(a, BarEntry(a.toFloat(), value.get(a).toFloat()))
        }
        return series
    }
    @SuppressLint("SimpleDateFormat")
    fun kcalseries_dataSet(f:MutableList<Date>, v:MutableList<Double>, k:Int):MutableList<Entry>{
        val series: MutableList<Entry> = ArrayList()
        val temp_Label:MutableList<String> =  ArrayList()
        val label = SimpleDateFormat("MM/dd")
        var s = 0
        for(b:Int in 0..(f.size-1)){
            temp_Label.add(b, label.format(f[b]))
        }
        for (a: Int in 0..k) {
            //series.add(BarEntry(horizonValue[a].time.toFloat(), value.get(a).toFloat()))
            if(temp_Label.contains(kcal_Label[a])){
                series.add(a, Entry(a.toFloat(), v[s].toFloat()))
                s += 1
            }else{
                series.add(a, Entry(a.toFloat(), 0f))
            }
        }
        return series
    }
    @SuppressLint("SimpleDateFormat")
    fun kcalseriesB_dataSet(f:MutableList<Date>, v:MutableList<Double>, k:Int):MutableList<BarEntry>{
        val series: MutableList<BarEntry> = ArrayList()
        val temp_Label:MutableList<String> =  ArrayList()
        val label = SimpleDateFormat("MM/dd")
        var s = 0
        for(b:Int in 0..(f.size-1)){
            temp_Label.add(b, label.format(f[b]))
        }
        for (a: Int in 0..k) {
            //series.add(BarEntry(horizonValue[a].time.toFloat(), value.get(a).toFloat()))
            if(temp_Label.contains(kcal_Label[a])){
                series.add(a, BarEntry(a.toFloat(), v[s].toFloat()))
                s += 1
            }else{
                series.add(a, BarEntry(a.toFloat(), 0f))
            }
        }
        return series
    }
    @SuppressLint("SimpleDateFormat")
    fun label_dataSet():MutableList<String> {
        val label = SimpleDateFormat("MM/dd")
        val series: MutableList<String> = ArrayList()
        val x = horizonValue.size - 1
        for (a: Int in 0..x) {
            series.add(a, label.format(horizonValue[a]))
        }
        return series
    }
    @SuppressLint("SimpleDateFormat")
    fun kcal_label_dataSet():MutableList<String> {
        val label = SimpleDateFormat("MM/dd")
        val series: MutableList<String> = ArrayList()
        val x = kcal_horizonValue.size - 1
        for (a: Int in 0..x) {
            series.add(a, label.format(kcal_horizonValue[a]))
        }
        return series
    }
 suspend fun graphSet(p:Int){
        when(p){
            0->{//체중
                if(mListener!!.readResponse == null){
                    Log.i(TAG, "체중 없음")
                }else {
                    if (graph.getData() != null &&
                            graph.getData().getDataSetCount() > 0) {
                        graph.data.clearValues()
                    }
                    if(weight_series.isEmpty()){
                        val job = launch {
                         printData(mListener!!.readResponse!!, p)
                        }
                        job.join()
                        weight_series = series_dataSet()
                        weight_Label = label_dataSet()
                    }
                    val set1 = BarDataSet(weight_series, getString(R.string.weight))
                    set1.setColors(Color.rgb(40, 184, 184))
                    val barData = BarData(set1)
                    val xAxis = graph.xAxis
                    xAxis.setGranularity(1f)
                    xAxis.setValueFormatter(MyXAxisValueFormatter(weight_Label.toTypedArray()))
                    val data = CombinedData()
                    data.setData(barData)
                    graph.setData(data)
                    graph.data.notifyDataChanged()
                    graph.notifyDataSetChanged()
                    }
            }
            1->{//걷기
                if(mListener!!.walkResponse == null){
                    Log.i(TAG, "걷기 없음")
                }else {
                    if (graph.getData() != null &&
                            graph.getData().getDataSetCount() > 0) {
                        graph.data.clearValues()
                    }
                    if(walk_series.isEmpty()) {
                        val job = launch {
                            printData(mListener!!.walkResponse!!, p)
                        }
                        job.join()
                        walk_series = series_dataSet()
                        walk_Label = label_dataSet()
                    }
                    val set1 = BarDataSet(walk_series, getString(R.string.walk))
                    set1.setColors(Color.rgb(184, 182, 85))
                    val barData = BarData(set1)
                    val xAxis = graph.xAxis
                    xAxis.setGranularity(1f)
                    xAxis.setValueFormatter(MyXAxisValueFormatter(walk_Label.toTypedArray()))
                    val data = CombinedData()
                    data.setData(barData)
                    graph.setData(data)
                    graph.data.notifyDataChanged()
                    graph.notifyDataSetChanged()
                }
            }
            2-> {//칼로리
                if (mListener!!.kcalResponse == null) {
                    Log.i(TAG, "칼로리 없음")
                } else {
                    if (graph.data != null &&
                            graph.data.getDataSetCount() > 0) {
                        graph.data.clearValues()
                    }
                    if(kcal_series.isEmpty()) {
                        val job = launch {
                            printData(mListener!!.kcalResponse!!, p)
                        }
                        job.join()
                        val job_second = launch {
                            kcal_horizonValue.clear()
                            kcalvalue.clear()
                            for (dataSet: DataSet in mListener!!.BkcalResponse!!.getDataSets()) {
                                for (dp: com.google.android.gms.fitness.data.DataPoint in dataSet.getDataPoints()) {
                                    kcalvalue.add(dp.getValue(Field.FIELD_CALORIES).asFloat().toDouble())
                                    kcal_horizonValue.add(Date(dp.getTimestamp(TimeUnit.MICROSECONDS)))
                                }
                            }
                        }
                        job_second.join()
                        val job_third = launch {
                            kcal_Label = getKcalLabel(kcal_label_dataSet(), label_dataSet()).toMutableList()
                            kcalB_series = kcalseries_dataSet(kcal_horizonValue, kcalvalue, kcal_Label.size-1)
                            kcal_series = kcalseriesB_dataSet(horizonValue, value, kcal_Label.size-1)
                        }
                        job_third.join()
                    }
                    val set1 = BarDataSet(kcal_series, getString(R.string.calore))
                    set1.setColors(Color.rgb(184, 187, 85))
                    val set2 = LineDataSet(kcalB_series, getString(R.string.caloreb))
                    set2.setColors(Color.rgb(135, 184, 85))
                    val barData = BarData(set1)
                    val lineData = LineData(set2)
                    val xAxis = graph.xAxis
                    xAxis.setGranularity(1f)
                    xAxis.setValueFormatter(MyXAxisValueFormatter(kcal_Label.toTypedArray()))
                    val data = CombinedData()
                    data.setData(barData)
                    data.setData(lineData)
                    graph.setData(data)
                    graph.data.notifyDataChanged()
                    graph.notifyDataSetChanged()
                }
            }
            3->{//체지방비율
                if(mListener!!.fatResponse == null){
                    Log.i(TAG, "체지방비율 없음")
                }else {
                    if (graph.getData() != null &&
                            graph.getData().getDataSetCount() > 0) {
                        graph.data.clearValues()
                    }
                    if (fat_series.isEmpty()) {
                        val job = launch {
                            printData(mListener!!.fatResponse!!, p)
                        }
                        job.join()
                        fat_series = series_dataSet()
                        fat_Label = label_dataSet()
                    }
                    val set1 = BarDataSet(fat_series, getString(R.string.bodyfat))
                    set1.setColors(Color.rgb(180, 70, 184))
                    val barData = BarData(set1)
                    val xAxis = graph.xAxis
                    xAxis.setGranularity(1f)
                    xAxis.setValueFormatter(MyXAxisValueFormatter(fat_Label.toTypedArray()))
                    val data = CombinedData()
                    data.setData(barData)
                    graph.setData(data)
                    graph.data.notifyDataChanged()
                    graph.notifyDataSetChanged()
                }
            }
            4->{//골격근
                if(mListener!!.muscleResponse == null){
                    Log.i(TAG, "골격근 없음")
                }else {
                    if (graph.getData() != null &&
                            graph.getData().getDataSetCount() > 0) {
                        graph.data.clearValues()
                    }
                    if(muscle_series.isEmpty()) {
                        val job = launch {
                            printData(mListener!!.muscleResponse!!, p)
                        }
                        job.join()
                        muscle_series = series_dataSet()
                        muscle_Label = label_dataSet()
                    }
                        val set1 = BarDataSet(muscle_series, getString(R.string.musclemass))
                        set1.setColors(Color.rgb(60, 187, 184))
                        val barData = BarData(set1)
                        val xAxis = graph.xAxis
                        xAxis.setGranularity(1f)
                        xAxis.setValueFormatter(MyXAxisValueFormatter(muscle_Label.toTypedArray()))
                    val data = CombinedData()
                    data.setData(barData)
                    graph.setData(data)
                    graph.data.notifyDataChanged()
                    graph.notifyDataSetChanged()
                }
            }
            5->{//BMI
                if(mListener!!.bmiResponse == null){
                    Log.i(TAG, "BMI 없음")
                }else {
                    if (graph.getData() != null &&
                            graph.getData().getDataSetCount() > 0) {
                        graph.data.clearValues()
                    }
                    if(bmi_series.isEmpty()) {
                        val job = launch {
                            printData(mListener!!.bmiResponse!!, p)
                        }
                        job.join()
                        bmi_series = series_dataSet()
                        bmi_Label = label_dataSet()
                    }
                        val set1 = BarDataSet(bmi_series, getString(R.string.bmi))
                        set1.setColors(Color.rgb(184, 151, 85))
                        val barData = BarData(set1)
                        val xAxis = graph.xAxis
                        xAxis.setGranularity(1f)
                        xAxis.setValueFormatter(MyXAxisValueFormatter(bmi_Label.toTypedArray()))
                    val data = CombinedData()
                    data.setData(barData)
                    graph.setData(data)
                    graph.data.notifyDataChanged()
                    graph.notifyDataSetChanged()
                }
                }
        }
        graph.invalidate()
    }
    @SuppressLint("SimpleDateFormat")
    fun printData(dataReadResult: DataReadResponse, i:Int) {
        val label = SimpleDateFormat("MM/dd")
        var ia = 0
        if (dataReadResult.getBuckets().size > 0) {
            horizonValue.clear()
            value.clear()
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
        } else if (dataReadResult.getDataSets().size > 0) {
            horizonValue.clear()
            value.clear()
            Log.i("printData", "Number of returned DataSets is: " + dataReadResult.getDataSets().size);
            for (dataSet: DataSet in dataReadResult.getDataSets()) {
                dumpDataSet(dataSet)
                ia += 1
                Log.i("printData", "\tia : " + ia.toString())
            }
        }
    }
    @SuppressLint("SimpleDateFormat")
   fun printBucket(bucket:Bucket, i: Int) {
        Log.i("printData", "printBucket")
        val label = SimpleDateFormat("MM/dd")
        when(i){
            1 -> {//걷기
                val set = bucket.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA)!!
                for(dp:com.google.android.gms.fitness.data.DataPoint in set.dataPoints) {
                    if (bucket.getEndTime(TimeUnit.MILLISECONDS) > 0) {
                        if (dp.getValue(Field.FIELD_STEPS).asInt() > 10) {
                            horizonValue.add(Date(bucket.getEndTime(TimeUnit.MILLISECONDS)))
                            value.add(dp.getValue(Field.FIELD_STEPS).asInt().toDouble())
                        }
                    }
                }
            }
            2 -> {//칼로리
                val set = bucket.getDataSet(DataType.AGGREGATE_CALORIES_EXPENDED)!!
                 for(dp:com.google.android.gms.fitness.data.DataPoint in set.dataPoints) {
                    horizonValue.add(Date(bucket.getEndTime(TimeUnit.MILLISECONDS)))
                     value.add(dp.getValue(Field.FIELD_CALORIES).asFloat().toDouble())
                    // value.add(dp.getValue(Field.FIELD_CALORIES).asFloat().toDouble())
                }
            }
        }
    }
    @SuppressLint("SimpleDateFormat")
    private fun dumpDataSet(dataSet:DataSet) {
        val label = SimpleDateFormat("MM/dd")
        var ia = 0

        for ( dp : com.google.android.gms.fitness.data.DataPoint in dataSet.getDataPoints()) {
            if(dp.getValue(dp.getDataType().fields.get(0)).toString().toDouble() > 0.5) {
                horizonValue.add(Date(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                value.add(dp.getValue(dp.getDataType().fields.get(0)).toString().toDouble())
                ia += 1
            }
        }
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
        var BkcalResponse: DataReadResponse?
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

class MyXAxisValueFormatter(private val mValues: Array<String>) : IAxisValueFormatter {

    /** this is only needed if numbers are returned, else return 0  */
    val decimalDigits: Int
        get() = 0

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        // "value" represents the position of the label on the axis (x or y)
        if(value.toInt()<mValues.size){
            return mValues[value.toInt()]
        }else{
            return ""
        }
    }
}