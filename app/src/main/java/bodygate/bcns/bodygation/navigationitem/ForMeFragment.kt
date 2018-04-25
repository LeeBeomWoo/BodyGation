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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.tasks.OnSuccessListener
import com.jjoe64.graphview.helper.StaticLabelsFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.fragment_for_me.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import java.text.DateFormat
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
                if (muscle_Btn.isChecked)
                    muscle_Btn.setChecked(!check)
                if (bmi_Btn.isChecked)
                    bmi_Btn.setChecked(!check)
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
                if (muscle_Btn.isChecked)
                    muscle_Btn.setChecked(!check)
                if (bmi_Btn.isChecked)
                    bmi_Btn.setChecked(!check)
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
                if (muscle_Btn.isChecked)
                    muscle_Btn.setChecked(!check)
                if (bmi_Btn.isChecked)
                    bmi_Btn.setChecked(!check)
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
                if (muscle_Btn.isChecked)
                    muscle_Btn.setChecked(!check)
                if (bmi_Btn.isChecked)
                    bmi_Btn.setChecked(!check)
                if (bfp_Btn.isChecked){
                    async(UI) {graphSet(3)}
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
                    async(UI) {graphSet(5)}
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
                    async(UI) {graphSet(4)}
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
    var e_dAte = Array<String>(0){"it = \$it"} //체중
    var a_dAte = Array<String>(0){"it = \$it"} //kcal
    var t_dAte = Array<String>(0){"it = \$it"} // 걷기
    var o_dAte = Array<String>(0){"it = \$it"} // fat
    var m_dAte = Array<String>(0){"it = \$it"} //muscle
    var b_dAte = Array<String>(0){"it = \$it"} //bmr

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
        bmi_Btn.setOnCheckedChangeListener(this)
        muscle_Btn.setOnCheckedChangeListener(this)
    }
    fun graphSet(p:Int){
        graph.removeAllSeries()
        when(p){
            0->{//체중
                graph.title = getString(R.string.weight)
                mListener!!.weight_dateSET
                if(mListener!!.weight_dateSET == null){
                    Log.i(TAG, "체중자료 없음")
                }
                graph.addSeries(lineGraph(mListener!!.weight_dateSET))
                val labelhorizon = StaticLabelsFormatter(graph)
                labelhorizon.setHorizontalLabels(e_dAte)
                graph.getGridLabelRenderer().setLabelFormatter(labelhorizon)
                graph.gridLabelRenderer.setHumanRounding(false)
                graph.getGridLabelRenderer().setNumHorizontalLabels(3)
                graph.viewport.setScalable(true)
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
                graph.viewport.setScalable(true)
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
                graph.viewport.setScalable(true)
            }
            3->{//체지방비율
                if(mListener!!.bfp_dateSET == null){
                    Log.i(TAG, "체지방비율 없음")
                }
                graph.title = getString(R.string.bodyfat)
                graph.addSeries(customdataLine(mListener!!.bfp_dateSET, 1))
                val labelhorizon = StaticLabelsFormatter(graph)
                labelhorizon.setHorizontalLabels(t_dAte)
                graph.getGridLabelRenderer().setLabelFormatter(labelhorizon)
                graph.gridLabelRenderer.setHumanRounding(false)
                graph.getGridLabelRenderer().setNumHorizontalLabels(3)
                graph.viewport.setScalable(true)
            }
            4->{//골격근
                if(mListener!!.muscle_dateSET == null){
                    Log.i(TAG, "골격근 없음")
                }
                graph.title = getString(R.string.musclemass)
                graph.addSeries(customdataLine(mListener!!.muscle_dateSET, 0))
                val labelhorizon = StaticLabelsFormatter(graph)
                labelhorizon.setHorizontalLabels(m_dAte)
                graph.getGridLabelRenderer().setLabelFormatter(labelhorizon)
                graph.gridLabelRenderer.setHumanRounding(false)
                graph.getGridLabelRenderer().setNumHorizontalLabels(3)
                graph.viewport.setScalable(true)
            }
            5->{//BMI
                if(mListener!!.bmi_dateSET == null){
                    Log.i(TAG, "BMI 없음")
                }
                graph.title = getString(R.string.bmi)
                graph.addSeries(customdataLine(mListener!!.bmi_dateSET, 2))
                val labelhorizon = StaticLabelsFormatter(graph)
                labelhorizon.setHorizontalLabels(b_dAte)
                graph.getGridLabelRenderer().setLabelFormatter(labelhorizon)
                graph.gridLabelRenderer.setHumanRounding(false)
                graph.getGridLabelRenderer().setNumHorizontalLabels(3)
                graph.viewport.setScalable(true)
            }
        }
        graph.onDataChanged(true, true)
    }
    fun lineSub(dataSet: DataSet?, i:Int):DataType{
        var dt:DataType? = null
        when(i){
            0->{//mouscle
                val pendingResult = Fitness.getConfigClient(mListener!!.context, GoogleSignIn.getLastSignedInAccount(mListener!!.context)!!).readDataType("bodygate.bcns.bodygation.muscle")
                pendingResult.addOnSuccessListener(object : OnSuccessListener<DataType> {
                    override fun onSuccess(p0: DataType?) {
                        dt = p0
                    }
                })
            }
            1->{//fat

            val pendingResult = Fitness.getConfigClient(mListener!!.context, GoogleSignIn.getLastSignedInAccount(mListener!!.context)!!).readDataType("bodygate.bcns.bodygation.fat")
            pendingResult.addOnSuccessListener(object : OnSuccessListener<DataType> {
                override fun onSuccess(p0: DataType?) {
                    dt = p0
                }
            })
            }
            2->{//bmi

            val pendingResult = Fitness.getConfigClient(mListener!!.context, GoogleSignIn.getLastSignedInAccount(mListener!!.context)!!).readDataType("bodygate.bcns.bodygation.bmi")
            pendingResult.addOnSuccessListener(object : OnSuccessListener<DataType> {
                override fun onSuccess(p0: DataType?) {
                    dt = p0
                }
            })
            }
        }
        return dt!!
    }
    @SuppressLint("SimpleDateFormat")
    fun customdataLine(dataSet:DataSet?, i:Int):LineGraphSeries<DataPoint>?{
        if (dataSet != null) {
            showDataSet(dataSet)
            Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName())
            val label = SimpleDateFormat("MM/dd")
            val siz = dataSet.dataPoints.size
            val mvalue = DoubleArray(siz)
            val bvalue = DoubleArray(siz)
            val fvalue = DoubleArray(siz)
            Log.i(TAG, "Data size:" + dataSet.dataPoints.size.toString())
            Log.i(TAG, "Data set:" + dataSet.toString())
            m_dAte = Array<String>(siz){"it = \$it"}
            val mlist = ArrayList<DataPoint>(siz)
            val blist = ArrayList<DataPoint>(siz)
            val flist = ArrayList<DataPoint>(siz)
            var ia = 0
            for (dp: com.google.android.gms.fitness.data.DataPoint in dataSet.dataPoints) {
                Log.i(TAG, "Data point:" + dp.toString() + "\nType: " + dp.getDataType().getName() + "\nStart: " + label.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + "\nEnd: " + label.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + "\nTimeStemp: " + label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)) + "\ntype: " + dp.getTimestamp(TimeUnit.MILLISECONDS).javaClass)
                when(i){
                    0->{//mouscle
                        m_dAte.set(ia, label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                        mvalue.set(ia, dp.getValue(lineSub(dataSet, i).fields.get(0)).toString().toDouble())
                    }
                    1->{//fat
                        Log.i(TAG, " Value: " + dp.getValue(lineSub(dataSet, i).fields.get(0)) + "type: " + dp.getValue(lineSub(dataSet, i).fields.get(0)).javaClass)
                        o_dAte.set(ia, label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                        bvalue.set(ia, dp.getValue(lineSub(dataSet, i).fields.get(0)).toString().toDouble())
                    }
                    2->{//bmi
                        Log.i(TAG, " Value: " + dp.getValue(lineSub(dataSet, i).fields.get(0)) + "type: " + dp.getValue(lineSub(dataSet, i).fields.get(0)).javaClass)
                        b_dAte.set(ia, label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                        fvalue.set(ia, dp.getValue(lineSub(dataSet, i).fields.get(0)).toString().toDouble())
                    }
                }
                ia += 1
            }
            var e = 0
            var a = 0
            var t = 0
            when(i){
                0->{//mouscle
                    if(m_dAte.size > 1) {
                        m_dAte.forEach {
                            Log.i(TAG, "m_dAte value:" + m_dAte[e])
                            Log.i(TAG, "m_dAte point:" + m_dAte[e])
                            Log.i(TAG, "m_dAte value point:" + mvalue[e].toString())
                            Log.i(TAG, "m point:" + e.toString())
                            mlist.add(DataPoint(e.toDouble(), mvalue[e]))
                            e +=1
                        }
                    }
                }
                1->{//fat
                    if(o_dAte.size > 1) {
                        o_dAte.forEach {
                            Log.i(TAG, "o_dAte value:" + o_dAte[a])
                            Log.i(TAG, "o_dAte point:" + o_dAte[a])
                            Log.i(TAG, "o_dAte value point:" + fvalue[a].toString())
                            Log.i(TAG, "o point:" + a.toString())
                            flist.add(DataPoint(a.toDouble(), fvalue[a]))
                            a +=1
                        }
                    }
                }
                2->{//bmi
                    if(b_dAte.size > 1) {
                        b_dAte.forEach {
                            Log.i(TAG, "b_dAte value:" + b_dAte[t])
                            Log.i(TAG, "b_dAte point:" + b_dAte[t])
                            Log.i(TAG, "b_dAte value point:" + bvalue[t].toString())
                            Log.i(TAG, "b point:" + t.toString())
                            blist.add(DataPoint(t.toDouble(), bvalue[t]))
                            t +=1
                        }
                    }
                }
            }
            when(i){
                0->{//muscle
                    val series = LineGraphSeries<DataPoint>(mlist.toTypedArray())
                    series.setColor(Color.BLUE)
                    return series
                }
                1->{//fat
                    val series = LineGraphSeries<DataPoint>(flist.toTypedArray())
                    series.setColor(Color.YELLOW)
                    return series
                }
                2->{//bmi
                    val series = LineGraphSeries<DataPoint>(blist.toTypedArray())
                    series.setColor(Color.MAGENTA)
                    return series
                }
            }
        }
        return null
    }
    @SuppressLint("SimpleDateFormat")
    fun lineGraph(dataSet:DataSet?):LineGraphSeries<DataPoint>?{
        if (dataSet != null) {
            showDataSet(dataSet)
            Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName())
            val label = SimpleDateFormat("yy/MM/dd")
            val siz = dataSet.dataPoints.size
            val evalue = DoubleArray(siz)
            val avalue = DoubleArray(siz)
            val tvalue = DoubleArray(siz)
            Log.i(TAG, "Data size:" + dataSet.dataPoints.lastIndex.toString())
            Log.i(TAG, "Data set:" + dataSet.toString())
                e_dAte = Array<String>(siz){"it = \$it"}
            val elist = ArrayList<DataPoint>(siz)
            val alist = ArrayList<DataPoint>(siz)
            val tlist = ArrayList<DataPoint>(siz)
            var e = 0
            var a = 0
            var t = 0
            for (dp: com.google.android.gms.fitness.data.DataPoint in dataSet.dataPoints) {
                Log.i(TAG, "Data point:" + dp.toString() + "\nType: " + dp.getDataType().getName() + "\nStart: " + label.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + "\nEnd: " + label.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + "\nTimeStemp: " + label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)) + "\ntype: " + dp.getTimestamp(TimeUnit.MILLISECONDS).javaClass)
                when(dataSet.dataType){
                    DataType.TYPE_WEIGHT->{
                        Log.i(TAG, " Value: " + dp.getValue(Field.FIELD_WEIGHT) + "  \ntype: " + dp.getValue(Field.FIELD_WEIGHT).javaClass + "  \ndate : " + label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                        e_dAte.set(e, label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                        evalue.set(e, dp.getValue(Field.FIELD_WEIGHT).toString().toDouble())
                        e +=1
                    }
                    DataType.TYPE_CALORIES_EXPENDED->{
                        Log.i(TAG, " Value: " + dp.getValue(Field.FIELD_CALORIES) + "  \ntype: " + dp.getValue(Field.FIELD_CALORIES).javaClass + "  \ndate : " + label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                        a_dAte.set(a, label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                        avalue.set(a, dp.getValue(Field.FIELD_STEPS).toString().toDouble())
                        a +=1
                    }
                    DataType.TYPE_STEP_COUNT_DELTA->{
                        Log.i(TAG, " Value: " + dp.getValue(Field.FIELD_STEPS) + "  \ntype: " + dp.getValue(Field.FIELD_STEPS).javaClass + "  \ndate : " + label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                        t_dAte.set(t, label.format(dp.getTimestamp(TimeUnit.MILLISECONDS)))
                        tvalue.set(t, dp.getValue(Field.FIELD_STEPS).toString().toDouble())
                        t +=1
                    }
                }
            }
            e = 0
            a = 0
            t = 0
            when(dataSet.dataType){
                DataType.TYPE_WEIGHT->{
                    if(e_dAte.size > 1) {
                        e_dAte.forEach {
                            Log.i(TAG, "e_dAte value:" + e_dAte[e])
                            Log.i(TAG, "e_dAte point:" + e_dAte[e])
                            Log.i(TAG, "e_dAte value point:" + evalue[e].toString())
                            Log.i(TAG, "e point:" + e.toString())
                            elist.add(DataPoint(e.toDouble(), evalue[e]))
                            e +=1
                        }
                    }
                }
                DataType.TYPE_CALORIES_EXPENDED->{
                    if(a_dAte.size > 1) {
                        a_dAte.forEach {
                            Log.i(TAG, "a_dAte value:" + a_dAte[a])
                            Log.i(TAG, "a_dAte point:" + a_dAte[a])
                            Log.i(TAG, "a_dAte value point:" + avalue[a].toString())
                            Log.i(TAG, "a point:" + a.toString())
                            alist.add(DataPoint(a.toDouble(), avalue[a]))
                            a +=1
                        }
                    }
                }
                DataType.TYPE_STEP_COUNT_DELTA->{
                    if(t_dAte.size > 1) {
                        t_dAte.forEach {
                            Log.i(TAG, "t_dAte value:" + t_dAte[t])
                            Log.i(TAG, "t_dAte point:" + t_dAte[t])
                            Log.i(TAG, "t_dAte value point:" + tvalue[t].toString())
                            Log.i(TAG, "t point:" + t.toString())
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
                DataType.TYPE_CALORIES_EXPENDED->{
                    val series = LineGraphSeries<DataPoint>(alist.toTypedArray())
                    series.setColor(Color.RED)
                    return series
                }
                DataType.TYPE_STEP_COUNT_DELTA->{
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
private fun showDataSet(dataSet:DataSet) {
    Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
    val dateFormat = DateFormat.getDateInstance();
    val timeFormat = DateFormat.getTimeInstance();

    for (dp :com.google.android.gms.fitness.data.DataPoint in dataSet.dataPoints) {
        Log.e("History", "Data point:");
        Log.e("History", "tType: " + dp.getDataType().getName());
        Log.e("History", "tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
        Log.e("History", "tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
        for(field : Field in dp.getDataType().getFields()) {
            Log.e("History", "tField: " + field.getName() +
                    " Value: " + dp.getValue(field))
        }
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
        val context:Context
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
