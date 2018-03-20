package bodygate.bcns.bodygation.navigationitem

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.service.autofill.Dataset
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bodygate.bcns.bodygation.MainActivity
import bodygate.bcns.bodygation.R
import bodygate.bcns.bodygation.R.id.weight_graph
import com.google.android.gms.fitness.data.Bucket
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.data.Value
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.fitness.result.DataSourcesResult
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import java.text.DateFormat
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
class ForMeFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: DoubleArray? = null
    private var mParam3: Array<String>? = null
    private var datalist: Array<DataPoint>? = null
    private var mListener: OnForMeInteraction? = null
    val TAG = "ForMeFragment_"

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
        val view = inflater.inflate(R.layout.fragment_for_me, container, false)
        val bmi_graph:GraphView = view.findViewById(R.id.bmi_graph)
        val weight_graph:GraphView = view.findViewById(R.id.weight_graph)
        mListener!!.OnForMeInteraction()
        bmi_graph.title = getString(R.string.bmi)
        weight_graph.title = getString(R.string.weight)
        val bmi_lineseries = LineGraphSeries<DataPoint>(arrayOf<DataPoint>(DataPoint(0.0, 6.0), DataPoint(1.0, 6.0), DataPoint(2.0, 6.0), DataPoint(3.0, 6.0), DataPoint(4.0, 6.0)))
       // weight_graph.addSeries(dumpDataSet(mListener!!.dateSET!!))
        dumpDataSet(mListener!!.dateSET!!)
        bmi_graph.addSeries(bmi_lineseries)
        bmi_graph.getViewport().setScalable(true)
        weight_graph.getViewport().setScalable(true)
        return view
    }
    // TODO: Rename method, update argument and hook method into UI event
    fun dumpDataSet(dataSet: DataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        val dateFormat = DateFormat.getDateInstance()
        val dAte:LongArray = LongArray(dataSet.dataPoints.size)
        val value:DoubleArray = DoubleArray(dataSet.getDataPoints().size)
        var i = 0
        for (dp: com.google.android.gms.fitness.data.DataPoint in dataSet.getDataPoints()) {
            Log.i(TAG, "Data point:" + dp.toString())
            Log.i(TAG, "Type: " + dp.getDataType().getName());
            Log.i(TAG, "Start: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "End: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "TimeStemp: " + dateFormat.format(dp.getTimestamp(TimeUnit.MILLISECONDS)) + "type: " + dp.getTimestamp(TimeUnit.MILLISECONDS).javaClass)
            Log.i(TAG, " Value: " + dp.getValue(Field.FIELD_WEIGHT) + "type: " + dp.getValue(Field.FIELD_WEIGHT).javaClass)
            dAte.set(i, dp.getTimestamp(TimeUnit.MILLISECONDS))
            value.set(i, dp.getValue(Field.FIELD_WEIGHT).asString().toDouble())
            i += 1
        }
        var a = 0
        dAte.forEach {
            Log.i(TAG, "dAte point:" + dAte[a].toString())
            Log.i(TAG, "value point:" + value[a].toString())
            Log.i(TAG, "a point:" + a.toString())
        a += 1
        }
        val series = BarGraphSeries<DataPoint>(arrayOf())
       // return series
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

        fun dumpDataSet(dataSet: DataSet)

        fun registerFitnessDataListener()= launch(CommonPool){}
        var dateSET: DataSet?
        var list:Array<DataPoint>?
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
