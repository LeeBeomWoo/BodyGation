package bodygate.bcns.bodygation.navigationitem

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.CheckableImageButton
import android.support.v4.app.Fragment
import android.util.ArrayMap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import bodygate.bcns.bodygation.R
import bodygate.bcns.bodygation.youtube.Search
import kotlinx.android.synthetic.main.select_view.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FollowFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FollowFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FollowFragment : Fragment(), View.OnClickListener {
    val TAG = "FollowFragment"
    @SuppressLint("RestrictedApi")
    override fun onClick(p0: View?) {
        when (p0!!.id){
            R.id.search_Btn -> consume {
                Log.i(TAG, "search_Btn")
                search()}
            R.id.stretching_img -> consume {
                Log.i(TAG, "stretching_img")
                stretching_img.setImageState(intArrayOf(id), true)
                if(stretching_img.isSelected){
                    stretching_img.isSelected = false
                } else{
                    muscle_img.setImageState(intArrayOf(id), false)
                    stretching_img.isSelected = true
                }
            }
            R.id.muscle_img -> consume {
                Log.i(TAG, "muscle_img")
                if(muscle_img.isSelected){
                    muscle_img.setImageState(intArrayOf(id), false)
                } else{
                    muscle_img.setImageState(intArrayOf(id), true)
                    stretching_img.setImageState(intArrayOf(id), false)
                } }
            R.id.home_img -> consume {
                Log.i(TAG, "home_img")
                if(home_img.isSelected){
                    home_img.isSelected = false
                } else{
                    home_img.isSelected = true
                    work_img.isSelected = false
                    car_img.isSelected = false
                }}
            R.id.car_img -> consume {
                Log.i(TAG, "car_img")
                if(car_img.isSelected){
                    car_img.isSelected = false
                } else{
                    car_img.isSelected = true
                    home_img.isSelected = false
                    work_img.isSelected = false
                }}
            R.id.work_img -> consume {
                Log.i(TAG, "work_img")
                if(work_img.isSelected){
                    work_img.isSelected = false
                } else{
                    work_img.isSelected = true
                    home_img.isSelected = false
                    car_img.isSelected = false
                }}
        }
    }
    inline fun consume(f: () -> Unit): Boolean {
        f()
        return true
    }
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFollowInteraction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.select_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        search_Btn.isClickable = true
        home_img.setImageState()
        car_img.isClickable = true
        work_img.isClickable = true
        hip_img.isClickable = true
        upback_body_img.isClickable = true
        upfront_abs_img.isClickable = true
        upleg_img.isClickable = true
        upperarm_img.isClickable = true
        lowerleg_img.isClickable = true
        shoulder_img.isClickable = true
        chest_img.isClickable = true
        muscle_img.isClickable = true
        stretching_img.isClickable = true
        search_Btn.setOnClickListener(this)
        home_img.setOnClickListener(this)
        car_img.setOnClickListener(this)
        work_img.setOnClickListener(this)
        hip_img.setOnClickListener(this)
        upback_body_img.setOnClickListener(this)
        upfront_abs_img.setOnClickListener(this)
        upleg_img.setOnClickListener(this)
        upperarm_img.setOnClickListener(this)
        lowerleg_img.setOnClickListener(this)
        shoulder_img.setOnClickListener(this)
        chest_img.setOnClickListener(this)
        muscle_img.setOnClickListener(this)
        stretching_img.setOnClickListener(this)
    }
    fun search(): ArrayList<ArrayMap<String, String>> {
        val query:Array<String> = Array<String>(37) { "it = \$it" }
        /*
        if(stretching_img.isSelected){
            query.set(0, "스트레칭 ")
            query.set(1, "요가 ")
            query.set(2, "만들기 ")
        }
        if(muscle_img.isSelected){
            query.set(0, "운동 ")
            query.set(1, "강화 ")
            query.set(2, "만들기 ")
        }
        if(hip_img.isSelected){
            query.set(3, "엉덩이 ")
            query.set(4, "힙업 ")
            query.set(5, "골반 ")
        }
        if(upperarm_img.isSelected){
            query.set(7, "팔")
            query.set(8, "이두근 ")
            query.set(9, "삼두근 ")
            query.set(7, "이두박근 ")
            query.set(8, "알통 ")
            query.set(9, "전완근 ")
            query.set(10, "손목 ")
        }
        if(lowerleg_img.isSelected){
            query.set(11, "종아리 ")
            query.set(12, "하퇴근 ")
            query.set(13, "발목 ")
        }
        if(upleg_img.isSelected){
            query.set(14, "허벅지강화 ")
            query.set(15, "햄스트리밍 ")
            query.set(16, "대퇴사두근 ")
        }
        if(shoulder_img.isSelected){
            query.set(17, "어깨 ")
            query.set(18, "승모근 ")
            query.set(19, "삼각근 ")
            query.set(20, "전면삼각근 ")
            query.set(21, "후면삼각근 ")
            query.set(22, "측면삼각근 ")
        }
        if(chest_img.isSelected){
            query.set(23, "가슴 ")
            query.set(24, "대흉근 ")
            query.set(25, "가슴키우기 ")
        }
        if(upback_body_img.isSelected){
            query.set(26, "허리 ")
            query.set(27, "척추 ")
            query.set(28, "척추기립근 ")
        }
        if(upfront_abs_img.isSelected){
            query.set(29, "복근 ")
            query.set(30, "복부 ")
            query.set(31, "왕자 ")
            query.set(32, "식스팩 ")
        }
        if(work_img.isSelected){
            query.set(33, "직장에서")
            query.set(34, "사무실에서")
        }
        if(home_img.isSelected){
            query.set(33, "집에서")
            query.set(34, "홈")
            query.set(35, "가정에서")
            query.set(36, "실내에서")
        }
        if(car_img.isSelected){
            query.set(33, "차에서 하는")
            query.set(34, "차안에서 하는")
            query.set(35, "차량안에서")
        }*/
        return Search.main(query)
    }
    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.OnFollowInteraction(uri)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFollowInteraction) {
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
    interface OnFollowInteraction {
        // TODO: Update argument type and name
        fun OnFollowInteraction(uri: Uri)
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
         * @return A new instance of fragment FollowFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): FollowFragment {
            val fragment = FollowFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
