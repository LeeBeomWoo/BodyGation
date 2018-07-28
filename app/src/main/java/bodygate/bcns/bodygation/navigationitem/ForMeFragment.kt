package bodygate.bcns.bodygation.navigationitem

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import bodygate.bcns.bodygation.DataClass
import bodygate.bcns.bodygation.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
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
    lateinit var mParam1: DataClass
    private var mListener: OnForMeInteraction? = null
    val TAG = "ForMeFragment_"
    var section = 0
    var weight_position = 0
    var muscle_position = 0
    var fat_position = 0
    var bmr_position = 0
    var walk_position = 0
    var bmi_position = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
        if (arguments != null) {
            mParam1 = arguments!!.getParcelable(ARG_PARAM1)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i(TAG, "onCreateView")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_for_me, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
            Picasso.get().load(mParam1.personUrl)
                    .placeholder(R.mipmap.toolbarlogo_round)
                    .error(R.mipmap.toolbarlogo_round).into(profile_Image)
        graphdata()
        scrollView.isSmoothScrollingEnabled = true
        /*
        scrollView.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener{
            override fun onScrollChange(v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
                Log.i(TAG, "scroll_child-scrollY: " + (scroll_child.height/6).toString())
                if( -1 < scrollY  && scrollY < scroll_child.height/6 ){
                    if(section != 1){
                        if(mParam1.weight_series.size > 0) {
                            main_lbl.text = mParam1.weight_series.get(weight_position).toString() + "Kg"
                            cal_lbl.text = mParam1.weight_Label.get(weight_position)
                            section = 1
                        }
                    }
                    Log.i(TAG, "scrollY: " + scrollY.toString())
                    Log.i(TAG, "scroll section 1")
                }else if(scroll_child.height/6  < scrollY && scrollY <(scroll_child.height/6)*2 ){
                    if(section != 2){
                        if(mParam1.muscle_series.size > 0) {
                        main_lbl.text = mParam1.muscle_series.get(muscle_position).toString() + "Kg"
                        cal_lbl.text = mParam1.muscle_Label.get(muscle_position)
                        section=2
                    }
                    }
                    Log.i(TAG, "scrollY: " + scrollY.toString())
                    Log.i(TAG, "scroll section 2")
                }else if((scroll_child.height/6)*2 < scrollY && scrollY < (scroll_child.height/6)*3 ){
                    if(section != 3){
                        if(mParam1.fat_series.size > 0) {
                        main_lbl.text = mParam1.fat_series.get(fat_position).toString() + "%"
                        cal_lbl.text = mParam1.fat_Label.get(fat_position)
                        section=3
                    }
                    }
                    Log.i(TAG, "scrollY: " + scrollY.toString())
                    Log.i(TAG, "scroll section 3")
                }else if((scroll_child.height/6)*3 <scrollY && scrollY < (scroll_child.height/6)*4 ){
                    if(section != 4) {
                        if(mParam1.kcal_series.size > 0) {
                        main_lbl.text = mParam1.kcal_series.get(bmr_position).toString() + "Kcal"
                        cal_lbl.text = mParam1.kcal_Label.get(bmr_position)
                        section = 4
                    }
                    }
                    Log.i(TAG, "scrollY: " + scrollY.toString())
                    Log.i(TAG, "scroll section 4")
                }else if((scroll_child.height/6)*4 < scrollY && scrollY <(scroll_child.height/6)*5 ){
                    if(section != 0) {
                        if(mParam1.bmi_series.size > 0) {
                        main_lbl.text = mParam1.bmi_series.get(bmi_position).toString() + "Kg/" + "m\u00B2"
                        cal_lbl.text = mParam1.bmi_Label.get(bmi_position)
                        section = 0
                    }
                    }
                    Log.i(TAG, "scrollY: " + scrollY.toString())
                    Log.i(TAG, "scroll section 5")
                }else if((scroll_child.height/6)*5 < scrollY && scrollY < (scroll_child.height/6)*6 ){
                    if(section != 5) {
                        if (mParam1.walk_series.size > 0) {
                            main_lbl.text = mParam1.walk_series.get(walk_position).toString() + "걸음"
                            cal_lbl.text = mParam1.walk_Label.get(walk_position)
                            section = 5
                        }
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
                        if (bmi_position < mParam1.bmi_series.size) {
                            bmi_position -= 1
                            main_lbl.text = mParam1.bmi_series.get(bmi_position).toString() + "Kg/" + "m\u00B2"
                            cal_lbl.text = mParam1.bmi_Label.get(bmi_position)
                        }else if (bmi_position == 0) {
                            Toast.makeText(mListener!!.context, "가장 오래된 자료입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    1 -> {//체중
                        if (weight_position < mParam1.weight_series.size) {
                            weight_position -= 1
                            main_lbl.text = mParam1.weight_series.get(weight_position).toString() + "Kg"
                            cal_lbl.text = mParam1.weight_Label.get(weight_position)
                        }else if (weight_position == 0) {
                            Toast.makeText(mListener!!.context, "가장 최근 자료입니다.", Toast.LENGTH_SHORT).show()

                        }
                    }
                    2 -> {//골격근
                        if (muscle_position < mParam1.muscle_series.size) {
                            muscle_position -= 1
                            main_lbl.text = mParam1.muscle_series.get(muscle_position).toString() + "Kg"
                            cal_lbl.text = mParam1.muscle_Label.get(muscle_position)}else if (muscle_position == 0) {
                            Toast.makeText(mListener!!.context, "가장 오래된 자료입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    3 -> {//체지방
                        if (fat_position < mParam1.fat_series.size) {
                            fat_position -= 1
                            main_lbl.text = mParam1.fat_series.get(fat_position).toString() + "%"
                            cal_lbl.text = mParam1.fat_Label.get(fat_position)}else if (fat_position == 0) {
                            Toast.makeText(mListener!!.context, "가장 오래된 자료입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    4 -> {
                        //소모칼로리
                        if (bmr_position < mParam1.kcal_series.size) {
                            bmr_position -= 1
                            main_lbl.text = mParam1.kcal_series.get(bmr_position).toString() + "Kcal"
                            cal_lbl.text = mParam1.kcal_Label.get(bmr_position)
                        } else if (bmr_position == 0) {
                            Toast.makeText(mListener!!.context, "가장 오래된 자료입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    5 -> {//걸음수
                        if (walk_position < mParam1.walk_series.size) {
                            walk_position -= 1
                            main_lbl.text = mParam1.walk_series.get(walk_position).toString() + "걸음"
                            cal_lbl.text = mParam1.walk_Label.get(walk_position)
                        }else if (walk_position== 0) {
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
                        if (bmi_position < mParam1.bmi_series.size) {
                            bmi_position += 1
                            main_lbl.text = mParam1.bmi_series.get(bmi_position).toString() + "Kg/" + "m\u00B2"
                            cal_lbl.text = mParam1.bmi_Label.get(bmi_position)
                        }else if (bmi_position == mParam1.bmi_series.size-1) {
                            Toast.makeText(mListener!!.context, "가장 최근 자료입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    1 -> {//체중
                        if (weight_position < mParam1.weight_series.size) {
                            weight_position += 1
                            main_lbl.text = mParam1.weight_series.get(weight_position).toString() + "Kg"
                            cal_lbl.text = mParam1.weight_Label.get(weight_position)}else if (weight_position == mParam1.weight_series.size-1) {
                            Toast.makeText(mListener!!.context, "가장 최근 자료입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    2 -> {//골격근
                        if (muscle_position < mParam1.muscle_series.size) {
                            muscle_position += 1
                            main_lbl.text = mParam1.muscle_series.get(muscle_position).toString() + "Kg"
                            cal_lbl.text = mParam1.muscle_Label.get(muscle_position)}else if (muscle_position == mParam1.muscle_series.size-1) {
                            Toast.makeText(mListener!!.context, "가장 최근 자료입니다.", Toast.LENGTH_SHORT).show()

                        }
                    }
                    3 -> {//체지방
                        if (fat_position < mParam1.fat_series.size) {
                            fat_position += 1
                            main_lbl.text = mParam1.fat_series.get(fat_position).toString() + "%"
                            cal_lbl.text = mParam1.fat_Label.get(fat_position)}else if (fat_position == mParam1.fat_series.size-1) {
                            Toast.makeText(mListener!!.context, "가장 최근 자료입니다.", Toast.LENGTH_SHORT).show()

                        }
                    }
                    4 -> {
                        //소모칼로리
                        if (bmr_position < mParam1.kcal_series.size) {
                            bmr_position += 1
                            main_lbl.text = mParam1.kcal_series.get(bmr_position).toString() + "Kcal"
                            cal_lbl.text = mParam1.kcal_Label.get(bmr_position)
                        } else if (bmr_position == mParam1.kcal_series.size-1) {
                            Toast.makeText(mListener!!.context, "가장 최근 자료입니다.", Toast.LENGTH_SHORT).show()

                        }
                    }
                    5 -> {//걸음수
                        if (walk_position < mParam1.walk_series.size) {
                            walk_position += 1
                            main_lbl.text = mParam1.walk_series.get(walk_position).toString() + "걸음"
                            cal_lbl.text = mParam1.walk_Label.get(walk_position)
                        }else if (walk_position == mParam1.walk_series.size-1) {
                            Toast.makeText(mListener!!.context, "가장 최근 자료입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
        })*/
    }
    fun graphdata(){
        Log.i(TAG, "graphdata")

        graph_bmi.data =  dataSet(5)
        setGraph(graph_bmi)

        graph_bmr.data = dataSet( 2)
        setGraph(graph_bmr)

        graph_fat.data = dataSet( 3)
        setGraph(graph_fat)

        graph_muscle.data = dataSet( 4)
        setGraph(graph_muscle)

        graph_walk.data =  dataSet(1)
        setGraph(graph_walk)

        graph_weight.data = dataSet(0)
        setGraph(graph_weight)
    }
    fun setGraph(graph: BarChart){
        Log.i(TAG, "setGraph")
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
    class MyXAxisValueFormatter(private var mValues: Array<String>) : IAxisValueFormatter {

        override fun getFormattedValue(value: Float, axis: AxisBase): String {
            // "value" represents the position of the label on the axis (x or y)
            if(value.toInt()<mValues.size){
                return mValues[value.toInt()]
            }else{
                return ""
            }
        }
    }
    fun dataSet(section : Int):BarData {
        val data = BarData()
        val list:MutableList<BarEntry> = ArrayList()
        /*
        when (section) {
            0 -> {//체중
                if (mParam1.weight_series.size > 0) {
                    Log.i(TAG, "체중 있음")
                    for(i:Int in mParam1.weight_series.indices){
                        list.add(BarEntry(i.toFloat(), mParam1.weight_series[i]))
                    }
                    weight_position = mParam1.weight_series.size - 1
                    val set1 = BarDataSet(list, getString(R.string.weight))
                    set1.setColors(Color.rgb(65, 192, 193))
                    data.addDataSet(set1)
                    val xA = graph_weight.xAxis
                    xA.setValueFormatter(MyXAxisValueFormatter(mParam1.weight_Label.toTypedArray()))
                } else {
                    Log.i(TAG, "체중 없음")
                    Toast.makeText(this.requireContext(), "현재 구글핏에서 데이터를 받아오지 못했습니다. 구글핏을 확인하시고 데이터가 있는데도 반복될 경우 개발자에게 오류보고 부탁드립니다.", Toast.LENGTH_SHORT).show()
                }
            }
            1 -> {//걷기
                if (mParam1.walk_series.size > 0) {
                    Log.i(TAG, "걷기자료 있음")
                    for(i:Int in mParam1.walk_series.indices){
                        list.add(BarEntry(i.toFloat(), mParam1.walk_series[i].toFloat()))
                    }
                    walk_position = mParam1.walk_series.size - 1
                    val set1 = BarDataSet(list, getString(R.string.walk))
                    set1.setColors(Color.rgb(65, 192, 193))
                    data.addDataSet(set1)
                    val xA = graph_walk.xAxis
                    xA.setValueFormatter(MyXAxisValueFormatter(mParam1.walk_Label.toTypedArray()))
                } else {
                    Log.i(TAG, "걷기자료 없음")
                    Toast.makeText(this.requireContext(), "현재 구글핏에서 데이터를 받아오지 못했습니다. 구글핏을 확인하시고 데이터가 있는데도 반복될 경우 개발자에게 오류보고 부탁드립니다.", Toast.LENGTH_SHORT).show()
                }
            }
            2 -> {//칼로리
                if (mParam1.kcal_series.size > 0) {
                    Log.i(TAG, "칼로리 있음")
                    for(i:Int in mParam1.kcal_series.indices){
                        list.add(BarEntry(i.toFloat(), mParam1.kcal_series[i]))
                    }
                    bmr_position = mParam1.kcal_series.size - 1
                    val set1 = BarDataSet(list, getString(R.string.calore))
                    set1.setColors(Color.rgb(65, 192, 193))
                    data.addDataSet(set1)
                    val xA = graph_bmr.xAxis
                    xA.setValueFormatter(MyXAxisValueFormatter(mParam1.kcal_Label.toTypedArray()))
                } else {
                    Log.i(TAG, "칼로리 없음")
                    Toast.makeText(this.requireContext(), "현재 구글핏에서 데이터를 받아오지 못했습니다. 구글핏을 확인하시고 데이터가 있는데도 반복될 경우 개발자에게 오류보고 부탁드립니다.", Toast.LENGTH_SHORT).show()
                }
            }
            3 -> {//체지방비율
                if (mParam1.fat_series.size > 0) {
                    Log.i(TAG, "체지방비율 있음")
                    for(i:Int in mParam1.fat_series.indices){
                        list.add(BarEntry(i.toFloat(), mParam1.fat_series[i]))
                    }
                    fat_position = mParam1.fat_series.size - 1
                    val set1 = BarDataSet(list, getString(R.string.bodyfat))
                    set1.setColors(Color.rgb(65, 192, 193))
                    data.addDataSet(set1)
                    val xA = graph_fat.xAxis
                    xA.setValueFormatter(MyXAxisValueFormatter(mParam1.fat_Label.toTypedArray()))
                } else {
                    Log.i(TAG, "체지방비율 없음")
                    Toast.makeText(this.requireContext(), "업로드 하신 개인 자료가 존재하지 않습니다. 달성목표로 가셔서 개인 자료를 업로드 하신 후 이용하여 주세요", Toast.LENGTH_SHORT).show()
                }
            }
            4 -> {//골격근
                if (mParam1.muscle_series.size > 0) {
                    Log.i(TAG, "골격근 있음")
                    for(i:Int in mParam1.muscle_series.indices){
                        list.add(BarEntry(i.toFloat(), mParam1.muscle_series[i]))
                    }
                    muscle_position = mParam1.muscle_series.size - 1
                    val set1 = BarDataSet(list, getString(R.string.musclemass))
                    set1.setColors(Color.rgb(65, 192, 193))
                    data.addDataSet(set1)
                    val xA = graph_muscle.xAxis
                    xA.setValueFormatter(MyXAxisValueFormatter(mParam1.muscle_Label.toTypedArray()))
                } else {
                    Log.i(TAG, "골격근 없음")
                    Toast.makeText(this.requireContext(), "업로드 하신 개인 자료가 존재하지 않습니다. 달성목표로 가셔서 개인 자료를 업로드 하신 후 이용하여 주세요", Toast.LENGTH_SHORT).show()
                }
            }
            5 -> {//BMI
                if (mParam1.bmi_series.size > 0) {
                    Log.i(TAG, "BMI 있음")
                    for(i:Int in mParam1.bmi_series.indices){
                        list.add(BarEntry(i.toFloat(), mParam1.bmi_series[i]))
                    }
                    bmi_position = mParam1.bmi_series.size - 1
                    val set1 = BarDataSet(list, getString(R.string.bmi))
                    set1.setColors(Color.rgb(65, 192, 193))
                    data.addDataSet(set1)
                    val xA = graph_bmi.xAxis
                    xA.setValueFormatter(MyXAxisValueFormatter(mParam1.bmi_Label.toTypedArray()))
                } else {
                    Log.i(TAG, "BMI 없음")
                    Toast.makeText(this.requireContext(), "업로드 하신 개인 자료가 존재하지 않습니다. 달성목표로 가셔서 개인 자료를 업로드 하신 후 이용하여 주세요", Toast.LENGTH_SHORT).show()
                }
            }
        }
        */
        return data
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
        val context:Context
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
        fun newInstance(param1: DataClass?): ForMeFragment {
            Log.i(TAG, "ForMeFragment")
            val fragment = ForMeFragment()
            val args = Bundle()
            args.putParcelable(ARG_PARAM1, param1)
            Log.i(TAG, "ForMeFragment")
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
