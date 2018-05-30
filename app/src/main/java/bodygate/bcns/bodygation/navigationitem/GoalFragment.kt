package bodygate.bcns.bodygation.navigationitem

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import bodygate.bcns.bodygation.R
import bodygate.bcns.bodygation.R.id.*
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.fitness.Fitness.ConfigApi
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataTypeCreateRequest
import com.google.android.gms.fitness.result.DataSourcesResult
import com.google.android.gms.fitness.result.DataTypeResult
import kotlinx.android.synthetic.main.fragment_goal.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlin.math.min
import java.lang.reflect.Array.setInt
import java.lang.reflect.Array.setFloat




/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DashboardFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GoalFragment : Fragment(), CompoundButton.OnCheckedChangeListener {
    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        Log.i(TAG, "onClick")
        when(buttonView!!.id){
            R.id.man_RBtn->{
                Log.i(TAG, "onClick man_RBtn")
                man_RBtn.setChecked(isChecked)
                if(isChecked) {
                    girl_RBtn.setChecked(false)
                }
            }
            R.id.girl_RBtn->{
                Log.i(TAG, "onClick girl_RBtn")
                girl_RBtn.setChecked(isChecked)
                if(isChecked){
                    man_RBtn.setChecked(false)
                }
            }
        }
    }

    val TAG = "GoalFragment"
    val muslceD = 0.45

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnGoalInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
        Log.i(TAG, "onCreate")
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i(TAG, "onCreateView")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_goal, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnGoalInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.i(TAG, "onActivityCreated")
        man_RBtn.setOnCheckedChangeListener(this)
        girl_RBtn.setOnCheckedChangeListener(this)
        upload_Btn.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                Log.i("pendingResult" , "upload_Btn.setOnClickListener")
                if(my_bodyfat_txtB.text.isNotEmpty() && my_musclemass_txtB.text.isNotEmpty()){
                   mListener!!.OnGoalInteractionListener()
                }else if(goal_height_txtB.text.isEmpty()) {
                    Toast.makeText(this@GoalFragment.context, "신장을 입력하여 주세요", Toast.LENGTH_SHORT).show()
                    goal_height_txtB.setFocusable(true)
                }else if(my_weight_txtB.text.isEmpty()) {
                    Toast.makeText(this@GoalFragment.context, "체중을 입력하여 주세요", Toast.LENGTH_SHORT).show()
                    my_weight_txtB.setFocusable(true)
                }else if(my_bodyfat_txtB.text.isEmpty()){
                    Toast.makeText(this@GoalFragment.context, "체지방률을 측정하여 작성하여 주세요", Toast.LENGTH_SHORT).show()
                    my_bodyfat_txtB.setFocusable(true)
                }else if(my_musclemass_txtB.text.isEmpty()){
                    Toast.makeText(this@GoalFragment.context, "골격근량을 측정하여 작성하여 주세요", Toast.LENGTH_SHORT).show()
                    my_musclemass_txtB.setFocusable(true)
                }
            }

        })
        goal_height_txtB.setOnFocusChangeListener(object :View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (hasFocus) {
                    if (!man_RBtn.isChecked && !girl_RBtn.isChecked) {
                        Toast.makeText(this@GoalFragment.context, "성별을 먼저 선택하여 주세요", Toast.LENGTH_SHORT).show()
                        goal_height_txtB.setFocusable(false)
                        goal_height_txtB.text.clear()
                    }
                }
            }
        })
        my_weight_txtB.setOnFocusChangeListener(object :View.OnFocusChangeListener{
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if(hasFocus){
                    if (!man_RBtn.isChecked && !girl_RBtn.isChecked) {
                        Toast.makeText(this@GoalFragment.context, "성별을 먼저 선택하여 주세요", Toast.LENGTH_SHORT).show()
                        my_weight_txtB.setFocusable(false)
                        my_weight_txtB.text.clear()
                    }else{
                        if (goal_height_txtB.text.isNotEmpty()) {
                        goal_weight_txtB.setText((Math.round(weightCal(goal_height_txtB.text.toString().toDouble() * 0.01) * 100)*0.01).toString())
                        goal_bmi_txtB.setText((Math.round(BMICal(goal_height_txtB.text.toString().toDouble() * 0.01, goal_weight_txtB.text.toString().toDouble()) * 100)*0.01).toString())
                        //goal_bodyfat_txtB.setText(bodyfatCal(goal_height_txtB.text.toString().toDouble(), goal_weight_txtB.text.toString().toDouble()).toString())
                        if(man_RBtn.isChecked){
                            goal_bodyfat_txtB.setText("13%~23%")
                        }else if(girl_RBtn.isChecked){
                            goal_bodyfat_txtB.setText("18%~27%")
                        }
                        goal_musclemass_txtB.setText((Math.round(muscleCal(goal_weight_txtB.text.toString().toDouble()) * 100)*0.01).toString())
                    }
                    }
                }
            }
        })
        my_bmi_txtB.setOnFocusChangeListener(object :View.OnFocusChangeListener{
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if(hasFocus) {
                    if(my_weight_txtB.text.isEmpty()) {
                        Toast.makeText(this@GoalFragment.context, "체중을 입력하여 주세요", Toast.LENGTH_SHORT).show()
                        my_bmi_txtB.setFocusable(false)
                        my_bmi_txtB.text.clear()
                    }else if(goal_height_txtB.text.isEmpty()){
                        Toast.makeText(this@GoalFragment.context, "신장을 입력하여 주세요", Toast.LENGTH_SHORT).show()
                        my_bmi_txtB.setFocusable(false)
                        my_bmi_txtB.text.clear()
                    }else if (my_weight_txtB.text.isNotEmpty() &&goal_height_txtB.text.isNotEmpty()) {
                            my_bmi_txtB.setText((Math.round(BMICal(goal_height_txtB.text.toString().toDouble() * 0.01, my_weight_txtB.text.toString().toDouble()) * 100) * 0.01).toString())
                            goal_weight_musclemass_txtB.setText((Math.round(weight_muscleCal(my_weight_txtB.text.toString().toDouble()) * 100) * 0.01).toString())
                    }
                }
            }
            })
        my_musclemass_txtB.setOnFocusChangeListener(object :View.OnFocusChangeListener{
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if(hasFocus) {
                    if(my_weight_txtB.text.isEmpty()) {
                        Toast.makeText(this@GoalFragment.context, "체중을 입력하여 주세요", Toast.LENGTH_SHORT).show()
                        my_musclemass_txtB.setFocusable(false)
                        goal_height_txtB.text.clear()
                    }else if(goal_height_txtB.text.isEmpty()) {
                        Toast.makeText(this@GoalFragment.context, "신장을 입력하여 주세요", Toast.LENGTH_SHORT).show()
                        my_musclemass_txtB.setFocusable(false)
                        goal_height_txtB.text.clear()
                    }
                }
            }
        })
        my_bodyfat_txtB.setOnFocusChangeListener(object :View.OnFocusChangeListener{
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if(hasFocus) {
                    if(my_weight_txtB.text.isEmpty()) {
                        Toast.makeText(this@GoalFragment.context, "체중을 입력하여 주세요", Toast.LENGTH_SHORT).show()
                        my_bodyfat_txtB.setFocusable(false)
                        my_bodyfat_txtB.text.clear()
                    }else if(goal_height_txtB.text.isEmpty()) {
                        Toast.makeText(this@GoalFragment.context, "신장을 입력하여 주세요", Toast.LENGTH_SHORT).show()
                        my_bodyfat_txtB.setFocusable(false)
                        my_bodyfat_txtB.text.clear()
                    }
                }
            }
        })
    }
    fun BMICal(height:Double, weight:Double):Double{
        return weight/(height*height)
    }
    fun muscleCal(weight:Double):Double{
        return (weight*muslceD)
    }
    fun weight_muscleCal(weight:Double):Double{
        return (weight*muslceD)
    }
    fun weightCal(height:Double):Double{
        var result = 0.0
        if(man_RBtn.isChecked) {
            result = height*height*22
        }else{
            result = height*height*21
        }
        return result
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
    interface OnGoalInteractionListener {
        // TODO: Update argument type and name
        fun OnGoalInteractionListener()
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
         * @return A new instance of fragment DashboardFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): GoalFragment {
            val fragment = GoalFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
