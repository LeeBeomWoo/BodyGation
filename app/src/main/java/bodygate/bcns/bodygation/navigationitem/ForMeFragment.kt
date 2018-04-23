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
import bodygate.bcns.bodygation.R.id.*
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.result.DataReadResponse
import com.jjoe64.graphview.helper.StaticLabelsFormatter
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.fragment_for_me.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.text.SimpleDateFormat
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
                if (bfp_Btn.isChecked){
                    async(UI) {graphSet(3)}
                }
            }
            R.id.bmi_Btn -> {
                bfp_Btn.setChecked(check)
                if (walk_Btn.isChecked)
                    walk_Btn.setChecked(!check)
                if (weight_Btn.isChecked)
                    weight_Btn.setChecked(!check)
                if (kal_Btn.isChecked)
                    kal_Btn.setChecked(!check)
                if (bfp_Btn.isChecked){
                    async(UI) {graphSet(3)}
                }
            }
            R.id.muscle_Btn -> {
                bfp_Btn.setChecked(check)
                if (walk_Btn.isChecked)
                    walk_Btn.setChecked(!check)
                if (weight_Btn.isChecked)
                    weight_Btn.setChecked(!check)
                if (kal_Btn.isChecked)
                    kal_Btn.setChecked(!check)
                if (bfp_Btn.isChecked){
                    async(UI) {graphSet(3)}
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
    var e_dAte = Array<String>(0){"it = \$it"}
    var o_dAte = Array<String>(0){"it = \$it"}
    var a_dAte = Array<String>(0){"it = \$it"}
    var t_dAte = Array<String>(0){"it = \$it"}
    var m_dAte = Array<String>(0){"it = \$it"}
    var b_dAte = Array<String>(0){"it = \$it"}

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
    }
    fun graphSet(p:Int){
        graph.removeAllSeries()
        when(p){
            0->{//체중
                graph.title = getString(R.string.weight)
                if(mListener!!.weight_dateSET == null){
                    Log.i(TAG, "체중자료 없음")
                }
                graph.addSeries(lineGraph(mListener!!.weight_dateSET))
                val labelhorizon = StaticLabelsFormatter(graph)
                labelhorizon.setHorizontalLabels(e_dAte)
                graph.getGridLabelRenderer().setLabelFormatter(labelhorizon)
                graph.gridLabelRenderer.setHumanRounding(false)
                graph.getGridLabelRenderer().setNumHorizontalLabels(3)
                graph.viewport.isScalable = true
            }
            1->{//걷기
                if(mListener!!.walk_dateSET == null){
                    Log.i(TAG, "걷기 없음")
                }
                graph.title = getString(R.string.walk)
                graph.addSeries(lineGraph(mListener!!.walk_dateSET))
                val labelhorizon = StaticLabelsFormatter(graph)
                labelhorizon.setHorizontalLabels(o_dAte)
                graph.getGridLabelRenderer().setLabelFormatter(labelhorizon)
                graph.gridLabelRenderer.setHumanRounding(false)
                graph.getGridLabelRenderer().setNumHorizontalLabels(3)
                graph.viewport.isScalable = true
            }
            2->{//칼로리
                if(mListener!!.calole_dateSET == null){
                    Log.i(TAG, "칼로리 없음")
                }
                graph.title = getString(R.string.calore)
                graph.addSeries(lineGraph(mListener!!.calole_dateSET))
                val labelhorizon = StaticLabelsFormatter(graph)
                labelhorizon.setHorizontalLabels(a_dAte)
                graph.getGridLabelRenderer().setLabelFormatter(labelhorizon)
                graph.gridLabelRenderer.setHumanRounding(false)
                graph.getGridLabelRenderer().setNumHorizontalLabels(3)
                graph.viewport.isScalable = true
            }
            3->{//체지방비율
                if(mListener!!.bfp_dateSET == null){
                    Log.i(TAG, "체지방비율 없음")
                }
                graph.title = getString(R.string.bodyfat)
                graph.addSeries(lineGraph(mListener!!.bfp_dateSET))
                val labelhorizon = StaticLabelsFormatter(graph)
                labelhorizon.setHorizontalLabels(t_dAte)
                graph.getGridLabelRenderer().setLabelFormatter(labelhorizon)
                graph.gridLabelRenderer.setHumanRounding(false)
                graph.getGridLabelRenderer().setNumHorizontalLabels(3)
                graph.viewport.isScalable = true
            }
            4->{//골격근
                if(mListener!!.muscle_dateSET == null){
                    Log.i(TAG, "골격근 없음")
                }
                graph.title = getString(R.string.musclemass)
                graph.addSeries(lineGraph(mListener!!.muscle_dateSET))
                val labelhorizon = StaticLabelsFormatter(graph)
                labelhorizon.setHorizontalLabels(m_dAte)
                graph.getGridLabelRenderer().setLabelFormatter(labelhorizon)
                graph.gridLabelRenderer.setHumanRounding(false)
                graph.getGridLabelRenderer().setNumHorizontalLabels(3)
                graph.viewport.isScalable = true
            }
            4->{//BMI
                if(mListener!!.bmi_dateSET == null){
                    Log.i(TAG, "BMI 없음")
                }
                graph.title = getString(R.string.musclemass)
                graph.addSeries(lineGraph(mListener!!.bmi_dateSET))
                val labelhorizon = StaticLabelsFormatter(graph)
                labelhorizon.setHorizontalLabels(b_dAte)
                graph.getGridLabelRenderer().setLabelFormatter(labelhorizon)
                graph.gridLabelRenderer.setHumanRounding(false)
                graph.getGridLabelRenderer().setNumHorizontalLabels(3)
                graph.viewport.isScalable = true
            }
        }
        graph.onDataChanged(true, true)

    }
    @SuppressLint("SimpleDateFormat")
// TODO: Rename method, update argument and hook method into UI event
    fun lineGraph(dataSet:DataSet?):LineGraphSeries<DataPoint>?{
        if (dataSet != null) {
            Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName())
            val label = SimpleDateFormat("MM/dd")
            val siz = dataSet.dataPoints.size
            val evalue = DoubleArray(siz)
            val avalue = DoubleArray(siz)
            val tvalue = DoubleArray(siz)
            Log.i(TAG, "Data size:" + dataSet.dataPoints.size.toString())
                e_dAte = Array<String>(siz){"it = \$it"}
            val elist = ArrayList<DataPoint>(siz)
            val alist = ArrayList<DataPoint>(siz)
            val tlist = ArrayList<DataPoint>(siz)
            var i = 0
            for (dp: com.google.android.gms.fitness.data.DataPoint in dataSet.getDataPoints()) {
                Log.i(TAG, "Data point:" + dp.toString())
                Log.i(TAG, "Type: " + dp.getDataType().getName());
                Log.i(TAG, "Start: " + label.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                Log.i(TAG, "End: " + label.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                Log.i(TAG, "TimeStemp: " + label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)) + "type: " + dp.getTimestamp(TimeUnit.MILLISECONDS).javaClass)
                when(dataSet.dataType){
                    DataType.TYPE_WEIGHT->{
                        Log.i(TAG, " Value: " + dp.getValue(Field.FIELD_WEIGHT) + "type: " + dp.getValue(Field.FIELD_WEIGHT).javaClass)
                        e_dAte.set(i, label.format(dp.getTimestamp(TimeUnit.MILLISECONDS).toDouble()))
                        evalue.set(i, dp.getValue(Field.FIELD_WEIGHT).toString().toDouble())
                    }
                    DataType.AGGREGATE_CALORIES_EXPENDED->{
                        Log.i(TAG, " Value: " + dp.getValue(Field.FIELD_CALORIES) + "type: " + dp.getValue(Field.FIELD_CALORIES).javaClass)
                        a_dAte.set(i, label.format(dp.getTimestamp(TimeUnit.MILLISECONDS).toDouble()))
                        avalue.set(i, dp.getValue(Field.FIELD_CALORIES).toString().toDouble())
                    }
                    DataType.AGGREGATE_STEP_COUNT_DELTA->{
                        Log.i(TAG, " Value: " + dp.getValue(Field.FIELD_STEPS) + "type: " + dp.getValue(Field.FIELD_STEPS).javaClass)
                        t_dAte.set(i, label.format(dp.getTimestamp(TimeUnit.MILLISECONDS).toDouble()))
                        tvalue.set(i, dp.getValue(Field.FIELD_STEPS).toString().toDouble())
                    }
                }
                i += 1
            }
            var e = 0
            var a = 0
            var t = 0
            when(dataSet.dataType){
                DataType.TYPE_WEIGHT->{
                    if(e_dAte.size > 1) {
                        e_dAte.forEach {
                            Log.i(TAG, "dAte value:" + e_dAte[e])
                            Log.i(TAG, "dAte point:" + e_dAte[e])
                            Log.i(TAG, "value point:" + evalue[e].toString())
                            Log.i(TAG, "a point:" + e.toString())
                            elist.add(DataPoint(e.toDouble(), evalue[e]))
                            e +=1
                        }
                    }
                }
                DataType.AGGREGATE_CALORIES_EXPENDED->{
                    if(a_dAte.size > 1) {
                        a_dAte.forEach {
                            Log.i(TAG, "dAte value:" + a_dAte[a])
                            Log.i(TAG, "dAte point:" + a_dAte[a])
                            Log.i(TAG, "value point:" + avalue[a].toString())
                            Log.i(TAG, "a point:" + a.toString())
                            alist.add(DataPoint(a.toDouble(), avalue[a]))
                            a +=1
                        }
                    }
                }
                DataType.AGGREGATE_STEP_COUNT_DELTA->{
                    if(t_dAte.size > 1) {
                        t_dAte.forEach {
                            Log.i(TAG, "dAte value:" + t_dAte[t])
                            Log.i(TAG, "dAte point:" + t_dAte[t])
                            Log.i(TAG, "value point:" + tvalue[t].toString())
                            Log.i(TAG, "a point:" + t.toString())
                            tlist.add(DataPoint(t.toDouble(), tvalue[t]))
                            t +=1
                        }
                    }
                }
            }
            when(dataSet.dataType){
                DataType.TYPE_WEIGHT->{
                    val series = LineGraphSeries<DataPoint>(elist.toTypedArray())
                    series.setColor(Color.GREEN)
                    return series
                }
                DataType.AGGREGATE_CALORIES_EXPENDED->{
                    val series = LineGraphSeries<DataPoint>(alist.toTypedArray())
                    series.setColor(Color.RED)
                    return series
                }
                DataType.AGGREGATE_STEP_COUNT_DELTA->{
                    val series = LineGraphSeries<DataPoint>(tlist.toTypedArray())
                    series.setColor(Color.DKGRAY)
                    return series
                }
            }
        }
        return null
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
        var weight_dateSET: DataSet?
        var bfp_dateSET: DataSet?
        var walk_dateSET: DataSet?
        var calole_dateSET: DataSet?
        var muscle_dateSET: DataSet?
        var bmi_dateSET: DataSet?
        var weight_list:Array<DataPoint>?
        var bfp_list:Array<DataPoint>?
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
