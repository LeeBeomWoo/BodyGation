package bodygate.bcns.bodygation.navigationitem

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bodygate.bcns.bodygation.R
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.result.DataReadResponse
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.helper.StaticLabelsFormatter
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.fragment_for_me.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import java.text.DateFormat
import java.text.DateFormat.LONG
import java.text.DateFormat.getDateInstance
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
class ForMeFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: DoubleArray? = null
    private var mParam3: Array<String>? = null
    private var datalist: Array<DataPoint>? = null
    private var mListener: OnForMeInteraction? = null
    val TAG = "ForMeFragment_"
    var height = 0.0
    var width = 0.0
    var weight_dAte = Array<String>(0){"it = \$it"}
    var dAte = Array<String>(0){"it = \$it"}

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
        weight_graph.title = getString(R.string.weight)
        weight_graph.addSeries(dumpDataSet(mListener!!.weight_dateSET))
        weight_graph.addSeries(lineGraph(mListener!!.weight_dateSET, 68.7))
        val labelhorizon = StaticLabelsFormatter(weight_graph)
        labelhorizon.setHorizontalLabels(weight_dAte)
        weight_graph.getGridLabelRenderer().setLabelFormatter(labelhorizon)
        weight_graph.gridLabelRenderer.setHumanRounding(false)
        weight_graph.viewport.isScalable = true
        bfp_graph.title = getString(R.string.bmi)
        bfp_graph.addSeries(dumpDataSet(mListener!!.bfp_dateSET))
        bfp_graph.addSeries(lineGraph(mListener!!.bfp_dateSET, 68.7))
        val bfp_labelhorizon = StaticLabelsFormatter(bfp_graph)
        bfp_labelhorizon.setHorizontalLabels(dAte)
        bfp_graph.getGridLabelRenderer().setLabelFormatter(bfp_labelhorizon)
        bfp_graph.gridLabelRenderer.setHumanRounding(false)
        bfp_graph.viewport.isScalable = true
    }
    @SuppressLint("SimpleDateFormat")
// TODO: Rename method, update argument and hook method into UI event
    fun lineGraph(dataSet:DataSet?, perpose: Double):LineGraphSeries<DataPoint>?{
        if (dataSet != null) {
            Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName())
            val label = SimpleDateFormat("yy/MM/dd hh:mm")
            val siz = dataSet.dataPoints.size
            val value = DoubleArray(siz)
            Log.i(TAG, "Data size:" + dataSet.dataPoints.size.toString())
            if(dataSet.dataType == DataType.TYPE_WEIGHT){
                weight_dAte = Array<String>(siz){"it = \$it"}
            }else {
                dAte = Array<String>(siz) { "it = \$it" }
            }
            val list = ArrayList<DataPoint>(siz)
            var i = 0
            for (dp: com.google.android.gms.fitness.data.DataPoint in dataSet.getDataPoints()) {
                Log.i(TAG, "Data point:" + dp.toString())
                Log.i(TAG, "Type: " + dp.getDataType().getName());
                Log.i(TAG, "Start: " + label.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                Log.i(TAG, "End: " + label.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                Log.i(TAG, "TimeStemp: " + label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)) + "type: " + dp.getTimestamp(TimeUnit.MILLISECONDS).javaClass)
                if(dp.dataType == DataType.TYPE_BODY_FAT_PERCENTAGE){
                Log.i(TAG, " Value: " + dp.getValue(Field.FIELD_PERCENTAGE) + "type: " + dp.getValue(Field.FIELD_PERCENTAGE).javaClass)
                dAte.set(i, label.format(dp.getTimestamp(TimeUnit.MILLISECONDS).toDouble()))
                value.set(i, dp.getValue(Field.FIELD_PERCENTAGE).toString().toDouble())
                value.set(i, perpose)
                }else if(dp.dataType == DataType.TYPE_WEIGHT){
                    Log.i(TAG, " Value: " + dp.getValue(Field.FIELD_WEIGHT) + "type: " + dp.getValue(Field.FIELD_WEIGHT).javaClass)
                    weight_dAte.set(i, label.format(dp.getTimestamp(TimeUnit.MILLISECONDS).toDouble()))
                    value.set(i, dp.getValue(Field.FIELD_WEIGHT).toString().toDouble())
                }
                i += 1
            }
            var a = 0
            var b = 0
            if(dAte.size > 1) {
                dAte.forEach {
                    Log.i(TAG, "dAte value:" + dAte[a])
                    Log.i(TAG, "dAte point:" + dAte[a])
                    Log.i(TAG, "value point:" + value[a].toString())
                    Log.i(TAG, "a point:" + a.toString())
                    list.add(DataPoint(a.toDouble(), value[a]))
                    a += 1
                }
            }
            if(weight_dAte.size > 1) {
                weight_dAte.forEach {
                    Log.i(TAG, "dAte value:" + weight_dAte[b])
                    Log.i(TAG, "dAte point:" + weight_dAte[b])
                    Log.i(TAG, "value point:" + value[b].toString())
                    Log.i(TAG, "a point:" + b.toString())
                    list.add(DataPoint(b.toDouble(), value[b]))
                    b += 1
                }
            }
            val series = LineGraphSeries<DataPoint>(list.toTypedArray())
            return series
        }
        return null
    }
    @SuppressLint("SimpleDateFormat")
    fun dumpDataSet(dataSet: DataSet?):BarGraphSeries<DataPoint>? {
        if (dataSet != null) {
            Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName())
            val siz = dataSet.dataPoints.size
            val value = DoubleArray(siz)
            Log.i(TAG, "Data size:" + dataSet.dataPoints.size.toString())
            val list = ArrayList<DataPoint>(siz)
            if(dataSet.dataType == DataType.TYPE_WEIGHT){
                weight_dAte = Array<String>(siz){"it = \$it"}
            }else {
                dAte = Array<String>(siz) { "it = \$it" }
            }
            var i = 0
            val label = SimpleDateFormat("yy/MM/dd hh:mm")
            for (dp: com.google.android.gms.fitness.data.DataPoint in dataSet.getDataPoints()) {
                Log.i(TAG, "Data point:" + dp.toString())
                Log.i(TAG, "Type: " + dp.getDataType().getName());
                Log.i(TAG, "Start: " + label.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                Log.i(TAG, "End: " + label.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                Log.i(TAG, "TimeStemp: " + label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)) + "type: " + dp.getTimestamp(TimeUnit.MILLISECONDS).javaClass)
                if(dp.dataType == DataType.TYPE_BODY_FAT_PERCENTAGE){
                Log.i(TAG, " Value: " + dp.getValue(Field.FIELD_PERCENTAGE) + "type: " + dp.getValue(Field.FIELD_PERCENTAGE).javaClass)
                dAte.set(i, label.format(dp.getTimestamp(TimeUnit.MILLISECONDS).toDouble()))
                value.set(i, dp.getValue(Field.FIELD_PERCENTAGE).toString().toDouble())
            }else if(dp.dataType == DataType.TYPE_WEIGHT){
                Log.i(TAG, " Value: " + dp.getValue(Field.FIELD_WEIGHT) + "type: " + dp.getValue(Field.FIELD_WEIGHT).javaClass)
                    weight_dAte.set(i, label.format(dp.getTimestamp(TimeUnit.MILLISECONDS).toDouble()))
                value.set(i, dp.getValue(Field.FIELD_WEIGHT).toString().toDouble())
            }
                i += 1
            }
            var a = 0
            var b = 0
            dAte.forEach {
                Log.i(TAG, "dAte value:" + dAte[a])
                Log.i(TAG, "dAte point:" + dAte[a]).toString()
                Log.i(TAG, "value point:" + value[a].toString())
                Log.i(TAG, "a point:" + a.toString())
                list.add(DataPoint(a.toDouble(), value[a]))
                a += 1
            }
            weight_dAte.forEach {
                Log.i(TAG, "dAte value:" + weight_dAte[b])
                Log.i(TAG, "dAte point:" + weight_dAte[b]).toString()
                Log.i(TAG, "value point:" + value[b].toString())
                Log.i(TAG, "a point:" + b.toString())
                list.add(DataPoint(b.toDouble(), value[b]))
                b += 1
            }
            val series = BarGraphSeries<DataPoint>(list.toTypedArray())
            series.spacing = 70
            series.isDrawValuesOnTop = true
            return series
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

        fun printData(dataReadResult: DataReadResponse)

        fun weight_dumpDataSet(dataSet: DataSet)

        fun bfp_dumpDataSet(dataSet: DataSet)

        fun registerFitnessDataListener()= launch(CommonPool){}
        var weight_dateSET: DataSet?
        var bfp_dateSET: DataSet?
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
