package bodygate.bcns.bodygation.navigationitem

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bodygate.bcns.bodygation.R
import bodygate.bcns.bodygation.youtube.Topics
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_follow.*
import javax.security.auth.Subject


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
    val topic = Topics()
    /** Global instance of the max number of videos we want returned (50 = upper limit per page).  */

    /** Global instance of Youtube object to make all API requests.  */
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.i(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_follow, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.i(TAG, "onActivityCreated")
        // Set the adapter
        val pop_linearLayoutManager = LinearLayoutManager(context)
        pop_linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL)
        result_list.setLayoutManager(pop_linearLayoutManager)
        result_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = pop_linearLayoutManager.getChildCount()
                val totalItemCount = pop_linearLayoutManager.getItemCount()
                val pastVisibleItems = pop_linearLayoutManager.findFirstVisibleItemPosition()
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    //End of list
                    mListener!!.getDatas("snippet", "가슴 어깨 허리 복근 등 허벅지 종아리 팔 목 엉덩이", getString(R.string.API_key), 40, true, mListener!!.getpage(), 1)
                }
            }
        })
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

    override fun onStart() {
        super.onStart()

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
        fun OnYoutubeResultInteraction()
        fun moveBack(q:Fragment)
        fun getDatas(part: String, q: String, api_Key: String, max_result: Int, more:Boolean,  page: String?, section:Int)
        fun getpage():String
    }

    companion object {
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
        fun newInstance(): YouTubeResult {
            val fragment = YouTubeResult()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
