package bodygate.bcns.bodygation.navigationitem

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import bodygate.bcns.bodygation.R
import bodygate.bcns.bodygation.support.CheckableImageButton
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.android.gms.fitness.data.Bucket
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.result.DataReadResponse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_for_me.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
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
    val value:MutableList<Double> =  ArrayList()
    val horizonValue:MutableList<Date> =  ArrayList()
    var weight_Label:MutableList<String> =  ArrayList()
    var kcal_Label:MutableList<String> =  ArrayList()
    var walk_Label:MutableList<String> =  ArrayList()
    var fat_Label:MutableList<String> =  ArrayList()
    var muscle_Label:MutableList<String> =  ArrayList()
    var bmi_Label:MutableList<String> =  ArrayList()
    val TAG = "ForMeFragment_"
    var last_position = 0
    var current_position = 0
    var section = 0
    var weight_series: MutableList<BarEntry> = ArrayList()
    var muscle_series: MutableList<BarEntry> = ArrayList()
    var walk_series: MutableList<BarEntry> = ArrayList()
    var fat_series: MutableList<BarEntry> = ArrayList()
    var bmi_series: MutableList<BarEntry> = ArrayList()
    var kcal_series: MutableList<BarEntry> = ArrayList()
    var display_label:MutableList<String> =  ArrayList()
    var display_series: MutableList<String> = ArrayList()

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
                    graphSet(0)
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
                    graphSet(1)
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
                 graphSet(2)
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
                    graphSet(3)
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
                    graphSet(5)
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
                    graphSet(4)
                }
            }
        }
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
        l.setYOffset(0f)
        l.setXOffset(10f)
        l.setYEntrySpace(0f)
        l.setTextSize(8f)
        val rAxis = graph.getAxisRight()
        rAxis.setEnabled(false)
        val xAxis = graph.getXAxis()
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        if(mParam1 !=  null){
            Picasso.get().load(Uri.parse(mParam1))
                    .placeholder(R.mipmap.toolbarlogo_round)
                    .error(R.mipmap.toolbarlogo_round).into(profile_Image)
        }else{
            Picasso.get().load(R.mipmap.toolbarlogo_round).into(profile_Image)
        }
        graph.setVisibleXRangeMaximum(5.toFloat())
        graph.setNoDataText("위 버튼을 클릭하시면 해당 기록이 이곳에 보여집니다.")
        graph.setNoDataTextColor(R.color.colorPrimaryDark)
        graph.setDrawOrder(arrayOf(CombinedChart.DrawOrder.BAR))
        graph.setBackgroundColor(resources.getColor(R.color.whiteback))
        pre_Btn.setOnClickListener(object
            :View.OnClickListener{
            override fun onClick(v: View?) {
                Log.i("Button_pre_Btn", current_position.toString() + "\t" +  last_position.toString() + "\t" + graph.combinedData.dataSetCount.toString())
                if(current_position >0 && current_position < (last_position +1)&& graph.combinedData.dataSetCount >0) {
                    current_position -= 1
                    cal_lbl.text = display_label.get(current_position)
                    when(section){
                        0->{//bmi
                            main_lbl.text = display_series.get(current_position) + "Kg/" + "m\u00B2"}
                        1->{//체중
                            main_lbl.text = display_series.get(current_position) + "Kg"}
                        2->{//골격근
                            main_lbl.text = display_series.get(current_position) + "Kg"}
                        3->{//체지방
                            main_lbl.text = display_series.get(current_position)+ "%"}
                        4->{//소모칼로리
                            main_lbl.text = display_series.get(current_position) + "Kcal"}
                        5->{//걸음수
                            main_lbl.text = display_series.get(current_position)+ "걸음"}
                    }
                }
            }

        })
        next_Btn.setOnClickListener(object
            :View.OnClickListener{
            override fun onClick(v: View?) {
                Log.i("Button_next_Btn", "onClick")
                if(current_position >0 && current_position < last_position&& graph.combinedData.dataSetCount >0) {
                    Log.i("Button_next_Btn", current_position.toString() + "\t" +  last_position.toString() + "\t" + graph.combinedData.dataSetCount.toString())
                    current_position += 1
                    cal_lbl.text = display_label.get(current_position)
                    when(section){
                        0->{//bmi
                            main_lbl.text = display_series.get(current_position) + "Kg/" + "m\u00B2"}
                        1->{//체중
                            main_lbl.text = display_series.get(current_position) + "Kg"}
                        2->{//골격근
                            main_lbl.text = display_series.get(current_position) + "Kg"}
                        3->{//체지방
                            main_lbl.text = display_series.get(current_position)+ "%"}
                        4->{//소모칼로리
                            main_lbl.text = display_series.get(current_position) + "Kcal"}
                        5->{//걸음수
                            main_lbl.text = display_series.get(current_position)+ "걸음"}
                    }
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


    @SuppressLint("SetTextI18n")
    fun graphSet(p:Int)= launch(UI) {
        when(p){
            0->{//체중
                section = 1
                        if(mListener!!.readResponse == null){
                            Log.i(TAG, "체중 없음")
                            Toast.makeText(mListener!!.context, "구글핏과 계정을 연동 하신 후 구글핏에 해당 자료가 업로드 되도록 해주세요", Toast.LENGTH_SHORT).show()
                        }else {
                            Log.i(TAG, "체중 있음")
                            if (graph.getData() != null &&
                                    graph.getData().getDataSetCount() > 0) {
                                graph.data.clearValues()
                            }
                            if (weight_series.isEmpty()) {
                                Log.i(TAG, "weight_series.isEmpty()")
                                launch(CommonPool) {
                                      Log.i(TAG, "weight_series.isEmpty()_launch")
                                      printData(mListener!!.readResponse!!)}.join()
                                Log.i(TAG, "weight_series.isEmpty()_launch_end")
                                launch(CommonPool) {
                                    weight_series = series_dataSet()
                                    weight_Label = label_dataSet() }.join()
                            }else{
                                last_position = weight_series.size-1
                            }
                            val set1 = BarDataSet(weight_series, getString(R.string.weight))
                            set1.setColors(Color.rgb(65, 192, 193))
                            val barData = BarData(set1)
                            val xAxis = graph.xAxis
                            xAxis.setGranularity(1f)
                            xAxis.setValueFormatter(MyXAxisValueFormatter(weight_Label.toTypedArray()))
                            val data = CombinedData()
                            data.setData(barData)
                            graph.setData(data)
                            graph.data.notifyDataChanged()
                            graph.notifyDataSetChanged()
                        }
            }
            1->{//걷기
                section = 5
                if(mListener!!.walkResponse == null){
                    Log.i(TAG, "걷기 없음")
                    Toast.makeText(mListener!!.context, "구글핏과 계정을 연동 하신 후 구글핏에 해당 자료가 업로드 되도록 해주세요", Toast.LENGTH_SHORT).show()
                }else {
                    Log.i(TAG, "걷기 있음")
                    if (graph.getData() != null &&
                            graph.getData().getDataSetCount() > 0) {
                        graph.data.clearValues()
                    }
                    if (walk_series.isEmpty()) {
                        Log.i(TAG, "walk_series.isEmpty()")
                        launch(CommonPool) {
                            Log.i(TAG, "walk_series.isEmpty()_launch")
                            printData(mListener!!.walkResponse!!)
                        }.join()
                        Log.i(TAG, "walk_series.isEmpty()_launch_end")
                        launch(CommonPool) {
                            walk_series = series_dataSet()
                            walk_Label = label_dataSet() }.join()
                    }else{
                        last_position = walk_series.size-1
                    }
                    val set1 = BarDataSet(walk_series, getString(R.string.walk))
                    set1.setColors(Color.rgb(65, 192, 193))
                    val barData = BarData(set1)
                    val xAxis = graph.xAxis
                    xAxis.setGranularity(1f)
                    xAxis.setValueFormatter(MyXAxisValueFormatter(walk_Label.toTypedArray()))
                    val data = CombinedData()
                    data.setData(barData)
                    graph.setData(data)
                    graph.data.notifyDataChanged()
                    graph.notifyDataSetChanged()
                }
            }
            2-> {//칼로리
                section = 4
                if (mListener!!.kcalResponse == null) {
                    Log.i(TAG, "칼로리 없음")
                    Toast.makeText(mListener!!.context, "구글핏과 계정을 연동 하신 후 구글핏에 해당 자료가 업로드 되도록 해주세요", Toast.LENGTH_SHORT).show()
                } else {
                    Log.i(TAG, "칼로리 있음")
                    if (graph.data != null &&
                            graph.data.getDataSetCount() > 0) {
                        graph.data.clearValues()
                    }
                        if(kcal_series.isEmpty()) {
                            Log.i(TAG, "kcalResponse.isEmpty()")
                            launch(CommonPool) {
                                Log.i(TAG, "kcalResponse.isEmpty()_launch")
                                printData(mListener!!.kcalResponse!!)
                            }.join()
                            Log.i(TAG, "kcalResponse.isEmpty()_launch_end")
                            launch(CommonPool) {
                                kcal_series = series_dataSet()
                                kcal_Label = label_dataSet() }.join()
                        }else{
                            last_position = kcal_series.size-1
                        }
                    val set1 = BarDataSet(kcal_series, getString(R.string.calore))
                    set1.setColors(Color.rgb(65, 192, 193))
                    val barData = BarData(set1)
                    val xAxis = graph.xAxis
                    xAxis.setGranularity(1f)
                    xAxis.setValueFormatter(MyXAxisValueFormatter(kcal_Label.toTypedArray()))
                    val data = CombinedData()
                    data.setData(barData)
                    graph.setData(data)
                        graph.data.notifyDataChanged()
                        graph.notifyDataSetChanged()
                }
            }
            3->{//체지방비율
                section = 3
                if(mListener!!.fatResponse == null){
                    Log.i(TAG, "체지방비율 없음")
                    Toast.makeText(mListener!!.context, "우리 앱에서 아직 해당 자료를 등록하지 않으셨습니다.", Toast.LENGTH_SHORT).show()
                }else {
                    Log.i(TAG, "체지방비율 있음")
                    if (graph.getData() != null &&
                            graph.getData().getDataSetCount() > 0) {
                        graph.data.clearValues()
                    }
                        if (fat_series.isEmpty()) {
                            Log.i(TAG, "fatResponse.isEmpty()")
                            launch(CommonPool) {
                                Log.i(TAG, "fatResponse.isEmpty()_launch")
                                printData(mListener!!.fatResponse!!) }.join()
                            Log.i(TAG, "fatResponse.isEmpty()_launch_end")
                            launch(CommonPool) {
                                fat_series = series_dataSet()
                                fat_Label = label_dataSet() }.join()
                        }else{
                            last_position = fat_series.size-1
                        }
                    val set1 = BarDataSet(fat_series, getString(R.string.bodyfat))
                    set1.setColors(Color.rgb(65, 192, 193))
                    val barData = BarData(set1)
                    val xAxis = graph.xAxis
                    xAxis.setGranularity(1f)
                    xAxis.setValueFormatter(MyXAxisValueFormatter(fat_Label.toTypedArray()))
                    val data = CombinedData()
                    data.setData(barData)
                    graph.setData(data)
                        graph.data.notifyDataChanged()
                        graph.notifyDataSetChanged()
                }
            }
            4->{//골격근
                section = 2
                if(mListener!!.muscleResponse == null){
                    Log.i(TAG, "골격근 없음")
                    Toast.makeText(mListener!!.context, "우리 앱에서 아직 해당 자료를 등록하지 않으셨습니다.", Toast.LENGTH_SHORT).show()
                }else {
                    Log.i(TAG, "골격근 있음")
                    if (graph.getData() != null &&
                            graph.getData().getDataSetCount() > 0) {
                        graph.data.clearValues()
                    }
                    if(muscle_series.isEmpty()) {
                        launch {
                            printData(mListener!!.muscleResponse!!)
                        }.join()
                        Log.i(TAG, "muscle_series.isEmpty()_launch_end")
                        launch(CommonPool) {
                            muscle_series = series_dataSet()
                            muscle_Label = label_dataSet() }.join()
                    }else{
                        last_position = muscle_series.size-1
                    }
                    val set1 = BarDataSet(muscle_series, getString(R.string.musclemass))
                    set1.setColors(Color.rgb(65, 192, 193))
                    val barData = BarData(set1)
                    val xAxis = graph.xAxis
                    xAxis.setGranularity(1f)
                    xAxis.setValueFormatter(MyXAxisValueFormatter(muscle_Label.toTypedArray()))
                    val data = CombinedData()
                    data.setData(barData)
                    graph.setData(data)
                        graph.data.notifyDataChanged()
                        graph.notifyDataSetChanged()
                }
            }
            5->{//BMI
                section = 0
                if(mListener!!.bmiResponse == null){
                    Log.i(TAG, "BMI 없음")
                    Toast.makeText(mListener!!.context, "우리 앱에서 아직 해당 자료를 등록하지 않으셨습니다.", Toast.LENGTH_SHORT).show()
                }else {
                    Log.i(TAG, "BMI 있음")
                    if (graph.getData() != null &&
                            graph.getData().getDataSetCount() > 0) {
                        graph.data.clearValues()
                    }
                        if(bmi_series.isEmpty()) {
                            launch {
                                printData(mListener!!.bmiResponse!!)
                            }.join()
                            launch(CommonPool) {
                                bmi_series = series_dataSet()
                                bmi_Label = label_dataSet() }.join()
                        }else{
                            last_position = bmi_series.size-1
                        }
                    val set1 = BarDataSet(bmi_series, getString(R.string.bmi))
                    set1.setColors(Color.rgb(65, 192, 193))
                    val barData = BarData(set1)
                    val xAxis = graph.xAxis
                    xAxis.setGranularity(1f)
                    xAxis.setValueFormatter(MyXAxisValueFormatter(bmi_Label.toTypedArray()))
                    val data = CombinedData()
                    data.setData(barData)
                    graph.setData(data)
                        graph.data.notifyDataChanged()
                        graph.notifyDataSetChanged()
                }
            }
        }
                Log.i(TAG, "graph_change")
                if(last_position>0) {
            graph.invalidate()
            current_position = last_position
                    Log.i(TAG, "graph_change : " + last_position.toString())
            cal_lbl.text = display_label.get(current_position)
            when (section) {
                0 -> {//bmi
                    main_lbl.text = display_series.get(current_position) + "Kg/" + "m\u00B2"
                }
                1 -> {//체중
                    main_lbl.text = display_series.get(current_position) + "Kg"
                }
                2 -> {//골격근
                    main_lbl.text = display_series.get(current_position) + "Kg"
                }
                3 -> {//체지방
                    main_lbl.text = display_series.get(current_position) + "%"
                }
                4 -> {//소모칼로리
                    main_lbl.text = display_series.get(current_position) + "Kcal"
                }
                5 -> {//걸음수
                    main_lbl.text = display_series.get(current_position) + "걸음"
                }
            }
            }
    }


   fun series_dataSet():MutableList<BarEntry>{
       Log.i(TAG, "series_dataSet")
        val series: MutableList<BarEntry> = ArrayList()
        val x = value.size-1
       display_series.clear()
       Log.i(TAG, "display_series: " + display_series.size.toString())
       Log.i(TAG, "value: " + value.size.toString())
        for (a: Int in 0..x) {
            //series.add(BarEntry(horizonValue[a].time.toFloat(), value.get(a).toFloat()))
            series.add(a, BarEntry(a.toFloat(), value.get(a).toFloat()))
            display_series.add("%.2f".format(value.get(a)))
        }
       Log.i(TAG, "display_series: " + display_series.size.toString())
        return series
    }

    @SuppressLint("SimpleDateFormat")
    fun label_dataSet():MutableList<String> {
        Log.i(TAG, "label_dataSet")
        val label = SimpleDateFormat("MM/dd")
        val series: MutableList<String> = ArrayList()
        last_position = horizonValue.size - 1
        display_label.clear()
        Log.i(TAG, "display_label: " + display_label.size.toString())
        Log.i(TAG, "horizonValue: " + horizonValue.size.toString())
        for (a: Int in 0..last_position) {
            series.add(a, label.format(horizonValue[a]))
            display_label.add(label.format(horizonValue[a]))
        }
        Log.i(TAG, "display_label: " + display_label.size.toString())
        return series
    }

    @SuppressLint("SimpleDateFormat")
    suspend fun printData(dataReadResult: DataReadResponse) {
        launch(CommonPool) {
            Log.i(TAG, "printData")
            Log.i(TAG, "printData_" + dataReadResult.getBuckets().size.toString())
            Log.i(TAG, "printData_" + dataReadResult.getDataSets().size.toString())
            val label = SimpleDateFormat("MM/dd")
            var ia = 0
            if (dataReadResult.getBuckets().size > 0) {
                Log.i(TAG, "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size)
                for (bucket: com.google.android.gms.fitness.data.Bucket in dataReadResult.getBuckets()) {
                    for(dataset: com.google.android.gms.fitness.data.DataSet in bucket.dataSets) {
                        Log.i(TAG, "dumpDataSet")
                        Log.i(TAG, dataset.toString())
                    Log.i(TAG, "Bucket point:");
                    Log.i(TAG, "bucket : " + bucket.toString())
                    Log.i(TAG, "\tStart: " + label.format(bucket.getStartTime(TimeUnit.MILLISECONDS)))
                    Log.i(TAG, "\tEnd: " + label.format(bucket.getEndTime(TimeUnit.MILLISECONDS)))
                    Log.i(TAG, "\tdataSets: " + bucket.dataSets.toString())
                        dumpDataSet(dataset)
                    }
                    Log.i(TAG, "\tia : " + ia.toString())
                }
                Log.i(TAG, "\thorizonValue : " + horizonValue.size.toString())
                Log.i(TAG, "\tvalue : " + value.size.toString())
            } else if (dataReadResult.getDataSets().size > 0) {
                Log.i(TAG, "Number of returned DataSets is: " + dataReadResult.getDataSets().size);
                for (dataSet: com.google.android.gms.fitness.data.DataSet in dataReadResult.getDataSets()) {
                    dumpDataSet(dataSet)
                    ia += 1
                    Log.i(TAG, "\tia : " + ia.toString())
                }
            }
        }.join()
    }

    @SuppressLint("SimpleDateFormat")
    fun dumpDataSet(dataSet:DataSet)= launch {
             val label = SimpleDateFormat("MM/dd")
             var ia = 0
        horizonValue.clear()
        value.clear()
            for ( dp : com.google.android.gms.fitness.data.DataPoint in dataSet.dataPoints)
            {
                Log.i(TAG, "Data point:")
                Log.i(TAG, "\tType: " + dp.dataType.name)
                Log.i(TAG, "\tStart: " + label.format(dp.getStartTime(TimeUnit.MILLISECONDS)))
                Log.i(TAG, "\tEnd: " + label.format(dp.getEndTime(TimeUnit.MILLISECONDS)))
                for (field:com.google.android.gms.fitness.data.Field in dp.dataType.fields)
                {
                    Log.i(TAG, "\tField: " + field.name + " Value: " + dp.getValue(field))
                    value.add(dp.getValue(field).toString().toDouble())
                    if(dp.getValue(field).toString().toDouble() > 0.0){
                        horizonValue.add(Date(dp.getEndTime(TimeUnit.MILLISECONDS)))
                    }
                    ia += 1
                }
            }
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

class MyXAxisValueFormatter(private val mValues: Array<String>) : IAxisValueFormatter {

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        // "value" represents the position of the label on the axis (x or y)
        if(value.toInt()<mValues.size){
            return mValues[value.toInt()]
        }else{
            return ""
        }
    }
}