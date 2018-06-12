package bodygate.bcns.bodygation.navigationitem

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import bodygate.bcns.bodygation.MainActivity
import bodygate.bcns.bodygation.R
import bodygate.bcns.bodygation.R.id.walk_Btn
import bodygate.bcns.bodygation.support.CheckableImageButton
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.Bucket
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_for_me.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.lang.Exception
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
@SuppressLint("SetTextI18n")
class ForMeFragment : Fragment(), CheckableImageButton.OnCheckedChangeListener {
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
    override fun onCheckedChanged(button: CheckableImageButton?, check: Boolean) {
        Log.i(TAG, "onCheckedChanged")
        if (graph.getData() != null &&
                graph.getData().getDataSetCount() > 0) {
            graph.data.clearValues()
        }
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
                    graph.setData(mListener!!.OnForMeInteraction(0))
                    graph.data.notifyDataChanged()
                    graph.notifyDataSetChanged()
                    graph.invalidate()
                    if (mListener!!.last_position > 0) {
                        mListener!!.current_position = mListener!!.last_position
                        Log.i(TAG, "graph_change : " + mListener!!.last_position.toString())
                        cal_lbl.text = mListener!!.display_label.get(mListener!!.current_position)
                        main_lbl.text = mListener!!.display_series.get(mListener!!.current_position) + "Kg"
                        section = 1
                    }
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
                    graph.setData(mListener!!.OnForMeInteraction(1))
                    graph.data.notifyDataChanged()
                    graph.notifyDataSetChanged()
                    graph.invalidate()
                    if (mListener!!.last_position > 0) {
                        mListener!!.current_position = mListener!!.last_position
                        Log.i(TAG, "graph_change : " + mListener!!.last_position.toString())
                        cal_lbl.text = mListener!!.display_label.get(mListener!!.current_position)
                        main_lbl.text = mListener!!.display_series.get(mListener!!.current_position) + "걸음"
                        section = 5
                    }
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
                if (kal_Btn.isChecked) {
                    graph.setData(mListener!!.OnForMeInteraction(2))
                    graph.data.notifyDataChanged()
                    graph.notifyDataSetChanged()
                    graph.invalidate()
                    if (mListener!!.last_position > 0) {
                        mListener!!.current_position = mListener!!.last_position
                        Log.i(TAG, "graph_change : " + mListener!!.last_position.toString())
                        cal_lbl.text = mListener!!.display_label.get(mListener!!.current_position)
                        main_lbl.text = mListener!!.display_series.get(mListener!!.current_position) + "Kcal"
                        section = 4
                    }
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
                if (bfp_Btn.isChecked) {
                    graph.setData(mListener!!.OnForMeInteraction(3))
                    graph.data.notifyDataChanged()
                    graph.notifyDataSetChanged()
                    graph.invalidate()
                    if (mListener!!.last_position > 0) {
                        mListener!!.current_position = mListener!!.last_position
                        Log.i(TAG, "graph_change : " + mListener!!.last_position.toString())
                        cal_lbl.text = mListener!!.display_label.get(mListener!!.current_position)
                        main_lbl.text = mListener!!.display_series.get(mListener!!.current_position) + "%"
                        section = 3
                    }
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
                    graph.setData(mListener!!.OnForMeInteraction(5))
                    graph.data.notifyDataChanged()
                    graph.notifyDataSetChanged()
                    graph.invalidate()
                    if (mListener!!.last_position > 0) {
                        mListener!!.current_position = mListener!!.last_position
                        Log.i(TAG, "graph_change : " + mListener!!.last_position.toString())
                        cal_lbl.text = mListener!!.display_label.get(mListener!!.current_position)
                        main_lbl.text = mListener!!.display_series.get(mListener!!.current_position) + "Kg/" + "m\u00B2"
                        section = 0
                    }
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
                    graph.setData(mListener!!.OnForMeInteraction(4))
                    graph.data.notifyDataChanged()
                    graph.notifyDataSetChanged()
                    graph.invalidate()
                    if (mListener!!.last_position > 0) {
                        mListener!!.current_position = mListener!!.last_position
                        Log.i(TAG, "graph_change : " + mListener!!.last_position.toString())
                        cal_lbl.text = mListener!!.display_label.get(mListener!!.current_position)
                        main_lbl.text = mListener!!.display_series.get(mListener!!.current_position) + "Kg"
                        section = 2
                    }
                }
            }
        }
        val xA = graph.xAxis
        xA.setValueFormatter(MainActivity.MyXAxisValueFormatter(mListener!!.display_label.toTypedArray()))
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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
        graph.setNoDataText("위 버튼을 클릭하시면 해당 기록이 이곳에 보여집니다.")
        graph.setNoDataTextColor(R.color.colorPrimaryDark)
        graph.setBackgroundColor(resources.getColor(R.color.whiteback))
        pre_Btn.setOnClickListener(object
            :View.OnClickListener{
            override fun onClick(v: View?) {
                Log.i("Button_pre_Btn", "onClick")
                if(mListener!!.current_position >0 && graph.barData.dataSetCount >0) {
                    mListener!!.current_position -= 1
                    cal_lbl.text = mListener!!.display_label.get(mListener!!.current_position)
                    when(section){
                        0-> {//bmi
                            main_lbl.text = mListener!!.display_series.get(mListener!!.current_position) + "Kg/" + "m\u00B2"
                            cal_lbl.text = mListener!!.display_label.get(mListener!!.current_position)
                        }
                        1->{//체중
                            main_lbl.text = mListener!!.display_series.get(mListener!!.current_position) + "Kg"
                            cal_lbl.text = mListener!!.display_label.get(mListener!!.current_position)
                        }
                        2->{//골격근
                            main_lbl.text = mListener!!.display_series.get(mListener!!.current_position) + "Kg"
                            cal_lbl.text = mListener!!.display_label.get(mListener!!.current_position)
                        }
                        3->{//체지방
                            main_lbl.text = mListener!!.display_series.get(mListener!!.current_position)+ "%"
                            cal_lbl.text = mListener!!.display_label.get(mListener!!.current_position)
                        }
                        4->{//소모칼로리
                            main_lbl.text = mListener!!.display_series.get(mListener!!.current_position) + "Kcal"
                            cal_lbl.text = mListener!!.display_label.get(mListener!!.current_position)
                        }
                        5->{//걸음수
                            main_lbl.text = mListener!!.display_series.get(mListener!!.current_position)+ "걸음"
                            cal_lbl.text = mListener!!.display_label.get(mListener!!.current_position)
                        }
                    }
                }else if(mListener!!.current_position == 0 && graph.barData.dataSetCount >0){
                    Toast.makeText(mListener!!.context, "첫번째 자료입니다.", Toast.LENGTH_SHORT).show()
                }
            }

        })
        next_Btn.setOnClickListener(object
            :View.OnClickListener{
            override fun onClick(v: View?) {
                Log.i("Button_next_Btn", "onClick")
                if(mListener!!.current_position < mListener!!.last_position&& graph.barData.dataSetCount >0) {
                    mListener!!.current_position += 1
                    cal_lbl.text = mListener!!.display_label.get(mListener!!.current_position)
                    when(section){
                        0-> {//bmi
                            main_lbl.text = mListener!!.display_series.get(mListener!!.current_position) + "Kg/" + "m\u00B2"
                            cal_lbl.text = mListener!!.display_label.get(mListener!!.current_position)
                        }
                        1->{//체중
                            main_lbl.text = mListener!!.display_series.get(mListener!!.current_position) + "Kg"
                            cal_lbl.text = mListener!!.display_label.get(mListener!!.current_position)
                        }
                        2->{//골격근
                            main_lbl.text = mListener!!.display_series.get(mListener!!.current_position) + "Kg"
                            cal_lbl.text = mListener!!.display_label.get(mListener!!.current_position)
                        }
                        3->{//체지방
                            main_lbl.text = mListener!!.display_series.get(mListener!!.current_position)+ "%"
                            cal_lbl.text = mListener!!.display_label.get(mListener!!.current_position)
                        }
                        4->{//소모칼로리
                            main_lbl.text = mListener!!.display_series.get(mListener!!.current_position) + "Kcal"
                            cal_lbl.text = mListener!!.display_label.get(mListener!!.current_position)
                        }
                        5->{//걸음수
                            main_lbl.text = mListener!!.display_series.get(mListener!!.current_position)+ "걸음"
                            cal_lbl.text = mListener!!.display_label.get(mListener!!.current_position)
                        }
                    }
                }else if(mListener!!.current_position == mListener!!.last_position && graph.barData.dataSetCount >0){
                    Toast.makeText(mListener!!.context, "가장 최근 자료입니다.", Toast.LENGTH_SHORT).show()
                }
            }

        })
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
