package bodygate.bcns.bodygation.navigationitem

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.support.v4.app.FragmentTabHost
import android.util.Log
import bodygate.bcns.bodygation.R
import bodygate.bcns.bodygation.support.MainPageAdapter
import kotlinx.android.synthetic.main.maintablayout.*

class MainTabFragment: Fragment(){
    private var mListener: mainTab? = null
    val list:MutableList<Fragment> = ArrayList()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("mainTabFragment", "onViewCreated")
        if(mListener!!.tabadapter == null){
            mListener!!.tabadapter = MainPageAdapter(requireFragmentManager())
        }
        mListener!!.tabadapter!!.addFragment(R.drawable.select_formemenu, getString(R.string.title_infome), mListener!!.forMeFragment!!)
        mListener!!.tabadapter!!.addFragment(R.drawable.select_followmenu, getString(R.string.follow_media), mListener!!.followFragment!!)
        viewPager.adapter = mListener!!.tabadapter!!
        tabs.setupWithViewPager(viewPager)
        for (i in 0 until viewPager.getAdapter()!!.getCount()) {
            tabs.getTabAt(i)!!.setIcon(mListener!!.tabadapter!!.getFragmentInfo(i).iconResId)
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainTabFragment.mainTab) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    interface mainTab{
        var tabadapter: MainPageAdapter?
        var followFragment:Fragment?
        var forMeFragment: Fragment?
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
    override fun onDetach() {
        super.onDetach()
        mListener = null
    }
}