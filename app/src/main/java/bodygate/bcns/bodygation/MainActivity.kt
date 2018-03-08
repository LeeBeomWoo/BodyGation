package bodygate.bcns.bodygation

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import bodygate.bcns.bodygation.navigationitem.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity() : AppCompatActivity(), GoalFragment.OnGoalInteractionListener, FollowFragment.OnFollowInteraction,
                            ForMeFragment.OnForMeInteraction, HomeFragment.OnHomeInteraction{
    override fun OnGoalInteractionListener(uri: Uri) {
        //TODO("not implemented") To change body of created functions use File | Settings | File Templates.
    }

    override fun OnFollowInteraction(uri: Uri) {
       // TODO("not implemented") To change body of created functions use File | Settings | File Templates.
    }

    override fun OnForMeInteraction(uri: Uri) {
       // TODO("not implemented") To change body of created functions use File | Settings | File Templates.
    }

    override fun OnHomeInteraction(uri: Uri) {
      //  TODO("not implemented") To change body of created functions use File | Settings | File Templates.
    }

    val ID: String? = null
    val PW: String? = null
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            //해당 페이지로 이동
            R.id.navigation_goal -> {
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.root_layout, GoalFragment.newInstance(ID, PW), "rageComicList")
                        .commit()
                toolbar.setTitle(R.string.)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_home -> {
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.root_layout, HomeFragment.newInstance(), "rageComicList")
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_follow -> {
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.root_layout, FollowFragment.newInstance(ID, PW), "rageComicList")
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_infome -> {
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.root_layout, ForMeFragment.newInstance(ID, PW), "rageComicList")
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.root_layout, HomeFragment.newInstance(), "rageComicList")
                    .commit()
        }else {
        }
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

}

