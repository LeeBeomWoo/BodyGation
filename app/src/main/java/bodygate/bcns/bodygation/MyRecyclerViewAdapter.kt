package bodygate.bcns.bodygation

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import bodygate.bcns.bodygation.dummy.DummyContent

import bodygate.bcns.bodygation.dummy.DummyContent.DummyItem
import bodygate.bcns.bodygation.navigationitem.MovieFragment
import bodygate.bcns.bodygation.navigationitem.ShowMeFragment

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyRecyclerViewAdapter(private val mValues: List<DummyItem>, val itemClick:(String) -> Unit) : RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.textview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues[position]
        holder.mIdView.text = mValues[position].toString()
        holder.mView.setOnClickListener {itemClick(mValues[position].todate())}
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView
        var mItem: DummyItem? = null

        init {
            mIdView = mView.findViewById<View>(R.id.date_text) as TextView
        }

        override fun toString(): String {
            return super.toString()
        }
    }
}
