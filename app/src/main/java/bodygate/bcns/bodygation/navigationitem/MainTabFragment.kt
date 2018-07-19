package bodygate.bcns.bodygation.navigationitem

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.support.v4.app.FragmentTabHost
import bodygate.bcns.bodygation.R
import bodygate.bcns.bodygation.support.MainPageAdapter


class MainTabFragment: Fragment(){
    private var mListener: mainTab? = null
    val list:MutableList<Fragment> = ArrayList()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mListener!!.tabadapter!!.addFragment(R.drawable.select_followmenu, getString(R.string.follow_media), mListener!!.forMeFragment!!)
        mListener!!.tabadapter!!.addFragment(R.drawable.select_followmenu, getString(R.string.follow_media), mListener!!.forMeFragment!!)
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