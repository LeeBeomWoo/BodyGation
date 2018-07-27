package bodygate.bcns.bodygation.navigationitem

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bodygate.bcns.bodygation.DataClass
import bodygate.bcns.bodygation.MyRecyclerViewAdapter
import bodygate.bcns.bodygation.R
import bodygate.bcns.bodygation.dummy.DummyContent
import bodygate.bcns.bodygation.dummy.listContent
import kotlinx.android.synthetic.main.fragment_follow.*
import kotlinx.android.synthetic.main.fragment_show_me.*
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "img"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ShowMeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ShowMeFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ShowMeFragment : Fragment(){

    private var param2: String? = null
    var section = 0
    var weight_position = 0
    var muscle_position = 0
    var fat_position = 0
    var bmr_position = 0
    var walk_position = 0
    var bmi_position = 0
    lateinit var mParam1: DataClass
    val listDM:MutableList<DummyContent.DummyItem> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mParam1 = it.getParcelable(ARG_PARAM1)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setdummy()
        val pop_linearLayoutManager = LinearLayoutManager(context)
        pop_linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL)
        date_list.layoutManager = pop_linearLayoutManager
        val adapter = MyRecyclerViewAdapter(listDM) { s: String ->
            searchData(s)
        }
        date_list.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
    fun searchData(s: String){
        if(mParam1.bmi_Label.contains(s)){
            value_bmi.text =  (mParam1.bmi_series[mParam1.bmi_Label.binarySearch(s, 0, mParam1.bmi_series.size)].toString() + "Kg/" + "m\u00B2")
        }else{
            value_bmi.text = "BMI 측정 기록을 업로드 해주신 날이 아닌네요"
        }
        if(mParam1.fat_Label.contains(s)){
            value_fat.text =  (mParam1.fat_series[mParam1.fat_Label.binarySearch(s, 0, mParam1.fat_series.size)].toString()+ "%")
        }else{
            value_fat.text = "체지방률 측정 기록을 업로드 해주신 날이 아닌네요"
        }
        if(mParam1.weight_Label.contains(s)){
            value_weight.text = ( mParam1.weight_series[mParam1.weight_Label.binarySearch(s, 0, mParam1.weight_series.size)].toString()+ "Kg")
        }else{
            value_weight.text = "체중 측정 기록을 업로드 해주신 날이 아닌네요"
        }
        if(mParam1.muscle_Label.contains(s)){
            value_muscle.text = ( mParam1.muscle_series[mParam1.muscle_Label.binarySearch(s, 0, mParam1.muscle_series.size)].toString() + "Kg")
        }else{
            value_muscle.text = "근육량 측정 기록을 업로드 해주신 날이 아닌네요"
        }
        if(mParam1.kcal_Label.contains(s)){
            value_bmr.text = ( mParam1.kcal_series[mParam1.kcal_Label.binarySearch(s, 0, mParam1.kcal_series.size)].toString()+ "kcal")
        }else{
            value_bmr.text = "오늘은 운동을 하지 않으셨나봐요"
        }
        if(mParam1.walk_Label.contains(s)){
            value_walk.text = ( mParam1.walk_series[mParam1.walk_Label.binarySearch(s, 0, mParam1.walk_series.size)].toString()+ "걸음")
        }else{
            value_walk.text = "오늘은 걷는 기록이 없네요"
        }
    }

    fun setdummy(){
        val cal = Calendar.getInstance()
        for(i:Int in 0..199){
            cal.set(Calendar.DAY_OF_YEAR, -i)
            listDM.add(DummyContent.createDummyItem(cal.timeInMillis))
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_me, container, false)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ShowMeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: DataClass) =
                ShowMeFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_PARAM1, param1)
                    }
                }
    }
}
