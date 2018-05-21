package bodygate.bcns.bodygation.navigationitem

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bodygate.bcns.bodygation.R
import kotlinx.android.synthetic.main.select_view.*
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FollowFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FollowFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FollowFragment : Fragment(), View.OnClickListener, bodygate.bcns.bodygation.support.CheckableImageButton.OnCheckedChangeListener {
    override fun onCheckedChanged(button: bodygate.bcns.bodygation.support.CheckableImageButton?, check: Boolean) {
        when (button!!.id) {
            R.id.stretching_img -> consume {
                Log.i(TAG, "stretching_img")
                stretching_img.setChecked(check)
                if(muscle_img.isChecked)
                muscle_img.setChecked(!check)
            }
            R.id.muscle_img -> consume {
                Log.i(TAG, "muscle_img")
                muscle_img.setChecked(check)
                if(stretching_img.isChecked)
                stretching_img.setChecked(!check)
            }
            R.id.home_img -> consume {
                Log.i(TAG, "home_img")
                home_img.setChecked(check)
                if(car_img.isChecked)
                car_img.setChecked(!check)
                if(work_img.isChecked)
                work_img.setChecked(!check)
            }
            R.id.car_img -> consume {
                Log.i(TAG, "car_img")
                car_img.setChecked(check)
                if(home_img.isChecked)
                home_img.setChecked(!check)
                if(work_img.isChecked)
                work_img.setChecked(!check)
            }
            R.id.work_img -> consume {
                Log.i(TAG, "work_img")
                work_img.setChecked(check)
                if(home_img.isChecked)
                home_img.setChecked(!check)
                if(car_img.isChecked)
                car_img.setChecked(!check)
            }
            R.id.hip_img -> consume {
                Log.i(TAG, "work_img")
                hip_img.setChecked(check)
                if(chest_img.isChecked)
                chest_img.setChecked(!check)
                if(shoulder_img.isChecked)
                shoulder_img.setChecked(!check)
                if(upback_body_img.isChecked)
                upback_body_img.setChecked(!check)
                if(upfront_abs_img.isChecked)
                upfront_abs_img.setChecked(!check)
                if(upleg_img.isChecked)
                upleg_img.setChecked(!check)
                if(upperarm_img.isChecked)
                upperarm_img.setChecked(!check)
                if(lowerleg_img.isChecked)
                lowerleg_img.setChecked(!check)
            }
            R.id.chest_img -> consume {
                Log.i(TAG, "chest_img")
                if(hip_img.isChecked)
                hip_img.setChecked(!check)
                chest_img.setChecked(check)
                if(shoulder_img.isChecked)
                shoulder_img.setChecked(!check)
                if(upback_body_img.isChecked)
                upback_body_img.setChecked(!check)
                if(upfront_abs_img.isChecked)
                upfront_abs_img.setChecked(!check)
                if(upleg_img.isChecked)
                upleg_img.setChecked(!check)
                if(upperarm_img.isChecked)
                upperarm_img.setChecked(!check)
                if(lowerleg_img.isChecked)
                lowerleg_img.setChecked(!check)
            }
            R.id.shoulder_img -> consume {
                Log.i(TAG, "shoulder_img")
                if(hip_img.isChecked)
                hip_img.setChecked(!check)
                if(chest_img.isChecked)
                chest_img.setChecked(!check)
                shoulder_img.setChecked(check)
                if(upback_body_img.isChecked)
                upback_body_img.setChecked(!check)
                if(upfront_abs_img.isChecked)
                upfront_abs_img.setChecked(!check)
                if(upleg_img.isChecked)
                upleg_img.setChecked(!check)
                if(upperarm_img.isChecked)
                upperarm_img.setChecked(!check)
                if(lowerleg_img.isChecked)
                lowerleg_img.setChecked(!check)
            }
            R.id.upback_body_img -> consume {
                Log.i(TAG, "upback_body_img")
                if(hip_img.isChecked)
                hip_img.setChecked(!check)
                if(chest_img.isChecked)
                chest_img.setChecked(!check)
                if(shoulder_img.isChecked)
                shoulder_img.setChecked(!check)
                upback_body_img.setChecked(check)
                if(upfront_abs_img.isChecked)
                upfront_abs_img.setChecked(!check)
                if(upleg_img.isChecked)
                upleg_img.setChecked(!check)
                if(upperarm_img.isChecked)
                upperarm_img.setChecked(!check)
                if(lowerleg_img.isChecked)
                lowerleg_img.setChecked(!check)
            }
            R.id.upfront_abs_img -> consume {
                Log.i(TAG, "upfront_abs_img")
                if(hip_img.isChecked)
                    hip_img.setChecked(!check)
                if(chest_img.isChecked)
                    chest_img.setChecked(!check)
                if(shoulder_img.isChecked)
                    shoulder_img.setChecked(!check)
                if(upback_body_img.isChecked)
                upback_body_img.setChecked(!check)
                upfront_abs_img.setChecked(check)
                if(upleg_img.isChecked)
                    upleg_img.setChecked(!check)
                if(upperarm_img.isChecked)
                    upperarm_img.setChecked(!check)
                if(lowerleg_img.isChecked)
                    lowerleg_img.setChecked(!check)
            }
            R.id.upleg_img -> consume {
                Log.i(TAG, "upleg_img")
                if(hip_img.isChecked)
                    hip_img.setChecked(!check)
                if(chest_img.isChecked)
                    chest_img.setChecked(!check)
                if(shoulder_img.isChecked)
                    shoulder_img.setChecked(!check)
                if(upback_body_img.isChecked)
                    upback_body_img.setChecked(!check)
                if(upfront_abs_img.isChecked)
                upfront_abs_img.setChecked(!check)
                upleg_img.setChecked(check)
                if(upperarm_img.isChecked)
                    upperarm_img.setChecked(!check)
                if(lowerleg_img.isChecked)
                    lowerleg_img.setChecked(!check)
            }
            R.id.upperarm_img -> consume {
                Log.i(TAG, "upperarm_img")
                if(hip_img.isChecked)
                    hip_img.setChecked(!check)
                if(chest_img.isChecked)
                    chest_img.setChecked(!check)
                if(shoulder_img.isChecked)
                    shoulder_img.setChecked(!check)
                if(upback_body_img.isChecked)
                    upback_body_img.setChecked(!check)
                if(upfront_abs_img.isChecked)
                    upfront_abs_img.setChecked(!check)
                if(upleg_img.isChecked)
                    upleg_img.setChecked(!check)
                upperarm_img.setChecked(check)
                if(lowerleg_img.isChecked)
                    lowerleg_img.setChecked(!check)
            }
            R.id.lowerleg_img -> consume {
                Log.i(TAG, "lowerleg_img")
                if(hip_img.isChecked)
                    hip_img.setChecked(!check)
                if(chest_img.isChecked)
                    chest_img.setChecked(!check)
                if(shoulder_img.isChecked)
                    shoulder_img.setChecked(!check)
                if(upback_body_img.isChecked)
                    upback_body_img.setChecked(!check)
                if(upfront_abs_img.isChecked)
                    upfront_abs_img.setChecked(!check)
                if(upleg_img.isChecked)
                    upleg_img.setChecked(!check)
                if(upperarm_img.isChecked)
                    upperarm_img.setChecked(!check)
                lowerleg_img.setChecked(check)
            }
        }
    }

    val TAG = "FollowFragment"
    @SuppressLint("RestrictedApi")
    override fun onClick(p0: View?) {
        when (p0!!.id){
            R.id.search_Btn -> consume {
                Log.i(TAG, "search_Btn")
                onButtonPressed(search())
            }
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
        search_Btn.setOnClickListener(this)
        home_img.setOnCheckedChangeListener(this)
        car_img.setOnCheckedChangeListener(this)
        work_img.setOnCheckedChangeListener(this)
        hip_img.setOnCheckedChangeListener(this)
        upback_body_img.setOnCheckedChangeListener(this)
        upfront_abs_img.setOnCheckedChangeListener(this)
        upleg_img.setOnCheckedChangeListener(this)
        upperarm_img.setOnCheckedChangeListener(this)
        lowerleg_img.setOnCheckedChangeListener(this)
        shoulder_img.setOnCheckedChangeListener(this)
        chest_img.setOnCheckedChangeListener(this)
        muscle_img.setOnCheckedChangeListener(this)
        stretching_img.setOnCheckedChangeListener(this)
        mListener!!.visableFragment = TAG
    }
    fun search(): ArrayList<String> {
        val query:MutableList<String> = ArrayList()
        if(home_img.isChecked){
            query.add("집")
            query.add("홈")
        }
        if(car_img.isChecked){
            query.add("차")
        }
        if(work_img.isChecked){
            query.add("직장")
        }
        if(hip_img.isChecked){
            query.add("엉덩이")
            query.add("힙업")
            query.add("골반")
        }
        if(upperarm_img.isChecked){
            query.add("팔")
            query.add("상완")
           query.add("전완")
            query.add("손목")
        }
        if(lowerleg_img.isChecked){
            query.add("종아리")
            query.add("하퇴근")
            query.add("발목")
        }
        if(upleg_img.isChecked){
            query.add("허벅지")
            query.add("대퇴근")
        }
        if(shoulder_img.isChecked){
            query.add("어깨")
            query.add("승모근")
            query.add("삼각근")
        }
        if(chest_img.isChecked){
            query.add("가슴")
            query.add("대흉근")
        }
        if(upback_body_img.isChecked){
            query.add("허리")
            query.add("척추")
            query.add("기립근")
        }
        if(upfront_abs_img.isChecked){
            query.add("복근")
            query.add("왕자")
            query.add("배")
        }
        if(stretching_img.isChecked){
            query.add( "스트레칭")
            query.add("요가")
        }
        if(muscle_img.isChecked){
           query.add("강화")
            query.add("만들기")
        }
        return query as ArrayList<String>
    }
    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: ArrayList<String>) = launch{
        if (mListener != null) {
            mListener!!.getDatas("snippet", uri.toString(), getString(R.string.API_key), 5, true, 0)
            mListener!!.OnFollowInteraction()
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
        fun OnFollowInteraction()
        var visableFragment:String
        suspend fun getDatas(part: String, q: String, api_Key: String, max_result: Int, more:Boolean, section:Int)
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
