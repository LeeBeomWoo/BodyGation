package bodygate.bcns.bodygation.navigationitem


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bodygate.bcns.bodygation.R
import bodygate.bcns.bodygation.support.MainPageAdapter
import kotlinx.android.synthetic.main.maintablayout.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainTabFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MainTabFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: Fragment? = null
    private var param2: Fragment? = null

    private var mListener: mainTab? = null
    val list:MutableList<Fragment> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_tab, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainTabFragment.mainTab) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.adapter = mListener!!.tabadapter!!
    }
    interface mainTab{
        var tabadapter:MainPageAdapter?
        var followFragment:FollowFragment?
        var forMeFragment:ForMeFragment?
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainTabFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
                MainTabFragment()
    }
}
