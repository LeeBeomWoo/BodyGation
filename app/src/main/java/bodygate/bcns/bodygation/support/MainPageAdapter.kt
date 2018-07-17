package bodygate.bcns.bodygation.support

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class MainPageAdapter(fragmentManager: FragmentManager, fragmentList:List<Fragment>) :
        FragmentStatePagerAdapter(fragmentManager) {
val fargments = fragmentList
    // 2
    override fun getItem(position: Int): Fragment {
        return fargments[position]
    }

    // 3
    override fun getCount(): Int {
        return count
    }
}