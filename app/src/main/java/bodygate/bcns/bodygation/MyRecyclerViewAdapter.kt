package bodygate.bcns.bodygation

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import bodygate.bcns.bodygation.dummy.DummyContent.DummyItem
import bodygate.bcns.bodygation.navigationitem.MovieFragment

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyRecyclerViewAdapter(private val mValues: List<DummyItem>, private val mListener: MovieFragment.OnMovieInteraction?) : RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues[position]
        holder.mIdView.text = mValues[position].id
        holder.mView.setOnClickListener {
            mListener?.OnMovieInteraction(holder.mItem!!)
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView
        val mContentView: ImageView
        var mItem: DummyItem? = null

        init {
            mIdView = mView.findViewById<View>(R.id.tv_title) as TextView
            mContentView = mView.findViewById<View>(R.id.iv_yt_logo) as ImageView
        }

        override fun toString(): String {
            return super.toString() + " '"
        }
    }
}
