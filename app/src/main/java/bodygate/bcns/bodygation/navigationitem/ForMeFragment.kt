package bodygate.bcns.bodygation.navigationitem

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bodygate.bcns.bodygation.R
import com.google.android.gms.fitness.result.DataSourcesResult
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries


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
    private var mParam2: String? = null
    private var mListener: OnForMeInteraction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_for_me, container, false)
        val bmi_graph:GraphView = view.findViewById(R.id.bmi_graph)
        val weight_graph:GraphView = view.findViewById(R.id.weight_graph)
        bmi_graph.title = getString(R.string.bmi)
        weight_graph.title = getString(R.string.weight)
       // val bmi_barseries = BarGraphSeries<DataPoint>(arrayOf<DataPoint>(GoogleFitManeger.getFitnessData()[1].getResult().dataPoints.get(1), DataPoint(1.0, 5.0), DataPoint(2.0, 3.0), DataPoint(3.0, 2.0), DataPoint(4.0, 6.0)))
       // bmi_graph.addSeries(bmi_barseries)
        val bmi_lineseries = LineGraphSeries<DataPoint>(arrayOf<DataPoint>(DataPoint(0.0, 6.0), DataPoint(1.0, 6.0), DataPoint(2.0, 6.0), DataPoint(3.0, 6.0), DataPoint(4.0, 6.0)))
        bmi_graph.addSeries(bmi_lineseries)
        val weight_barseries = BarGraphSeries<DataPoint>(arrayOf<DataPoint>(DataPoint(0.0, -2.0), DataPoint(1.0, 5.0), DataPoint(2.0, 3.0), DataPoint(3.0, 2.0), DataPoint(4.0, 6.0)))
        weight_graph.addSeries(weight_barseries)
        val weight_lineseries = LineGraphSeries<DataPoint>(arrayOf<DataPoint>(DataPoint(0.0, 6.0), DataPoint(1.0, 6.0), DataPoint(2.0, 6.0), DataPoint(3.0, 6.0), DataPoint(4.0, 6.0)))
        weight_graph.addSeries(weight_lineseries)
        return view
    }
    fun graphviewSet(view: GraphView): GraphView {
        // activate horizontal zooming and scrolling
        view.getViewport().setScalable(true);
        // activate horizontal scrolling
        view.getViewport().setScrollable(true);
        // activate horizontal and vertical zooming and scrolling
        view.getViewport().setScalableY(true);
        // activate vertical scrolling
        view.getViewport().setScrollableY(true);
        return view
    }
    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.OnForMeInteraction(uri)
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
        fun OnForMeInteraction(uri: Uri)

        fun onResult(dataSourcesResult: DataSourcesResult)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ForMeFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): ForMeFragment {
            val fragment = ForMeFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
