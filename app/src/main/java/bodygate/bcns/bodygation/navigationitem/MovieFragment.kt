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
import bodygate.bcns.bodygation.dummy.DummyContent
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
        val view = inflater.inflate(R.layout.fragment_movie, container, false)
        val pop_list:RecyclerView = view.findViewById(R.id.pop_list)
        val new_list:RecyclerView = view.findViewById(R.id.new_list)
        val my_list:RecyclerView = view.findViewById(R.id.my_list)
        val pop_adapter = MyRecyclerViewAdapter(DummyContent.ITEMS, mListener)
        val new_adapter = MyRecyclerViewAdapter(DummyContent.ITEMS, mListener)
        val my_adapter = MyRecyclerViewAdapter(DummyContent.ITEMS, mListener)

        // Set the adapter
        val pop_linearLayoutManager = LinearLayoutManager(context)
        pop_linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        val new_linearLayoutManager = LinearLayoutManager(context)
        new_linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        val my_linearLayoutManager = LinearLayoutManager(context)
        my_linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        pop_list.layoutManager = pop_linearLayoutManager
        pop_list.adapter = pop_adapter
        new_list.layoutManager = new_linearLayoutManager
        new_list.adapter = new_adapter
        my_list.layoutManager = my_linearLayoutManager
        my_list.adapter = my_adapter
        return view
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