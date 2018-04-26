package bodygate.bcns.bodygation.navigationitem

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
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
import com.jjoe64.graphview.helper.StaticLabelsFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.fragment_for_me.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
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
                    async(UI) {graphSet(0)}
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
                    async(UI) {graphSet(1)}
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
                    async(UI) {graphSet(2)}
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
                    async(UI) {graphSet(3)}
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
                    async(UI) {graphSet(5)}
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
                    async(UI) {graphSet(4)}
                }
            }
        }
    }

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: DoubleArray? = null
    private var mParam3: Array<String>? = null
    private var datalist: Array<DataPoint>? = null
    private var mListener: OnForMeInteraction? = null
    val value:MutableList<Double> =  kotlin.collections.ArrayList()
    val horizonValue:MutableList<String> =  kotlin.collections.ArrayList()
    val verticalValue:MutableList<String> =  kotlin.collections.ArrayList()
    val TAG = "ForMeFragment_"
    var height = 0.0
    var width = 0.0

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
        graph.removeAllSeries()
        horizonValue.clear()
        verticalValue.clear()
        value.clear()
        when(p){
            0->{//체중
                if(mListener!!.readResponse == null){
                    Log.i(TAG, "체지방비율 없음")
                }else {
                    val series = printData(mListener!!.readResponse!!, p)
                    graph.title = getString(R.string.weight)
                    graph.addSeries(series)
                    val labelhorizon = StaticLabelsFormatter(graph)
                    labelhorizon.setHorizontalLabels(horizonValue.toTypedArray())
                    graph.getGridLabelRenderer().setLabelFormatter(labelhorizon)
                    graph.gridLabelRenderer.setHumanRounding(true)
                    graph.getGridLabelRenderer().setNumHorizontalLabels(3)
                    graph.viewport.setScalable(true)
                }
            }
            1->{//걷기
                if(mListener!!.walkResponse == null){
                    Log.i(TAG, "걷기 없음")
                }else {
                    graph.title = getString(R.string.walk)
                    graph.addSeries(printData(mListener!!.walkResponse!!, p))
                    val labelhorizon = StaticLabelsFormatter(graph)
                    labelhorizon.setHorizontalLabels(horizonValue.toTypedArray())
                    graph.getGridLabelRenderer().setLabelFormatter(labelhorizon)
                    graph.gridLabelRenderer.setHumanRounding(true)
                    graph.getGridLabelRenderer().setNumHorizontalLabels(3)
                    graph.viewport.setScalable(true)
                }
            }
            2->{//칼로리
                if(mListener!!.kcalResponse == null){
                    Log.i(TAG, "칼로리 없음")
                }else {
                    graph.title = getString(R.string.calore)
                    graph.addSeries(printData(mListener!!.kcalResponse!!, p))
                    val labelhorizon = StaticLabelsFormatter(graph)
                    labelhorizon.setHorizontalLabels(horizonValue.toTypedArray())
                    graph.getGridLabelRenderer().setLabelFormatter(labelhorizon)
                    graph.gridLabelRenderer.setHumanRounding(true)
                    graph.getGridLabelRenderer().setNumHorizontalLabels(3)
                    graph.viewport.setScalable(true)
                }
            }
            3->{//체지방비율
                if(mListener!!.fatResponse == null){
                    Log.i(TAG, "체지방비율 없음")
                }else {
                    graph.title = getString(R.string.bodyfat)
                    graph.addSeries(printData(mListener!!.fatResponse!!, p))
                    val labelhorizon = StaticLabelsFormatter(graph)
                    labelhorizon.setHorizontalLabels(horizonValue.toTypedArray())
                    graph.getGridLabelRenderer().setLabelFormatter(labelhorizon)
                    graph.gridLabelRenderer.setHumanRounding(true)
                    graph.getGridLabelRenderer().setNumHorizontalLabels(3)
                    graph.viewport.setScalable(true)
                }
            }
            4->{//골격근
                if(mListener!!.muscleResponse == null){
                    Log.i(TAG, "골격근 없음")
                }else {
                    graph.title = getString(R.string.musclemass)
                    graph.addSeries(printData(mListener!!.muscleResponse!!, p))
                    val labelhorizon = StaticLabelsFormatter(graph)
                    labelhorizon.setHorizontalLabels(horizonValue.toTypedArray())
                    graph.getGridLabelRenderer().setLabelFormatter(labelhorizon)
                    graph.gridLabelRenderer.setHumanRounding(true)
                    graph.getGridLabelRenderer().setNumHorizontalLabels(3)
                    graph.viewport.setScalable(true)
                }
            }
            5->{//BMI
                if(mListener!!.bmiResponse == null){
                    Log.i(TAG, "BMI 없음")
                }else {
                    graph.title = getString(R.string.bmi)
                    graph.addSeries(printData(mListener!!.bmiResponse!!, p))
                    val labelhorizon = StaticLabelsFormatter(graph)
                    labelhorizon.setHorizontalLabels(horizonValue.toTypedArray())
                    graph.getGridLabelRenderer().setLabelFormatter(labelhorizon)
                    graph.gridLabelRenderer.setHumanRounding(true)
                    graph.getGridLabelRenderer().setNumHorizontalLabels(3)
                    graph.viewport.setScalable(true)
                }
            }
        }
        graph.onDataChanged(true, false)
    }
    @SuppressLint("SimpleDateFormat")
    fun printData(dataReadResult: DataReadResponse, i:Int):LineGraphSeries<com.jjoe64.graphview.series.DataPoint> {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
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
                    Log.i("printData", "\tdataPoints: " + bucket.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA)!!.dataPoints.getOrNull(0).toString())
                    val data = bucket.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA)!!.dataPoints.getOrNull(0)
                    Log.i("printData", "\tgetValue: " + data?.getValue(Field.FIELD_STEPS).toString())
                    horizonValue.add(label.format(bucket.getEndTime(TimeUnit.MILLISECONDS)))
                    value.add(data?.getValue(Field.FIELD_STEPS).toString().toDouble())
                    ia += 1
                }
                for(a:Int in 0..ia){
                    line.add(com.jjoe64.graphview.series.DataPoint(label.parse(horizonValue.get(a)), value.get(a)))
                }
            } else if (dataReadResult.getDataSets().size > 0) {
                Log.i("printData", "Number of returned DataSets is: " + dataReadResult.getDataSets().size);
                for (dataSet: DataSet in dataReadResult.getDataSets()) {
                    line.addAll(dumpDataSet(dataSet, i))
                }
            }
        val series = LineGraphSeries<com.jjoe64.graphview.series.DataPoint>(line.toTypedArray())
        when(i) {
            0 -> {//체중
                series.setColor(Color.GREEN)
            }
            1 -> {//걷기
                series.setColor(Color.DKGRAY)
            }
            2 -> {//칼로리
                series.setColor(Color.RED)
            }
            3 -> {//체지방
                series.setColor(Color.YELLOW)
            }
            4 -> {//골격근
                series.setColor(Color.BLUE)
            }
            5 -> {//bmr
                series.setColor(Color.MAGENTA)
            }
        }
        return series
        // [END parse_read_data_result]
    }
    @SuppressLint("SimpleDateFormat")
    private fun dumpDataSet(dataSet:DataSet, i:Int): ArrayList<com.jjoe64.graphview.series.DataPoint> {
        Log.i("printData", "Data returned for Data type: " + dataSet.getDataType().getName());
        val label = SimpleDateFormat("MM/dd")
        Log.i("printData", "Data size:" + dataSet.getDataPoints().size.toString())
        Log.i("printData", "Data set:" + dataSet.toString())
        var ia = 0
        for ( dp : com.google.android.gms.fitness.data.DataPoint in dataSet.getDataPoints()) {
            Log.i("printData", "Data point:");
            Log.i("printData", "\tType: " + dp.getDataType().getName());
            Log.i("printData", "\tStart: " + label.format(dp.getStartTime(TimeUnit.MILLISECONDS)))
            Log.i("printData", "\tEnd: " + label.format(dp.getEndTime(TimeUnit.MILLISECONDS)))
            Log.i("printData", "\tTimestamp: " + label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
            Log.i("printData", "\tValue: " + dp.getValue(dp.getDataType().fields.get(0)).toString());
            horizonValue.add(label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
            value.add(dp.getValue(dp.getDataType().fields.get(0)).toString().toDouble())
            ia += 1
            Log.i("printData", "ia: " + ia.toString())
        }
        val mlist:ArrayList<com.jjoe64.graphview.series.DataPoint> = ArrayList((ia+1))
        for(a:Int in 0..ia){
            mlist.add(com.jjoe64.graphview.series.DataPoint(label.parse(horizonValue.get(a)), value.get(a)))
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
