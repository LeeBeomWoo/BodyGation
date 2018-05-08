package bodygate.bcns.bodygation.navigationitem

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bodygate.bcns.bodygation.CheckableImageButton
import bodygate.bcns.bodygation.R
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.google.android.gms.fitness.data.Bucket
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.result.DataReadResponse
import kotlinx.android.synthetic.main.fragment_for_me.*
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

    private var mParam1: String? = null
    private var mListener: OnForMeInteraction? = null
    val value:MutableList<Double> =  ArrayList()
    val valueLabel:MutableList<String> =  ArrayList()
    val horizonValue:MutableList<Date> =  ArrayList()
    val horizonLabel:MutableList<String> =  ArrayList()
    val TAG = "ForMeFragment_"
    var weight_series: MutableList<BarEntry> = ArrayList()
    var muscle_series: MutableList<BarEntry> = ArrayList()
    var walk_series: MutableList<BarEntry> = ArrayList()
    var fat_series: MutableList<BarEntry> = ArrayList()
    var bmi_series: MutableList<BarEntry> = ArrayList()
    var kcal_series: MutableList<BarEntry> = ArrayList()
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
        XAxis xAxis = chart.getXAxis();
xAxis.setPosition(XAxisPosition.BOTTOM);
xAxis.setTextSize(10f);
xAxis.setTextColor(Color.RED);
xAxis.setDrawAxisLine(true);
xAxis.setDrawGridLines(false);
    }
    @SuppressLint("SimpleDateFormat")
    fun graphSet(p:Int){
        val label = SimpleDateFormat("MM/dd")
        horizonLabel.clear()
        valueLabel.clear()
        for(a:Int in 0..(horizonValue.size-1)){
            horizonLabel.add(label.format(horizonValue[a]))
            valueLabel.add(value[a].toString())
        }
        when(p){
            0->{//체중
                if(mListener!!.readResponse == null){
                    Log.i(TAG, "체중 없음")
                }else {
                    if (graph.getData() != null &&
                            graph.getData().getDataSetCount() > 0) {
                        graph.data.clearValues()
                    } else {
                        weight_series = printData(mListener!!.readResponse!!, p)
                        val set1 = BarDataSet(weight_series, getString(R.string.weight))
                        val barData = BarData(set1)
                        val xAxis = graph.xAxis
                        xAxis.setValueFormatter(object : IAxisValueFormatter{
                            val mValues: Array<String> = horizonLabel.toTypedArray()
                            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                                return mValues[value.toInt()]
                            }
                        })
                        graph.setData(barData)
                        graph.getData().notifyDataChanged();
                        graph.notifyDataSetChanged();
                    }
                }
            }
            1->{//걷기
                if(mListener!!.walkResponse == null){
                    Log.i(TAG, "걷기 없음")
                }else {
                    if (graph.getData() != null &&
                            graph.getData().getDataSetCount() > 0) {
                        graph.data.clearValues()
                    } else {
                        walk_series = printData(mListener!!.walkResponse!!, p)
                        val set1 = BarDataSet(walk_series, getString(R.string.walk))
                        val barData = BarData(set1)
                        val xAxis = graph.xAxis
                        xAxis.setValueFormatter(object : IAxisValueFormatter{
                            val mValues: Array<String> = horizonLabel.toTypedArray()
                            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                                return mValues[value.toInt()]
                            }
                        })
                        graph.setData(barData)
                        graph.getData().notifyDataChanged();
                        graph.notifyDataSetChanged();
                    }
                }
            }
            2-> {//칼로리
                if (mListener!!.kcalResponse == null) {
                    Log.i(TAG, "칼로리 없음")
                } else {
                    if (graph.getData() != null &&
                            graph.getData().getDataSetCount() > 0) {
                        graph.data.clearValues()
                    } else {
                        kcal_series = printData(mListener!!.kcalResponse!!, p)
                        val set1 = BarDataSet(kcal_series, getString(R.string.calore))
                        val barData = BarData(set1)
                        val xAxis = graph.xAxis
                        xAxis.setValueFormatter(object : IAxisValueFormatter{
                            val mValues: Array<String> = horizonLabel.toTypedArray()
                            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                                return mValues[value.toInt()]
                            }
                        })
                        graph.setData(barData)
                        graph.getData().notifyDataChanged();
                        graph.notifyDataSetChanged();
                    }
                }
            }
            3->{//체지방비율
                if(mListener!!.fatResponse == null){
                    Log.i(TAG, "체지방비율 없음")
                }else {
                    if (graph.getData() != null &&
                            graph.getData().getDataSetCount() > 0) {
                        graph.data.clearValues()
                    } else {
                        fat_series = printData(mListener!!.fatResponse!!, p)
                        val set1 = BarDataSet(fat_series, getString(R.string.bodyfat))
                        val barData = BarData(set1)
                        val xAxis = graph.xAxis
                        xAxis.setValueFormatter(object : IAxisValueFormatter{
                            val mValues: Array<String> = horizonLabel.toTypedArray()
                            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                                return mValues[value.toInt()]
                            }
                        })
                        graph.setData(barData)
                        graph.data.notifyDataChanged()
                        graph.notifyDataSetChanged()
                    }
                }
            }
            4->{//골격근
                if(mListener!!.muscleResponse == null){
                    Log.i(TAG, "골격근 없음")
                }else {
                    if (graph.getData() != null &&
                            graph.getData().getDataSetCount() > 0) {
                        graph.data.clearValues()
                    } else {
                        muscle_series = printData(mListener!!.muscleResponse!!, p)
                        val set1 = BarDataSet(muscle_series, getString(R.string.musclemass))
                        val barData = BarData(set1)
                        val xAxis = graph.xAxis
                        xAxis.setValueFormatter(object : IAxisValueFormatter{
                            val mValues: Array<String> = horizonLabel.toTypedArray()
                            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                                return mValues[value.toInt()]
                            }
                        })
                        graph.setData(barData)
                        graph.data.notifyDataChanged()
                        graph.notifyDataSetChanged()
                    }
                }
            }
            5->{//BMI
                if(mListener!!.bmiResponse == null){
                    Log.i(TAG, "BMI 없음")
                }else {
                    if (graph.getData() != null &&
                            graph.getData().getDataSetCount() > 0) {
                        graph.data.clearValues()
                    } else {
                        bmi_series = printData(mListener!!.bmiResponse!!, p)
                        val set1 = BarDataSet(bmi_series, getString(R.string.bmi))
                        val barData = BarData(set1)
                        val xAxis = graph.xAxis
                        xAxis.setValueFormatter(object : IAxisValueFormatter{
                            val mValues: Array<String> = horizonLabel.toTypedArray()
                            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                                return mValues[value.toInt()]
                            }
                        })
                        graph.setData(barData)
                        graph.data.notifyDataChanged()
                        graph.notifyDataSetChanged()
                    }
                }
            }
        }
    graph.invalidate()
    }
    @SuppressLint("SimpleDateFormat")
    fun printData(dataReadResult: DataReadResponse, i:Int):MutableList<BarEntry> {
        val label = SimpleDateFormat("MM/dd")
        var ia = 0
        val line :MutableList<BarEntry> = ArrayList()
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
        val x = horizonValue.size-1
        for(a:Int in 0..x){
            line.add(BarEntry(a.toFloat(), value.get(a).toFloat())) //동적
            // line.add(com.jjoe64.graphview.series.DataPoint(a.toDouble(), value.get(a))) //정적
        }
        Log.i("printData_size", "\thorizonValue.size : " + horizonValue.size.toString())
        Log.i("printData_size", "\tvalue.size : " + value.size.toString())
        return line
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
                            Log.i("printData_" + "걷기", "\tTimestamp: " + label.format(Date(bucket.getEndTime(TimeUnit.MILLISECONDS))))
                            Log.i("printData_" + "걷기", "\tgetValue: " + dp.getValue(Field.FIELD_STEPS).toString())
                            horizonValue.add(Date(bucket.getEndTime(TimeUnit.MILLISECONDS)))
                            value.add(dp.getValue(Field.FIELD_STEPS).asInt().toDouble())
                        }
                    }
                }
            }
            2 -> {//칼로리
                val set = bucket.getDataSet(DataType.AGGREGATE_CALORIES_EXPENDED)!!
                for(dp:com.google.android.gms.fitness.data.DataPoint in set.dataPoints) {
                    Log.i("printData_" + "칼로리", "\tTimestamp: " + label.format(Date(bucket.getEndTime(TimeUnit.MILLISECONDS))))
                    Log.i("printData_" + "칼로리", "\tgetValue: " + dp.getValue(Field.FIELD_CALORIES).toString())
                    if (bucket.getEndTime(TimeUnit.MILLISECONDS) > 0) {
                        Log.i("printData_" + "칼로리", "getEndTime")
                        if (dp.getValue(Field.FIELD_CALORIES).asFloat().toDouble() > 100.0) {
                            Log.i("printData_" + "칼로리", "getValue")
                            horizonValue.add(Date(bucket.getEndTime(TimeUnit.MILLISECONDS)))
                            value.add(dp.getValue(Field.FIELD_CALORIES).asFloat().toDouble())
                        }
                    }
                }
            }
        }

    }
    @SuppressLint("SimpleDateFormat")
    private fun dumpDataSet(dataSet:DataSet) {
        Log.i("printData", "Data returned for Data type: " + dataSet.getDataType().getName());
        val label = SimpleDateFormat("MM/dd")
        Log.i("printData", "Data size:" + dataSet.getDataPoints().size.toString())
        Log.i("printData", "Data set:" + dataSet.toString())
        var ia = 0

        for ( dp : com.google.android.gms.fitness.data.DataPoint in dataSet.getDataPoints()) {
            if(dp.getValue(dp.getDataType().fields.get(0)).toString().toDouble() > 0.5) {
                horizonValue.add(Date(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                value.add(dp.getValue(dp.getDataType().fields.get(0)).toString().toDouble())
                ia += 1
                Log.i("printData", "Data point:");
                Log.i("printData_", "\tType: " + dp.getDataType().getName());
                Log.i("printData", "\tStart: " + label.format(dp.getStartTime(TimeUnit.MILLISECONDS)))
                Log.i("printData", "\tEnd: " + label.format(dp.getEndTime(TimeUnit.MILLISECONDS)))
                Log.i("printData_", "\tTimestamp: " + label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                Log.i("printData", "\tValue: " + dp.getValue(dp.getDataType().fields.get(0)).toString());
                Log.i("printData", "ia: " + ia.toString())
            }
        }
        Log.i("printData", "\thorizonValue : " + horizonValue.size.toString())
        Log.i("printData", "\tvalue : " + value.size.toString())
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
