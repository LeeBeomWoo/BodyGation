package bodygate.bcns.bodygation.navigationitem

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import bodygate.bcns.bodygation.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_for_me.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ForMeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ForMeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@SuppressLint("SetTextI18n")
class ForMeFragment : Fragment() {
    private var mParam1: String? = null
    private var mListener: OnForMeInteraction? = null
    val TAG = "ForMeFragment_"
    var section = 0

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
        setGraph(graph_bmi)
        graph_bmi.data = mListener!!.OnForMeInteraction(0)
        setGraph(graph_bmr)
        graph_bmr.data = mListener!!.OnForMeInteraction(4)
        setGraph(graph_fat)
        graph_fat.data = mListener!!.OnForMeInteraction(3)
        setGraph(graph_muscle)
        graph_muscle.data = mListener!!.OnForMeInteraction(2)
        setGraph(graph_walk)
        graph_walk.data = mListener!!.OnForMeInteraction(5)
        setGraph(graph_weight)
        graph_weight.data = mListener!!.OnForMeInteraction(1)
        scrollView.isSmoothScrollingEnabled = true
        scrollView.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener{
            override fun onScrollChange(v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
                if(0 < scroll_child.height/6 && scroll_child.height/6 < 1.1 ){
                    section = 1
                }else if(1 < scroll_child.height/6 && scroll_child.height/6 < 2.1 ){section=2
                }else if(2 < scroll_child.height/6 && scroll_child.height/6 < 3.1 ){section=3
                }else if(3 < scroll_child.height/6 && scroll_child.height/6 < 4.1 ){section=4
                }else if(4 < scroll_child.height/6 && scroll_child.height/6 < 5.1 ){section=0
                }else if(5 < scroll_child.height/6 && scroll_child.height/6 < 7 ){section=5
                }
            }
        })
        pre_Btn.setOnClickListener(object
            :View.OnClickListener{
            override fun onClick(v: View?) {
                Log.i("Button_pre_Btn", "onClick")
                    if (mListener!!.current_position > 0) {
                        mListener!!.current_position -= 1
                        when (section) {
                            0 -> {//bmi
                                main_lbl.text = mListener!!.bmi_series.get(mListener!!.current_position).y.toString() + "Kg/" + "m\u00B2"
                                cal_lbl.text = mListener!!.bmi_Label.get(mListener!!.current_position)
                            }
                            1 -> {//체중
                                main_lbl.text = mListener!!.weight_series.get(mListener!!.current_position).y.toString() + "Kg"
                                cal_lbl.text = mListener!!.weight_Label.get(mListener!!.current_position)
                            }
                            2 -> {//골격근
                                main_lbl.text = mListener!!.muscle_series.get(mListener!!.current_position).y.toString() + "Kg"
                                cal_lbl.text = mListener!!.muscle_Label.get(mListener!!.current_position)
                            }
                            3 -> {//체지방
                                main_lbl.text = mListener!!.fat_series.get(mListener!!.current_position).y.toString() + "%"
                                cal_lbl.text = mListener!!.fat_Label.get(mListener!!.current_position)
                            }
                            4 -> {//소모칼로리
                                main_lbl.text = mListener!!.kcal_series.get(mListener!!.current_position).y.toString() + "Kcal"
                                cal_lbl.text = mListener!!.kcal_Label.get(mListener!!.current_position)
                            }
                            5 -> {//걸음수
                                main_lbl.text = mListener!!.walk_series.get(mListener!!.current_position).y.toString() + "걸음"
                                cal_lbl.text = mListener!!.walk_Label.get(mListener!!.current_position)
                            }
                        }
                    } else if (mListener!!.current_position == 0) {
                        Toast.makeText(mListener!!.context, "첫번째 자료입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
        })
        next_Btn.setOnClickListener(object
            :View.OnClickListener{
            override fun onClick(v: View?) {
                Log.i("Button_next_Btn", "onClick")
                    if (mListener!!.current_position < mListener!!.last_position) {
                        mListener!!.current_position += 1
                        when (section) {
                            0 -> {//bmi
                                main_lbl.text = mListener!!.bmi_series.get(mListener!!.current_position).y.toString() + "Kg/" + "m\u00B2"
                                cal_lbl.text = mListener!!.bmi_Label.get(mListener!!.current_position)
                            }
                            1 -> {//체중
                                main_lbl.text = mListener!!.weight_series.get(mListener!!.current_position).y.toString() + "Kg"
                                cal_lbl.text = mListener!!.weight_Label.get(mListener!!.current_position)
                            }
                            2 -> {//골격근
                                main_lbl.text = mListener!!.muscle_series.get(mListener!!.current_position).y.toString() + "Kg"
                                cal_lbl.text = mListener!!.muscle_Label.get(mListener!!.current_position)
                            }
                            3 -> {//체지방
                                main_lbl.text = mListener!!.fat_series.get(mListener!!.current_position).y.toString() + "%"
                                cal_lbl.text = mListener!!.fat_Label.get(mListener!!.current_position)
                            }
                            4 -> {//소모칼로리
                                main_lbl.text = mListener!!.kcal_series.get(mListener!!.current_position).y.toString() + "Kcal"
                                cal_lbl.text = mListener!!.kcal_Label.get(mListener!!.current_position)
                            }
                            5 -> {//걸음수
                                main_lbl.text = mListener!!.walk_series.get(mListener!!.current_position).y.toString() + "걸음"
                                cal_lbl.text = mListener!!.walk_Label.get(mListener!!.current_position)
                            }
                        }
                    } else if (mListener!!.current_position == mListener!!.last_position) {
                        Toast.makeText(mListener!!.context, "가장 최근 자료입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
        })
    }
    fun setGraph(graph: BarChart){
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
        l.setTextSize(11f)
        val rAxis = graph.getAxisRight()
        rAxis.setEnabled(false)
        val xAxis = graph.getXAxis()
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f)
        xAxis.setLabelCount(5, true)
        if(mParam1 !=  null){
            Picasso.get().load(Uri.parse(mParam1))
                    .placeholder(R.mipmap.toolbarlogo_round)
                    .error(R.mipmap.toolbarlogo_round).into(profile_Image)
        }else{
            Picasso.get().load(R.mipmap.toolbarlogo_round).into(profile_Image)
        }
        graph.setNoDataText("데이터가 없습니다.")
        graph.setNoDataTextColor(R.color.colorPrimaryDark)
        graph.setBackgroundColor(resources.getColor(R.color.whiteback))
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
        fun OnForMeInteraction(section:Int):BarData
        var last_position:Int
        var current_position :Int
        var display_label:MutableList<String>
        var display_series: MutableList<String>
        val context:Context
        val weight_series: MutableList<BarEntry>
        val muscle_series: MutableList<BarEntry>
        val walk_series: MutableList<BarEntry>
        val fat_series: MutableList<BarEntry>
        val bmi_series: MutableList<BarEntry>
        val kcal_series: MutableList<BarEntry>

        val weight_Label:MutableList<String>
        val kcal_Label:MutableList<String>
        val walk_Label:MutableList<String>
        val fat_Label:MutableList<String>
        val muscle_Label:MutableList<String>
        val bmi_Label:MutableList<String>
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "img"
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
