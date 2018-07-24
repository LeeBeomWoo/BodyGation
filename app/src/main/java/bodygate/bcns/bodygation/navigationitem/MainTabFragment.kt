package bodygate.bcns.bodygation.navigationitem

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.support.v4.app.FragmentTabHost
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import bodygate.bcns.bodygation.R
import bodygate.bcns.bodygation.dummy.DataClass
import bodygate.bcns.bodygation.support.MainPageAdapter
import kotlinx.android.synthetic.main.maintablayout.*

class MainTabFragment: Fragment(){
    val TAG = "MainTabFragment"
    private var mParam1: DataClass? = null
    private var mListener: mainTab? = null
    val list:MutableList<Fragment> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
        if (arguments != null) {
            mParam1 = arguments!!.getSerializable(ARG_PARAM1) as DataClass
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated")
        if(mListener!!.tabadapter == null){
            mListener!!.tabadapter = MainPageAdapter(requireFragmentManager())
        }
        if (requireFragmentManager().findFragmentByTag("forme") == null) {
            mListener!!.forMeFragment = ForMeFragment.newInstance(mParam1)
        }
        if (requireFragmentManager().findFragmentByTag("follow") == null) {
            mListener!!.followFragment = FollowFragment.newInstance()
        }
        mListener!!.tabadapter!!.addFragment(R.drawable.select_formemenu, getString(R.string.title_infome), mListener!!.forMeFragment!!)
        mListener!!.tabadapter!!.addFragment(R.drawable.select_followmenu, getString(R.string.follow_media), mListener!!.followFragment!!)
        viewPager.adapter = mListener!!.tabadapter!!
        tabs.setupWithViewPager(viewPager)
        for (i in 0 until viewPager.getAdapter()!!.getCount()) {
            tabs.getTabAt(i)!!.setIcon(mListener!!.tabadapter!!.getFragmentInfo(i).iconResId)
        }
        viewPager.currentItem = 1
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainTabFragment.mainTab) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i(TAG, "onCreateView")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.maintablayout, container, false)
    }
    interface mainTab{
        var tabadapter: MainPageAdapter?
        var followFragment:Fragment?
        var forMeFragment: Fragment?
    }
    companion object {
        private val ARG_PARAM1 = "img"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainTabFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: DataClass?): MainTabFragment {
            val fragment = MainTabFragment()
            val args = Bundle()
            args.putSerializable(ARG_PARAM1, param1)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onDetach() {
        super.onDetach()
        mListener = null
    }
}