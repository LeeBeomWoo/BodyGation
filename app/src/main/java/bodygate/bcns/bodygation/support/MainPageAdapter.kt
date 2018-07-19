package bodygate.bcns.bodygation.support

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.google.android.gms.common.api.internal.LifecycleCallback.getFragment
import android.support.v4.app.FragmentPagerAdapter


class MainPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val mFragmentInfoList = ArrayList<FragmentInfo>()

    fun addFragment(iconResId: Int, title: String, fragment: Fragment) {
        val info = FragmentInfo(iconResId, title, fragment)
        mFragmentInfoList.add(info)
    }

    fun getFragmentInfo(position: Int): FragmentInfo {
        return mFragmentInfoList[position]
    }

    override fun getItem(position: Int): Fragment {
        return mFragmentInfoList[position].fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentInfoList[position].titleText
    }

    override fun getCount(): Int {
        return mFragmentInfoList.size
    }
}
class FragmentInfo(iconResId:Int, text:String, fragment:Fragment) {

    var iconResId:Int = 0
        get() = field
    var titleText:String
        get() = field
    var fragment:Fragment
        get() = field

    init{
        this.iconResId = iconResId
        this.titleText = text
        this.fragment = fragment
    }
}