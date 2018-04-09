package bodygate.bcns.bodygation.navigationitem

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bodygate.bcns.bodygation.MyRecyclerViewAdapter
import bodygate.bcns.bodygation.R
import bodygate.bcns.bodygation.YoutubeResultListViewAdapter
import bodygate.bcns.bodygation.dummy.DummyContent
import bodygate.bcns.bodygation.youtube.YoutubeResponse
import kotlinx.android.synthetic.main.fragment_movie.*
import kotlinx.android.synthetic.main.fragment_movie.view.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MovieFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnMovieInteraction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        // Set the adapter
        return inflater.inflate(R.layout.fragment_movie, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mListener!!.getDatas("snippet", "가슴 어깨 허리 복근 등 허벅지 종아리 팔 목 엉덩이", getString(R.string.API_key), 40, true,  1)//새로운 영상
        mListener!!.getDatas("snippet", "가슴 어깨 허리 복근 등 허벅지 종아리 팔 목 엉덩이", getString(R.string.API_key), 40, true, 2)//인기있는 영상
        mListener!!.getDatas("snippet", "가슴 어깨 허리 복근 등 허벅지 종아리 팔 목 엉덩이", getString(R.string.API_key), 40, true, 3)//내가 봐온 영상
        val pop_linearLayoutManager = LinearLayoutManager(context)
        pop_linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        pop_list.setLayoutManager(pop_linearLayoutManager)
        val new_linearLayoutManager = LinearLayoutManager(context)
        new_linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        new_list.setLayoutManager(new_linearLayoutManager)
        val my_linearLayoutManager = LinearLayoutManager(context)
        my_linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        my_list.setLayoutManager(my_linearLayoutManager)
    }
    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(item: DummyContent.DummyItem) {
        if (mListener != null) {
            mListener!!.OnMovieInteraction(item)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnMovieInteraction) {
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
    interface OnMovieInteraction {
        // TODO: Update argument type and name
        fun OnMovieInteraction(item: DummyContent.DummyItem)
        fun getDatas(part: String, q: String, api_Key: String, max_result: Int, more:Boolean, section:Int)
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
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(): MovieFragment {
            val fragment = MovieFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
