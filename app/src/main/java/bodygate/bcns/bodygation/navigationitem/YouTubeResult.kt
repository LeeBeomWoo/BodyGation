package bodygate.bcns.bodygation.navigationitem

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.ArrayMap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bodygate.bcns.bodygation.R
import bodygate.bcns.bodygation.YoutubeResultListViewAdapter
import bodygate.bcns.bodygation.dummy.listContent
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import kotlinx.android.synthetic.main.fragment_follow.*
import kotlinx.coroutines.experimental.Deferred
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by LeeBeomWoo on 2018-03-23.
 */

class YouTubeResult : Fragment() {
    // TODO: Rename and change types of parameters
    val TAG = "YouTubeResult_"
    private var mParam1: MutableList<String> = ArrayList<String>()

    private var mListener: OnYoutubeResultInteraction? = null
    private val PROPERTIES_FILENAME = "youtube.properties"

    /** Global instance of the HTTP transport.  */
    private val HTTP_TRANSPORT = NetHttpTransport()

    /** Global instance of the JSON factory.  */
    private val JSON_FACTORY = JacksonFactory()

    /** Global instance of the max number of videos we want returned (50 = upper limit per page).  */

    /** Global instance of Youtube object to make all API requests.  */
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getStringArrayList(ARG_PARAM1)
            Log.i(TAG+ ARG_PARAM1, mParam1[3])
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.i(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_follow, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.i(TAG, "onActivityCreated")
        mListener!!.HTTP_TRANSPORT = HTTP_TRANSPORT
        mListener!!.JSON_FACTORY = JSON_FACTORY
        val pop_adapter = mListener!!.OnYoutubeResultInteraction(mParam1 as ArrayList<String>)
        // Set the adapter
        val pop_linearLayoutManager = LinearLayoutManager(context)
        pop_linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL)
        result_list.setLayoutManager(pop_linearLayoutManager)
        result_list.setAdapter(pop_adapter)
    }
    // TODO: Rename method, update argument and hook method into UI event

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnYoutubeResultInteraction) {
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
    interface OnYoutubeResultInteraction {
        var HTTP_TRANSPORT: HttpTransport?

        var JSON_FACTORY: JsonFactory?

        var query:Array<String>
        // TODO: Update argument type and name
        fun OnYoutubeResultInteraction(item: ArrayList<String>): YoutubeResultListViewAdapter?
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1 : ArrayList<String>): YouTubeResult {
            val fragment = YouTubeResult()
            val args = Bundle()
            args.putStringArrayList(ARG_PARAM1, param1)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
