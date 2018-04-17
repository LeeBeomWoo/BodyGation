package bodygate.bcns.bodygation.navigationitem

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import bodygate.bcns.bodygation.R
import com.google.android.gms.fitness.result.DataSourcesResult
import kotlinx.android.synthetic.main.fragment_goal.*


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
    val onlymuscleD = 0.577
    val girlfatD = 1.07
    val manfatD = 1.1
    val fatD = 128.0

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
    fun getBMI(){
       my_bmi_txtB.setText((my_weight_txtB.text.toString().toDouble()/(goal_height_txtB.text.toString().toDouble() * 0.01)*(goal_height_txtB.text.toString().toDouble() * 0.01)).toString())
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i(TAG, "onCreateView")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_goal, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.OnGoalInteractionListener(uri)
        }
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
        goal_height_txtB.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                goal_weight_txtB.setText(weightCal(s.toString().toDouble()*0.01).toString())
                goal_bmi_txtB.setText(BMICal(s.toString().toDouble()*0.01, goal_weight_txtB.text.toString().toDouble()).toString())
                goal_bodyfat_txtB.setText(bodyfatCal(s.toString().toDouble()*0.01, goal_weight_txtB.text.toString().toDouble()).toString())
                goal_musclemass_txtB.setText(muscleCal(goal_weight_txtB.text.toString().toDouble()).toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.i(TAG, "goal_musclemass_txtB_onTextChanged")
                if(!man_RBtn.isChecked && !girl_RBtn.isChecked){
                    Toast.makeText(this@GoalFragment.requireContext(), "성별을 먼저 선택하여 주세요", Toast.LENGTH_SHORT).show()
                    goal_height_txtB.setText("")
                    Log.i(TAG, "if_txtB_onTextChanged")
                }
            }

        })
        my_weight_txtB.setOnEditorActionListener(object :View.Ed){
            override fun afterTextChanged(s: Editable?) {
                my_bmi_txtB.setText(BMICal(goal_height_txtB.text.toString().toDouble()*0.01, my_weight_txtB.text.toString().toDouble()).toString())
                my_bodyfat_txtB.setText(bodyfatCal(goal_height_txtB.text.toString().toDouble()*0.01, my_weight_txtB.text.toString().toDouble()).toString())
                my_musclemass_txtB.setText(muscleCal(my_weight_txtB.text.toString().toDouble()).toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.i(TAG, "my_weight_txtB_txtB_onTextChanged")
                if(!man_RBtn.isChecked && !girl_RBtn.isChecked){
                    Toast.makeText(this@GoalFragment.requireContext(), "성별을 먼저 선택하여 주세요", Toast.LENGTH_SHORT).show()
                    goal_height_txtB.setText("")
                }
            }

        })
    }
    fun bodyfatCal(height:Double, weight:Double):Double{
        var result = 0.0
        if(man_RBtn.isChecked){
            result = (manfatD*weight)-((fatD*(weight*weight))/(height*height))
        }else{
            result = (girlfatD*weight)-((fatD*(weight*weight))/(height*height))
        }
        return result
    }
    fun BMICal(height:Double, weight:Double):Double{
        return weight/(height*height)
    }
    fun muscleCal(weight:Double):Double{
        return (weight*muslceD)*onlymuscleD
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
        fun OnGoalInteractionListener(uri: Uri)

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
