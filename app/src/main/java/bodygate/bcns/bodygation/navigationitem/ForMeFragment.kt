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
import bodygate.bcns.bodygation.support.CheckableImageButton
import bodygate.bcns.bodygation.R
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.android.gms.fitness.data.Bucket
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.result.DataReadResponse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_for_me.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
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
    fun graphSet(p:Int)=runBlocking {
        horizonValue.clear()
        value.clear()
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
                                    Log.i(TAG, "weight_series.isEmpty()_launch")
                                  launch (CommonPool){ printData(mListener!!.readResponse!!, p) }.join()
                            }
                            weight_series = series_dataSet()
                            weight_Label = label_dataSet()
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
                            printData(mListener!!.walkResponse!!, p)
                        }.join()
                    }
                    walk_series = series_dataSet()
                    walk_Label = label_dataSet()
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
                                printData(mListener!!.kcalResponse!!, p)
                            }.join()
                        }
                            kcal_series = series_dataSet()
                            kcal_Label = label_dataSet()
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
                                printData(mListener!!.fatResponse!!, p) }.join()
                        }
                            fat_series = series_dataSet()
                            fat_Label = label_dataSet()
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
                            printData(mListener!!.muscleResponse!!, p)
                        }.join()
                    }
                        muscle_series = series_dataSet()
                        muscle_Label = label_dataSet()
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
                                printData(mListener!!.bmiResponse!!, p)
                            }.join()
                        }

                        bmi_series = series_dataSet()
                        bmi_Label = label_dataSet()
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
    suspend fun printData(dataReadResult: DataReadResponse, i:Int) {
        launch(CommonPool) {
            Log.i(TAG, "printData")
            Log.i(TAG, "printData_" + dataReadResult.getBuckets().size.toString())
            Log.i(TAG, "printData_" + dataReadResult.getDataSets().size.toString())
            val label = SimpleDateFormat("MM/dd")
            var ia = 0
            if (dataReadResult.getBuckets().size > 0) {
                Log.i("printData", "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size)
                for (bucket: Bucket in dataReadResult.getBuckets()) {
                    Log.i("printData", "Bucket point:");
                    Log.i("printData", "bucket : " + bucket.toString())
                    Log.i("printData", "\tStart: " + label.format(bucket.getStartTime(TimeUnit.MILLISECONDS)))
                    Log.i("printData", "\tEnd: " + label.format(bucket.getEndTime(TimeUnit.MILLISECONDS)))
                    Log.i("printData", "\tdataSets: " + bucket.dataSets.toString())
                    printBucket(bucket, i)
                    ia += 1
                    Log.i("printData", "\tia : " + ia.toString())
                }
                Log.i("printData", "\thorizonValue : " + horizonValue.size.toString())
                Log.i("printData", "\tvalue : " + value.size.toString())
            } else if (dataReadResult.getDataSets().size > 0) {
                Log.i("printData", "Number of returned DataSets is: " + dataReadResult.getDataSets().size);
                for (dataSet: DataSet in dataReadResult.getDataSets()) {
                    dumpDataSet(dataSet)
                    ia += 1
                    Log.i("printData", "\tia : " + ia.toString())
                }
            }
        }.join()
    }

   fun printBucket(bucket:Bucket, i: Int) {
       Log.i(TAG, "printBucket")
        Log.i("printData", "printBucket")
        when(i){
            1 -> {//걷기
                Log.i(TAG, "printBucket_" + "set")
                for(dataset: DataSet in bucket.dataSets) {
                    for (dp: com.google.android.gms.fitness.data.DataPoint in dataset.dataPoints) {
                        Log.i(TAG, "printBucket_" + "dataPoints")
                        if (bucket.getEndTime(TimeUnit.MILLISECONDS) > 0) {
                            Log.i(TAG, "printBucket_" + "getEndTime")
                            //  if (dp.getValue(Field.FIELD_STEPS).asInt() > 10) {
                            for (field: Field in dp.getDataType().getFields()) {
                                Log.i(TAG, "printBucket_" + "getDataType")
                                value.add(dp.getValue(field).toString().toDouble())
                                horizonValue.add(Date(dp.getEndTime(TimeUnit.MILLISECONDS)))
                                Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
                            }
                            //  value.add(dp.getValue(Field.FIELD_STEPS).asFloat().toDouble())
                            Log.i(TAG, "걷기: " + Date(bucket.getEndTime(TimeUnit.MILLISECONDS)).toString() + dp.getValue(Field.FIELD_STEPS).asInt().toDouble().toString())
                            // }
                        }
                    }
                }
            }
            2 -> {//칼로리
                for(dataset: DataSet in bucket.dataSets) {
                    Log.i(TAG, "printBucket_" + "set")
                    for (dp: com.google.android.gms.fitness.data.DataPoint in dataset.dataPoints) {
                        Log.i(TAG, "printBucket_" + "dataPoints")
                        for (field: Field in dp.getDataType().getFields()) {
                            Log.i(TAG, "printBucket_" + "getDataType")
                            value.add(dp.getValue(field).toString().toDouble())
                            horizonValue.add(Date(dp.getEndTime(TimeUnit.MILLISECONDS)))
                            Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
                        }
                        //  value.add(dp.getValue(Field.FIELD_CALORIES).asFloat().toDouble())
                        Log.i(TAG, "칼로리: " + Date(bucket.getEndTime(TimeUnit.MILLISECONDS)).toString() + dp.getValue(Field.FIELD_STEPS).asInt().toDouble().toString())
                    }
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun dumpDataSet(dataSet:DataSet) {
        Log.i(TAG, "dumpDataSet")
        Log.i(TAG, dataSet.toString())
        var ia = 0
        for ( dp : com.google.android.gms.fitness.data.DataPoint in dataSet.getDataPoints()) {
            Log.i(TAG, "dumpDataSet_" + "dataSet")
            if(dp.getValue(dp.getDataType().fields.get(0)).toString().toDouble() > 0.5) {
                Log.i(TAG, "dumpDataSet_" + "fields")
                horizonValue.add(Date(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                for (field : Field in dp.getDataType().getFields()) {
                    Log.i(TAG, "dumpDataSet_" + "getDataType")
                    value.add(dp.getValue(field).toString().toDouble())
                    ia += 1
                    Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
                }
                Log.i(TAG, "dumpDataSet: " + Date(dp.getTimestamp(TimeUnit.MILLISECONDS)).toString() + dp.getValue(dp.getDataType().fields.get(0)).asInt().toDouble().toString())
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