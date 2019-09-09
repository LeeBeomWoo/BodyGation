package bodygate.bcns.bodygation.navigationitem

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import bodygate.bcns.bodygation.R
import bodygate.bcns.bodygation.R.*
import bodygate.bcns.bodygation.support.CheckableImageButton
import kotlinx.android.synthetic.main.select_view.*
import kotlinx.coroutines.runBlocking


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FollowFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FollowFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val ARG_PARAM1 = "img"
class FollowFragment : Fragment(), View.OnClickListener, bodygate.bcns.bodygation.support.CheckableImageButton.OnCheckedChangeListener {
    private lateinit var stretching_img: CheckableImageButton
    private lateinit var muscle_img: CheckableImageButton
    private lateinit var home_img: CheckableImageButton
    private lateinit var car_img: CheckableImageButton
    private lateinit var work_img: CheckableImageButton
    private lateinit var hip_img: CheckableImageButton
    private lateinit var chest_img: CheckableImageButton
    private lateinit var upback_body_img: CheckableImageButton
    private lateinit var shoulder_img: CheckableImageButton
    private lateinit var upfront_abs_img: CheckableImageButton
    private lateinit var upleg_img: CheckableImageButton
    private lateinit var lowerleg_img: CheckableImageButton
    private lateinit var upperarm_img: CheckableImageButton
    private lateinit var search_Btn: Button
    override fun onCheckedChanged(button: bodygate.bcns.bodygation.support.CheckableImageButton?, check: Boolean) {
        when (button!!.id) {
            R.id.stretching_img -> consume {
                Log.i(TAG, "stretching_img")
                stretching_img.isChecked = check
                if(muscle_img.isChecked)
                    muscle_img.isChecked = !check
            }
            R.id.muscle_img -> consume {
                Log.i(TAG, "muscle_img")
                muscle_img.isChecked = check
                if(stretching_img.isChecked)
                    stretching_img.isChecked = !check
            }
            R.id.home_img -> consume {
                Log.i(TAG, "home_img")
                home_img.isChecked = check
                if(car_img.isChecked)
                    car_img.isChecked = !check
                if(work_img.isChecked)
                    work_img.isChecked = !check
            }
            R.id.car_img -> consume {
                Log.i(TAG, "car_img")
                car_img.isChecked = check
                if(home_img.isChecked)
                    home_img.isChecked = !check
                if(work_img.isChecked)
                    work_img.isChecked = !check
            }
            R.id.work_img -> consume {
                Log.i(TAG, "work_img")
                work_img.isChecked = check
                if(home_img.isChecked)
                    home_img.isChecked = !check
                if(car_img.isChecked)
                    car_img.isChecked = !check
            }
            R.id.hip_img -> consume {
                Log.i(TAG, "work_img")
                hip_img.isChecked = check
                if(chest_img.isChecked)
                    chest_img.isChecked = !check
                if(shoulder_img.isChecked)
                    shoulder_img.isChecked = !check
                if(upback_body_img.isChecked)
                    upback_body_img.isChecked = !check
                if(upfront_abs_img.isChecked)
                    upfront_abs_img.isChecked = !check
                if(upleg_img.isChecked)
                    upleg_img.isChecked = !check
                if(upperarm_img.isChecked)
                    upperarm_img.isChecked = !check
                if(lowerleg_img.isChecked)
                    lowerleg_img.isChecked = !check
            }
            R.id.chest_img -> consume {
                Log.i(TAG, "chest_img")
                if(hip_img.isChecked)
                    hip_img.isChecked = !check
                chest_img.isChecked = check
                if(shoulder_img.isChecked)
                    shoulder_img.isChecked = !check
                if(upback_body_img.isChecked)
                    upback_body_img.isChecked = !check
                if(upfront_abs_img.isChecked)
                    upfront_abs_img.isChecked = !check
                if(upleg_img.isChecked)
                    upleg_img.isChecked = !check
                if(upperarm_img.isChecked)
                    upperarm_img.isChecked = !check
                if(lowerleg_img.isChecked)
                    lowerleg_img.isChecked = !check
            }
            R.id.shoulder_img -> consume {
                Log.i(TAG, "shoulder_img")
                if(hip_img.isChecked)
                    hip_img.isChecked = !check
                if(chest_img.isChecked)
                    chest_img.isChecked = !check
                shoulder_img.isChecked = check
                if(upback_body_img.isChecked)
                    upback_body_img.isChecked = !check
                if(upfront_abs_img.isChecked)
                    upfront_abs_img.isChecked = !check
                if(upleg_img.isChecked)
                    upleg_img.isChecked = !check
                if(upperarm_img.isChecked)
                    upperarm_img.isChecked = !check
                if(lowerleg_img.isChecked)
                    lowerleg_img.isChecked = !check
            }
            R.id.upback_body_img -> consume {
                Log.i(TAG, "upback_body_img")
                if(hip_img.isChecked)
                    hip_img.isChecked = !check
                if(chest_img.isChecked)
                    chest_img.isChecked = !check
                if(shoulder_img.isChecked)
                    shoulder_img.isChecked = !check
                upback_body_img.isChecked = check
                if(upfront_abs_img.isChecked)
                    upfront_abs_img.isChecked = !check
                if(upleg_img.isChecked)
                    upleg_img.isChecked = !check
                if(upperarm_img.isChecked)
                    upperarm_img.isChecked = !check
                if(lowerleg_img.isChecked)
                    lowerleg_img.isChecked = !check
            }
            R.id.upfront_abs_img -> consume {
                Log.i(TAG, "upfront_abs_img")
                if(hip_img.isChecked)
                    hip_img.isChecked = !check
                if(chest_img.isChecked)
                    chest_img.isChecked = !check
                if(shoulder_img.isChecked)
                    shoulder_img.isChecked = !check
                if(upback_body_img.isChecked)
                    upback_body_img.isChecked = !check
                upfront_abs_img.isChecked = check
                if(upleg_img.isChecked)
                    upleg_img.isChecked = !check
                if(upperarm_img.isChecked)
                    upperarm_img.isChecked = !check
                if(lowerleg_img.isChecked)
                    lowerleg_img.isChecked = !check
            }
            R.id.upleg_img -> consume {
                Log.i(TAG, "upleg_img")
                if(hip_img.isChecked)
                    hip_img.isChecked = !check
                if(chest_img.isChecked)
                    chest_img.isChecked = !check
                if(shoulder_img.isChecked)
                    shoulder_img.isChecked = !check
                if(upback_body_img.isChecked)
                    upback_body_img.isChecked = !check
                if(upfront_abs_img.isChecked)
                    upfront_abs_img.isChecked = !check
                upleg_img.isChecked = check
                if(upperarm_img.isChecked)
                    upperarm_img.isChecked = !check
                if(lowerleg_img.isChecked)
                    lowerleg_img.isChecked = !check
            }
            R.id.upperarm_img -> consume {
                Log.i(TAG, "upperarm_img")
                if(hip_img.isChecked)
                    hip_img.isChecked = !check
                if(chest_img.isChecked)
                    chest_img.isChecked = !check
                if(shoulder_img.isChecked)
                    shoulder_img.isChecked = !check
                if(upback_body_img.isChecked)
                    upback_body_img.isChecked = !check
                if(upfront_abs_img.isChecked)
                    upfront_abs_img.isChecked = !check
                if(upleg_img.isChecked)
                    upleg_img.isChecked = !check
                upperarm_img.isChecked = check
                if(lowerleg_img.isChecked)
                    lowerleg_img.isChecked = !check
            }
            R.id.lowerleg_img -> consume {
                Log.i(TAG, "lowerleg_img")
                if(hip_img.isChecked)
                    hip_img.isChecked = !check
                if(chest_img.isChecked)
                    chest_img.isChecked = !check
                if(shoulder_img.isChecked)
                    shoulder_img.isChecked = !check
                if(upback_body_img.isChecked)
                    upback_body_img.isChecked = !check
                if(upfront_abs_img.isChecked)
                    upfront_abs_img.isChecked = !check
                if(upleg_img.isChecked)
                    upleg_img.isChecked = !check
                if(upperarm_img.isChecked)
                    upperarm_img.isChecked = !check
                lowerleg_img.isChecked = check
            }
        }
    }

    val TAG = "FollowFragment"
    lateinit var mParam1: String
    @SuppressLint("RestrictedApi")
    override fun onClick(p0: View?) {
        when (p0!!.id){
            R.id.search_Btn -> consume {
                Log.i(TAG, "search_Btn")
                onButtonPressed(search())
                Log.i(TAG, search().toString())
            }
        }
    }
    inline fun consume(f: () -> Unit): Boolean {
        f()
        return true
    }

    private var mListener: OnFollowInteraction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootview = inflater.inflate(layout.select_view, container, false)
        search_Btn = rootview.findViewById(R.id.search_Btn)
        home_img = rootview.findViewById(R.id.home_img)
        car_img = rootview.findViewById(R.id.car_img)
        work_img = rootview.findViewById(R.id.work_img)
        hip_img = rootview.findViewById(R.id.hip_img)
        upback_body_img = rootview.findViewById(R.id.upback_body_img)
        upfront_abs_img = rootview.findViewById(R.id.upfront_abs_img)
        upleg_img = rootview.findViewById(R.id.upleg_img)
        upperarm_img = rootview.findViewById(R.id.upperarm_img)
        lowerleg_img = rootview.findViewById(R.id.lowerleg_img)
        shoulder_img = rootview.findViewById(R.id.shoulder_img)
        chest_img = rootview.findViewById(R.id.chest_img)
        muscle_img = rootview.findViewById(R.id.muscle_img)
        stretching_img = rootview.findViewById(R.id.stretching_img)
        return rootview
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

    fun onButtonPressed(uri: ArrayList<String>)= runBlocking{
        if (mListener != null) {
            if(!muscle_img.isChecked && !stretching_img.isChecked){
                uri.add("운동")
                uri.add("스트레칭")
            }
            mListener!!.getDatas("snippet", uri, getString(string.API_key), 5, true)}
            Log.i("test", "네번째")
            mListener!!.OnFollowInteraction(uri, 0)
            Log.i("test", "여섯번째")
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
        fun OnFollowInteraction(q: ArrayList<String>?, s:Int)
        var visableFragment:String
        suspend fun getDatas(part: String, q: ArrayList<String>, api_Key: String, max_result: Int, more:Boolean)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FollowFragment.
         */
        // TODO: Rename and change types and number of parameters

        fun newInstance() =
                FollowFragment().apply {
                    arguments = Bundle().apply {                                      }
                }
    }
}// Required empty public constructor
