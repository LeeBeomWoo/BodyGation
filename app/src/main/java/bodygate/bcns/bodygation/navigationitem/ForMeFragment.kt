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
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
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
    val TAG = "ForMeFragment_"
    var height = 0.0
    var width = 0.0
    var e_dAte = Array<String>(0){"it = \$it"} //체중
    var a_dAte = Array<String>(0){"it = \$it"} //kcal
    var t_dAte = Array<String>(0){"it = \$it"} // 걷기
    var o_dAte = Array<String>(0){"it = \$it"} // fat
    var m_dAte = Array<String>(0){"it = \$it"} //muscle
    var b_dAte = Array<String>(0){"it = \$it"} //bmr

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
        weight_Btn.setOnCheckedChangeListener(this)
        kal_Btn.setOnCheckedChangeListener(this)
        walk_Btn.setOnCheckedChangeListener(this)
        bfp_Btn.setOnCheckedChangeListener(this)
        bmi_Btn.setOnCheckedChangeListener(this)
        muscle_Btn.setOnCheckedChangeListener(this)
    }
    fun graphSet(p:Int){
        graph.removeAllSeries()
        when(p){
            0->{//체중
                graph.title = getString(R.string.weight)
                graph.addSeries(printData(mListener!!.readResponse!!, 0))
                val labelhorizon = StaticLabelsFormatter(graph)
                labelhorizon.setHorizontalLabels(e_dAte)
                graph.getGridLabelRenderer().setLabelFormatter(labelhorizon)
                graph.gridLabelRenderer.setHumanRounding(false)
                graph.getGridLabelRenderer().setNumHorizontalLabels(3)
                graph.viewport.setScalable(true)
            }
            1->{//걷기
                if(mListener!!.walkResponse == null){
                    Log.i(TAG, "걷기 없음")
                }else {
                    graph.title = getString(R.string.walk)
                    graph.addSeries(printData(mListener!!.walkResponse!!, 1))
                    val labelhorizon = StaticLabelsFormatter(graph)
                    labelhorizon.setHorizontalLabels(t_dAte)
                    graph.getGridLabelRenderer().setLabelFormatter(labelhorizon)
                    graph.gridLabelRenderer.setHumanRounding(false)
                    graph.getGridLabelRenderer().setNumHorizontalLabels(3)
                    graph.viewport.setScalable(true)
                }
            }
            2->{//칼로리
                if(mListener!!.kcalResponse == null){
                    Log.i(TAG, "칼로리 없음")
                }else {
                    graph.title = getString(R.string.calore)
                    graph.addSeries(printData(mListener!!.walkResponse!!, 2))
                    val labelhorizon = StaticLabelsFormatter(graph)
                    labelhorizon.setHorizontalLabels(a_dAte)
                    graph.getGridLabelRenderer().setLabelFormatter(labelhorizon)
                    graph.gridLabelRenderer.setHumanRounding(false)
                    graph.getGridLabelRenderer().setNumHorizontalLabels(3)
                    graph.viewport.setScalable(true)
                }
            }
            3->{//체지방비율
                if(mListener!!.fatResponse == null){
                    Log.i(TAG, "체지방비율 없음")
                }else {
                    graph.title = getString(R.string.bodyfat)
                    graph.addSeries(printData(mListener!!.walkResponse!!, 3))
                    val labelhorizon = StaticLabelsFormatter(graph)
                    labelhorizon.setHorizontalLabels(o_dAte)
                    graph.getGridLabelRenderer().setLabelFormatter(labelhorizon)
                    graph.gridLabelRenderer.setHumanRounding(false)
                    graph.getGridLabelRenderer().setNumHorizontalLabels(3)
                    graph.viewport.setScalable(true)
                }
            }
            4->{//골격근
                if(mListener!!.muscleResponse == null){
                    Log.i(TAG, "골격근 없음")
                }else {
                    graph.title = getString(R.string.musclemass)
                    graph.addSeries(printData(mListener!!.walkResponse!!, 4))
                    val labelhorizon = StaticLabelsFormatter(graph)
                    labelhorizon.setHorizontalLabels(m_dAte)
                    graph.getGridLabelRenderer().setLabelFormatter(labelhorizon)
                    graph.gridLabelRenderer.setHumanRounding(false)
                    graph.getGridLabelRenderer().setNumHorizontalLabels(3)
                    graph.viewport.setScalable(true)
                }
            }
            5->{//BMI
                if(mListener!!.bmiResponse == null){
                    Log.i(TAG, "BMI 없음")
                }else {
                    graph.title = getString(R.string.bmi)
                    graph.addSeries(printData(mListener!!.walkResponse!!, 5))
                    val labelhorizon = StaticLabelsFormatter(graph)
                    labelhorizon.setHorizontalLabels(b_dAte)
                    graph.getGridLabelRenderer().setLabelFormatter(labelhorizon)
                    graph.gridLabelRenderer.setHumanRounding(false)
                    graph.getGridLabelRenderer().setNumHorizontalLabels(3)
                    graph.viewport.setScalable(true)
                }
            }
        }
        graph.onDataChanged(true, false)
    }
    fun printData(dataReadResult: DataReadResponse, i:Int):LineGraphSeries<com.jjoe64.graphview.series.DataPoint> {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        val line :MutableList<com.jjoe64.graphview.series.DataPoint> = ArrayList()
        if (dataReadResult.getBuckets().size > 0) {
            Log.i("printData", "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size)
            for (bucket :Bucket in dataReadResult.getBuckets()) {
                val dataSets = bucket.getDataSets();
                for ( dataSet : DataSet in dataSets) {
                    line.addAll(dumpDataSet(dataSet, i))
                }
            }
        } else if (dataReadResult.getDataSets().size > 0) {
            Log.i("printData", "Number of returned DataSets is: " + dataReadResult.getDataSets().size);
            for ( dataSet :DataSet in dataReadResult.getDataSets()) {
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
        val siz = dataSet.dataPoints.size
        val value = DoubleArray(siz)
        Log.i(TAG, "Data size:" + dataSet.dataPoints.size.toString())
        Log.i(TAG, "Data set:" + dataSet.toString())
        val mlist = ArrayList<com.jjoe64.graphview.series.DataPoint>(siz)
        var ia = 0

        when(i){
            0->{//체중
                e_dAte = Array<String>(siz){"it = \$it"} //체중
            }
            1-> {//걷기
                t_dAte = Array<String>(siz){"it = \$it"} // 걷기
            }
            2->{//칼로리
                a_dAte = Array<String>(siz){"it = \$it"} //kcal
            }
            3->{//체지방
                o_dAte = Array<String>(siz){"it = \$it"} // fat
            }
            4->{//골격근
                m_dAte = Array<String>(siz){"it = \$it"} //muscle
            }
            5->{//bmr
                b_dAte = Array<String>(siz){"it = \$it"} //bmr
            }
        }
        for ( dp : com.google.android.gms.fitness.data.DataPoint in dataSet.getDataPoints()) {
            Log.i("printData", "Data point:");
            Log.i("printData", "\tType: " + dp.getDataType().getName());
            Log.i("printData", "\tStart: " + label.format(dp.getStartTime(TimeUnit.MILLISECONDS)))
            Log.i("printData", "\tEnd: " + label.format(dp.getEndTime(TimeUnit.MILLISECONDS)))
            Log.i("printData", "\tTimestamp: " + label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
            for (field :Field in dp.getDataType().getFields()) {
                Log.i("printData", "\tField: " + field.getName() + " Value: " + dp.getValue(field))
                when(i){
                    0->{//체중
                        e_dAte.set(ia, label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                         }
                    1-> {//걷기
                        t_dAte.set(ia, label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                    }
                    2->{//칼로리
                        a_dAte.set(ia, label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                    }
                    3->{//체지방
                        o_dAte.set(ia, label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                    }
                    4->{//골격근
                        m_dAte.set(ia, label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                    }
                    5->{//bmr
                        b_dAte.set(ia, label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                    }
                }
                value.set(ia, dp.getValue(field).toString().toDouble())
                ia += 1
            }
        }

        var e = 0
        when(i) {
            0 -> {//체중
                if(e_dAte.size > 1) {
                    mlist.add(com.jjoe64.graphview.series.DataPoint(label.parse(e_dAte[e]), value[e]))
                    e +=1
                }
            }
            1 -> {//걷기
                if(t_dAte.size > 1) {
                    mlist.add(com.jjoe64.graphview.series.DataPoint(label.parse(t_dAte[e]), value[e]))
                    e +=1
                }
            }
            2 -> {//칼로리
                if(a_dAte.size > 1) {
                    mlist.add(com.jjoe64.graphview.series.DataPoint(label.parse(a_dAte[e]), value[e]))
                    e +=1
                }
            }
            3 -> {//체지방
                if(o_dAte.size > 1) {
                    mlist.add(com.jjoe64.graphview.series.DataPoint(label.parse(o_dAte[e]), value[e]))
                    e +=1
                }
            }
            4 -> {//골격근
                if(m_dAte.size > 1) {
                    mlist.add(com.jjoe64.graphview.series.DataPoint(label.parse(m_dAte[e]), value[e]))
                    e +=1
                }
            }
            5 -> {//bmr
                if(b_dAte.size > 1) {
                    mlist.add(com.jjoe64.graphview.series.DataPoint(label.parse(b_dAte[e]), value[e]))
                    e +=1
                }
            }
        }
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

private fun showDataSet(dataSet:DataSet) {
    Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
    val dateFormat = DateFormat.getDateInstance();
    val timeFormat = DateFormat.getTimeInstance();

    for (dp :com.google.android.gms.fitness.data.DataPoint in dataSet.dataPoints) {
        Log.e("History", "Data point:");
        Log.e("History", "tType: " + dp.getDataType().getName());
        Log.e("History", "tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
        Log.e("History", "tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
        for(field : Field in dp.getDataType().getFields()) {
            Log.e("History", "tField: " + field.getName() +
                    " Value: " + dp.getValue(field))
        }
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
