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
import android.util.TimeUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import bodygate.bcns.bodygation.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
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
        if(!mListener!!.dataCom) {
        }else{
            graphdata()
        }
        scrollView.isSmoothScrollingEnabled = true
        scrollView.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener{
            override fun onScrollChange(v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
                Log.i(TAG, "scroll_child-scrollY: " + (scroll_child.height/6).toString())
                if( -1 < scrollY  && scrollY < scroll_child.height/6 ){
                    if(section != 1){
                        main_lbl.text = mListener!!.weight_series.get(mListener!!.weight_position).y.toString() + "Kg"
                        cal_lbl.text = mListener!!.weight_Label.get(mListener!!.weight_position)
                        section = 1
                    }
                    Log.i(TAG, "scrollY: " + scrollY.toString())
                    Log.i(TAG, "scroll section 1")
                }else if(scroll_child.height/6  < scrollY && scrollY <(scroll_child.height/6)*2 ){
                    if(section != 2){
                        main_lbl.text = mListener!!.muscle_series.get(mListener!!.muscle_position).y.toString() + "Kg"
                        cal_lbl.text = mListener!!.muscle_Label.get(mListener!!.muscle_position)
                        section=2
                    }
                    Log.i(TAG, "scrollY: " + scrollY.toString())
                    Log.i(TAG, "scroll section 2")
                }else if((scroll_child.height/6)*2 < scrollY && scrollY < (scroll_child.height/6)*3 ){
                    if(section != 3){
                        main_lbl.text = mListener!!.fat_series.get(mListener!!.fat_position).y.toString() + "%"
                        cal_lbl.text = mListener!!.fat_Label.get(mListener!!.fat_position)
                        section=3
                    }
                    Log.i(TAG, "scrollY: " + scrollY.toString())
                    Log.i(TAG, "scroll section 3")
                }else if((scroll_child.height/6)*3 <scrollY && scrollY < (scroll_child.height/6)*4 ){
                    if(section != 4) {
                        main_lbl.text = mListener!!.kcal_series.get(mListener!!.bmr_position).y.toString() + "Kcal"
                        cal_lbl.text = mListener!!.kcal_Label.get(mListener!!.bmr_position)
                        section = 4
                    }
                    Log.i(TAG, "scrollY: " + scrollY.toString())
                    Log.i(TAG, "scroll section 4")
                }else if((scroll_child.height/6)*4 < scrollY && scrollY <(scroll_child.height/6)*5 ){
                    if(section != 0) {
                        main_lbl.text = mListener!!.bmi_series.get(mListener!!.bmi_position).y.toString() + "Kg/" + "m\u00B2"
                        cal_lbl.text = mListener!!.bmi_Label.get(mListener!!.bmi_position)
                        section = 0
                    }
                    Log.i(TAG, "scrollY: " + scrollY.toString())
                    Log.i(TAG, "scroll section 5")
                }else if((scroll_child.height/6)*5 < scrollY && scrollY < (scroll_child.height/6)*6 ){
                    if(section != 5) {
                        main_lbl.text = mListener!!.walk_series.get(mListener!!.walk_position).y.toString() + "걸음"
                        cal_lbl.text = mListener!!.walk_Label.get(mListener!!.walk_position)
                        section = 5
                    }
                    Log.i(TAG, "scrollY: " + scrollY.toString())
                    Log.i(TAG, "scroll section 6")
                }
            }
        })
        pre_Btn.setOnClickListener(object
            :View.OnClickListener{
            override fun onClick(v: View?) {
                Log.i("Button_pre_Btn", "onClick")
                when (section) {
                    0 -> {//bmi
                        if (mListener!!.bmi_position < mListener!!.bmi_series.size) {
                            mListener!!.bmi_position -= 1
                            main_lbl.text = mListener!!.bmi_series.get(mListener!!.bmi_position).y.toString() + "Kg/" + "m\u00B2"
                            cal_lbl.text = mListener!!.bmi_Label.get(mListener!!.bmi_position)
                        }else if (mListener!!.bmi_position == 0) {
                            Toast.makeText(mListener!!.context, "가장 오래된 자료입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    1 -> {//체중
                        if (mListener!!.weight_position < mListener!!.weight_series.size) {
                            mListener!!.weight_position -= 1
                            main_lbl.text = mListener!!.weight_series.get(mListener!!.weight_position).y.toString() + "Kg"
                            cal_lbl.text = mListener!!.weight_Label.get(mListener!!.weight_position)
                        }else if (mListener!!.weight_position == 0) {
                            Toast.makeText(mListener!!.context, "가장 최근 자료입니다.", Toast.LENGTH_SHORT).show()

                        }
                    }
                    2 -> {//골격근
                        if (mListener!!.muscle_position < mListener!!.muscle_series.size) {
                            mListener!!.muscle_position -= 1
                            main_lbl.text = mListener!!.muscle_series.get(mListener!!.muscle_position).y.toString() + "Kg"
                            cal_lbl.text = mListener!!.muscle_Label.get(mListener!!.muscle_position)}else if (mListener!!.muscle_position == 0) {
                            Toast.makeText(mListener!!.context, "가장 오래된 자료입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    3 -> {//체지방
                        if (mListener!!.fat_position < mListener!!.fat_series.size) {
                            mListener!!.fat_position -= 1
                            main_lbl.text = mListener!!.fat_series.get(mListener!!.fat_position).y.toString() + "%"
                            cal_lbl.text = mListener!!.fat_Label.get(mListener!!.fat_position)}else if (mListener!!.fat_position == 0) {
                            Toast.makeText(mListener!!.context, "가장 오래된 자료입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    4 -> {
                        //소모칼로리
                        if (mListener!!.bmr_position < mListener!!.kcal_series.size) {
                            mListener!!.bmr_position -= 1
                            main_lbl.text = mListener!!.kcal_series.get(mListener!!.bmr_position).y.toString() + "Kcal"
                            cal_lbl.text = mListener!!.kcal_Label.get(mListener!!.bmr_position)
                        } else if (mListener!!.bmr_position == 0) {
                            Toast.makeText(mListener!!.context, "가장 오래된 자료입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    5 -> {//걸음수
                        if (mListener!!.walk_position < mListener!!.walk_series.size) {
                            mListener!!.walk_position -= 1
                            main_lbl.text = mListener!!.walk_series.get(mListener!!.walk_position).y.toString() + "걸음"
                            cal_lbl.text = mListener!!.walk_Label.get(mListener!!.walk_position)
                        }else if (mListener!!.walk_position== 0) {
                            Toast.makeText(mListener!!.context, "가장 오래된 자료입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
        next_Btn.setOnClickListener(object
            :View.OnClickListener{
            override fun onClick(v: View?) {
                Log.i("Button_next_Btn", "onClick")
                when (section) {
                    0 -> {//bmi
                        if (mListener!!.bmi_position < mListener!!.bmi_series.size) {
                            mListener!!.bmi_position += 1
                            main_lbl.text = mListener!!.bmi_series.get(mListener!!.bmi_position).y.toString() + "Kg/" + "m\u00B2"
                            cal_lbl.text = mListener!!.bmi_Label.get(mListener!!.bmi_position)
                        }else if (mListener!!.bmi_position == mListener!!.bmi_series.size-1) {
                            Toast.makeText(mListener!!.context, "가장 최근 자료입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    1 -> {//체중
                        if (mListener!!.weight_position < mListener!!.weight_series.size) {
                            mListener!!.weight_position += 1
                            main_lbl.text = mListener!!.weight_series.get(mListener!!.weight_position).y.toString() + "Kg"
                            cal_lbl.text = mListener!!.weight_Label.get(mListener!!.weight_position)}else if (mListener!!.weight_position == mListener!!.weight_series.size-1) {
                            Toast.makeText(mListener!!.context, "가장 최근 자료입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    2 -> {//골격근
                        if (mListener!!.muscle_position < mListener!!.muscle_series.size) {
                            mListener!!.muscle_position += 1
                            main_lbl.text = mListener!!.muscle_series.get(mListener!!.muscle_position).y.toString() + "Kg"
                            cal_lbl.text = mListener!!.muscle_Label.get(mListener!!.muscle_position)}else if (mListener!!.muscle_position == mListener!!.muscle_series.size-1) {
                            Toast.makeText(mListener!!.context, "가장 최근 자료입니다.", Toast.LENGTH_SHORT).show()

                        }
                    }
                    3 -> {//체지방
                        if (mListener!!.fat_position < mListener!!.fat_series.size) {
                            mListener!!.fat_position += 1
                            main_lbl.text = mListener!!.fat_series.get(mListener!!.fat_position).y.toString() + "%"
                            cal_lbl.text = mListener!!.fat_Label.get(mListener!!.fat_position)}else if (mListener!!.fat_position == mListener!!.fat_series.size-1) {
                            Toast.makeText(mListener!!.context, "가장 최근 자료입니다.", Toast.LENGTH_SHORT).show()

                        }
                    }
                    4 -> {
                        //소모칼로리
                        if (mListener!!.bmr_position < mListener!!.kcal_series.size) {
                            mListener!!.bmr_position += 1
                            main_lbl.text = mListener!!.kcal_series.get(mListener!!.bmr_position).y.toString() + "Kcal"
                            cal_lbl.text = mListener!!.kcal_Label.get(mListener!!.bmr_position)
                        } else if (mListener!!.bmr_position == mListener!!.kcal_series.size-1) {
                            Toast.makeText(mListener!!.context, "가장 최근 자료입니다.", Toast.LENGTH_SHORT).show()

                        }
                    }
                    5 -> {//걸음수
                        if (mListener!!.walk_position < mListener!!.walk_series.size) {
                            mListener!!.walk_position += 1
                            main_lbl.text = mListener!!.walk_series.get(mListener!!.walk_position).y.toString() + "걸음"
                            cal_lbl.text = mListener!!.walk_Label.get(mListener!!.walk_position)
                        }else if (mListener!!.walk_position == mListener!!.walk_series.size-1) {
                            Toast.makeText(mListener!!.context, "가장 최근 자료입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
        })
    }
    fun graphdata(){
        graph_bmi.data = mListener!!.bmi_data
        setGraph(graph_bmi)
        graph_bmr.data = mListener!!.kcal_data
        setGraph(graph_bmr)
        graph_fat.data = mListener!!.fat_data
        setGraph(graph_fat)
        graph_muscle.data = mListener!!.muscle_data
        setGraph(graph_muscle)
        graph_walk.data = mListener!!.walk_data
        setGraph(graph_walk)
        graph_weight.data = mListener!!.weight_data
        setGraph(graph_weight)
    }
    fun setGraph(graph: BarChart){
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
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(2f)
        xAxis.setLabelCount(5, true)
        if(mParam1 !=  null){
            Picasso.get().load(Uri.parse(mParam1))
                    .placeholder(R.mipmap.toolbarlogo_round)
                    .error(R.mipmap.toolbarlogo_round).into(profile_Image)
        }else{
            Picasso.get().load(R.mipmap.toolbarlogo_round).into(profile_Image)
        }
        graph.isDragEnabled = true
        graph.isScaleYEnabled = false
        graph.setVisibleXRangeMaximum(3.toFloat())
        graph.setVisibleXRangeMinimum(5.toFloat())
        graph.getDescription().setEnabled(false)
        graph.setPinchZoom(true)
        graph.setDrawBarShadow(false)
        graph.setDrawGridBackground(false)
        graph.setNoDataText("데이터가 없습니다.")
        graph.setNoDataTextColor(R.color.colorPrimaryDark)
        graph.setBackgroundColor(resources.getColor(R.color.whiteback))
        graph.invalidate()
        graph.centerViewToAnimated(graph.data.xMax, graph.data.yMax, YAxis.AxisDependency.RIGHT, 2000)
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
        var walk_position :Int
        var weight_position :Int
        var muscle_position :Int
        var fat_position :Int
        var bmr_position :Int
        var bmi_position :Int
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

        var dataCom:Boolean
        var weight_data: BarData
        var muscle_data: BarData
        var walk_data: BarData
        var fat_data: BarData
        var bmi_data: BarData
        var kcal_data: BarData
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
